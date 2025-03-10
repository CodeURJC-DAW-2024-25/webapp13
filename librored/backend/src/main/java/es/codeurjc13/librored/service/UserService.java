package es.codeurjc13.librored.service;

import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.LoanRepository;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public void registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_USER);  // Default assign ROLE_USER
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());

            // ✅ Only update the password if a new one is provided
            if (updatedUser.getEncodedPassword() != null && !updatedUser.getEncodedPassword().isEmpty()) {
                user.setEncodedPassword(passwordEncoder.encode(updatedUser.getEncodedPassword()));
            }
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);  // Use the built-in delete method
    }

    public List<User> getAllUsersExcept(User user) {
        List<User> users = userRepository.findAll();
        users.remove(user); // Remove the lender from the list
        return users;
    }

    public void updateUsername(User user, String newUsername) {
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void updatePassword(User user, String newEncodedPassword) {
        user.setPassword(newEncodedPassword);
        userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        System.out.println("🔍 Searching for user in DB: " + username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        System.out.println("🔍 Searching for user in DB by email: " + email);
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        System.out.println("💾 Saving user: " + user.getEmail());
        userRepository.save(user);  // Save without returning anything
    }

    public List<User> getValidBorrowers(User lender) {
        return userRepository.findAllValidBorrowers(lender);
    }
}
