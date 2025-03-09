package es.codeurjc13.librored.security;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Loading user for authentication: " + email);

        User user = userRepository.findByEmail(email)  // Fetch by email instead of username
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()) // Store email in Spring Security session
                .password(user.getEncodedPassword()) // Use encoded password
                .roles(user.getRole().name()) // Assign correct role
                .build();
    }


}