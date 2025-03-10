package es.codeurjc13.librored.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Ensure CSRF token is accessible
                )
                .authorizeHttpRequests(auth -> auth
                        // Public resources (CSS, JS, Images)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/login", "/register", "/error/**", "/perform_login", "/loginerror").permitAll()

                        // API access: Public endpoints
                        .requestMatchers("/api/books", "/api/books/books-per-genre","/api/books/**" ).permitAll()

                        // API access: Only logged-in users
                        .requestMatchers("/api/users/**").hasRole("USER")  //  Only users with ROLE_USER can access
                        .requestMatchers("/api/users/verify-password", "/api/users/update-username", "/api/users/update-password").authenticated()
                        .requestMatchers("/api/loans/valid-borrowers").authenticated() // ✅ Allow only authenticated users
                        .requestMatchers("/api/**").authenticated()  // Require authentication for all APIs except the ones above


                        // User dashboard and protected actions (only for authenticated users)
                        //.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/myaccount").hasRole("USER")
                        .requestMatchers("/books").authenticated()
                        .requestMatchers("/loans").authenticated()
                        .requestMatchers("/recommendations").authenticated()


                        // Admin-only pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")

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

        return http.build();
    }

}
