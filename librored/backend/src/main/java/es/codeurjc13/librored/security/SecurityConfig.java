package es.codeurjc13.librored.security;

import es.codeurjc13.librored.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.http.SessionCreationPolicy;

/*import es.codeurjc13.librored.security.jwt.JwtRequestFilter;
import es.codeurjc13.librored.security.jwt.UnauthorizedHandlerJwt;*/

@Configuration
@EnableWebSecurity
public class SecurityConfig {

/*
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;
*/

    @Autowired
    RepositoryUserDetailsService userDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // === API FILTER CHAIN ===
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());
        http
                .securityMatcher("/api/**");
                //.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));


        http
                .authorizeHttpRequests(auth -> auth

                        // Public API endpoints
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/{id}", "/api/books/{id}/cover").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/search", "/api/books/books-per-genre").permitAll()

                        // Private API endpoints
                        // BOOKS
                        .requestMatchers(HttpMethod.POST, "/api/books/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").authenticated()
                        // USERS
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        // LOANS
                        .requestMatchers("/api/loans/**").authenticated()

                        // Everything else under /api/** requires authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("LibroRed API"));

        // Disable Form login Authentication
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(AbstractHttpConfigurer::disable);

        // Disable Basic Authentication
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
/*        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); */
        return http.build();
    }

    // === WEB FILTER CHAIN ===
    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/login",
                                "/register",
                                "/error/**",
                                "/perform_login",
                                "/loginerror").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers("/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/myaccount").authenticated()
                        .requestMatchers("/users/edit/{id}").access((authentication, request) -> {
                            boolean isAdmin = authentication.get().getAuthorities().stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals(User.Role.ROLE_ADMIN.name()));
                            boolean isSelf = authentication.get().getName().equals(request.getRequest().getServletPath().split("/")[3]);
                            return new AuthorizationDecision(isAdmin || isSelf);
                        })
                        .requestMatchers("/books", "/loans", "/loans/**", "/recommendations").authenticated()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/perform_login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        http
                .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
