package es.codeurjc13.librored.security.jwt;

import es.codeurjc13.librored.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        String token = authService.login(request.email(), request.password());
        User user = authService.getUser(request.email());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole().toString(),
                "email", user.getEmail()
        ));
    }

    // Para pruebas si el token está activo
    @GetMapping("/check")
    public ResponseEntity<String> checkToken() {
        return ResponseEntity.ok("Token is valid!");
    }
}
