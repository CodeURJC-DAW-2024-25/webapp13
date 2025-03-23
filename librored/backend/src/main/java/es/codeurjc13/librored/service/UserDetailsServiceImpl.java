package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.UserRepository;
import es.codeurjc13.librored.security.jwt.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This class is responsible for retrieving user details for authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user from the database by their username.
     *
     * @param username the username to search for
     * @return the user as a Spring Security UserDetails instance
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new UserDetailsImpl(user);
    }
}
