package es.codeurjc13.librored.security;

import es.codeurjc13.librored.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        authProvider.setUserDetailsService(userDetailsService);  // Now using email instead of username
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        //http.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection USE only when developing

        http

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/**") // Disable CSRF for REST API endpoints
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Ensure CSRF token is accessible
                )
                .authorizeHttpRequests(auth -> auth
                        // Public resources (CSS, JS, Images)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/error/**", "/perform_login", "/loginerror").permitAll()

                        // API access: Public endpoints
                        .requestMatchers("/api/books", "/api/books/books-per-genre", "/api/books/**").permitAll()

                        // REST API endpoints (P2 requirements) - disable CSRF and use basic auth
                        .requestMatchers("/api/v1/**").authenticated()

                        // API access: Only logged-in users
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  //  Only users with ROLE_USER or ROLE_ADMIN can access
                        .requestMatchers("/api/users/verify-password", "/api/users/update-username", "/api/users/update-password").authenticated()
                        .requestMatchers("/api/loans/valid-borrowers").authenticated() // Allow only authenticated users
                        .requestMatchers("/api/**").authenticated()  // Require authentication for all APIs except the ones above


                        // User dashboard and protected actions (only for authenticated users)
                        .requestMatchers("/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  //  Only users with ROLE_USER or ROLE_ADMIN can access
                        .requestMatchers("/myaccount").authenticated()
                        .requestMatchers("/users/edit/{id}").access((authentication, request) -> {
                            boolean isAdmin = authentication.get().getAuthorities().stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals(User.Role.ROLE_ADMIN.name()));

                            boolean isSelf = authentication.get().getName().equals(request.getRequest().getServletPath().split("/")[3]);

                            return new AuthorizationDecision(isAdmin || isSelf);
                        })

                        .requestMatchers("/books").authenticated()
                        .requestMatchers("/loans").authenticated()
                        .requestMatchers("/loans/**").authenticated()
                        .requestMatchers("/recommendations").authenticated()


                        // Admin-only pages
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .httpBasic(httpBasic -> httpBasic
                        .realmName("LibroRed API")) // Enable HTTP Basic Auth for API endpoints
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Return 401 for API endpoints instead of redirect
                            String requestPath = request.getRequestURI();
                            if (requestPath.startsWith("/api/v1/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Authentication required\",\"message\":\"" + authException.getMessage() + "\"}");
                            } else {
                                // Redirect to login page for web endpoints
                                response.sendRedirect("/login");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Return 403 for API endpoints instead of redirect
                            String requestPath = request.getRequestURI();
                            if (requestPath.startsWith("/api/v1/")) {
                                response.setStatus(403);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Access denied\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        }));

        return http.build();
    }

}
