package es.codeurjc13.librored.security.jwt;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        return jwtProvider.createToken(auth);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }
}
