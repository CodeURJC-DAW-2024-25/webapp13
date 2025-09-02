package es.codeurjc13.librored.controller;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    private final UserService userService;

    public LoanRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/valid-borrowers")
    public ResponseEntity<List<User>> getValidBorrowers(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User lender = userService.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        List<User> borrowers = userService.getValidBorrowers(lender);

        return ResponseEntity.ok(borrowers);
    }
}
