package es.codeurjc13.librored.security;

import es.codeurjc13.librored.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // ✅ Now using email instead of username
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        //http.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection USE only when developing

        http
                .csrf(csrf -> csrf
                        // TODO: Fix DELETE LOANS
                        // .ignoringRequestMatchers("/loans/delete/**") // ?!?! Explicitly allow DELETE
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Ensure CSRF token is accessible
                        .ignoringRequestMatchers("/api/**")  // Disable CSRF for all API endpoints
                )
                .authorizeHttpRequests(auth -> auth
                        // Public resources (CSS, JS, Images)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/error/**", "/perform_login", "/loginerror").permitAll()

                        // API access: Public endpoints
                        // BOOKS
                        // Allow GET for non-authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/books-per-genre").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/{id}/cover").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        // Allow POST, PUT, DELETE only for authenticated users
                        .requestMatchers(HttpMethod.POST, "/api/books").authenticated() // Require auth for POST
                        .requestMatchers(HttpMethod.PUT, "/api/books").authenticated() // Require auth for POST
                        .requestMatchers(HttpMethod.DELETE, "/api/books").authenticated() // Require auth for POST


                        // API access: Only logged-in users
                        // For any other API endpoint not , require authentication for any other method not expressly written
                        .requestMatchers("/api/**").authenticated() // everything else (POST, DELETE)

                        // USERS
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  //  Only users with ROLE_USER or ROLE_ADMIN can access
                        .requestMatchers("/api/users/verify-password", "/api/users/update-username", "/api/users/update-password").authenticated()
                        // LOANS
                        .requestMatchers("/api/loans/valid-borrowers").authenticated() // Allow only authenticated users


                        //  Protected actions (only for authenticated users)
                        .requestMatchers("/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  //  Only users with ROLE_USER or ROLE_ADMIN can access
                        .requestMatchers("/myaccount").authenticated()
                        .requestMatchers("/users/edit/{id}").access((authentication, request) -> {
                            boolean isAdmin = authentication.get().getAuthorities().stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals(User.Role.ROLE_ADMIN.name()));
                            boolean isSelf = authentication.get().getName().equals(request.getRequest().getServletPath().split("/")[3]);
                            return new AuthorizationDecision(isAdmin || isSelf);
                        })

                        // Templates only for authenticated users
                        .requestMatchers("/books").authenticated()
                        .requestMatchers("/loans").authenticated()
                        .requestMatchers("/loans/**").authenticated()
                        .requestMatchers("/recommendations").authenticated()

                        // Admin-only pages
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/perform_login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        // Basic Authentication
        // Enable this for REST API clients - Postman
        http.httpBasic(httpBasic -> httpBasic.realmName("LibroRed API")); //Add this for REST API clients - Postman

        return http.build();
    }

}
