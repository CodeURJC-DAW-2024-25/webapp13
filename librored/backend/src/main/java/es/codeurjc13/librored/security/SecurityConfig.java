package es.codeurjc13.librored.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
        authProvider.setUserDetailsService(userDetailsService);  // Using email instead of username
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * REST API security (P2):
     * - Applies ONLY to /api/v1/**
     * - Stateless + CSRF off
     * - HTTP Basic
     * - Public reads for books (GET)
     * - Your existing API rules migrated here with /api/v1 prefix
     */

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public reads for books
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()

                        // Existing API rules with /api/v1 prefix
                        .requestMatchers("/api/v1/users/verify-password",
                                "/api/v1/users/update-username",
                                "/api/v1/users/update-password").authenticated()
                        .requestMatchers("/api/v1/loans/valid-borrowers").authenticated()
                        .requestMatchers("/api/v1/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Catch‑all for anything else under /api/v1/**
                        .requestMatchers("/api/v1/**").authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Web MVC security (P1 intact):
     * - Form login
     * - CSRF enabled (cookie)
     * - Public pages + your protected MVC URLs remain here
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());
        //http.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection USE only when developing

        http
                .authorizeHttpRequests(auth -> auth
                        // Public resources (CSS, JS, Images)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Swagger / SpringDoc
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/error/**", "/perform_login", "/loginerror").permitAll()


                        // User dashboard and protected actions (only for authenticated users)
                        .requestMatchers("/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  //  Only users with ROLE_USER or ROLE_ADMIN can access
                        .requestMatchers("/myaccount").authenticated()

                        // Edit user: allow only admin OR self
                        // NOTE: we're matching the path and deciding with a custom Access lambda.
                        // We use the request's servletPath to compare the id segment.
                        .requestMatchers("/users/edit/**").access((authentication, context) -> {
                            boolean isAdmin = authentication.get().getAuthorities().stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
                            String[] parts = context.getRequest().getServletPath().split("/");
                            boolean isSelf = parts.length > 3 && authentication.get().getName().equals(parts[3]);
                            return new AuthorizationDecision(isAdmin || isSelf);
                        })

                        .requestMatchers("/books").authenticated()
                        .requestMatchers("/loans").authenticated()
                        .requestMatchers("/loans/**").authenticated()
                        .requestMatchers("/recommendations").authenticated()

                        // Admin-only pages
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Ensure CSRF token is accessible
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/perform_login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

}
