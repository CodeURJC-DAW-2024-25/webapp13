package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.dto.UserBasicDTO;
import es.codeurjc13.librored.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;


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
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        userRepository.save(user);  // Save without returning anything
    }

    public List<User> getValidBorrowers(User lender) {
        return userRepository.findAllValidBorrowers(lender);
    }

    // ==================== DTO-BASED METHODS FOR REST API ====================

    public List<UserDTO> getAllUsersDTO() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOs(users);
    }

    public Optional<UserDTO> getUserByIdDTO(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toDTO);
    }

    public UserDTO createUserDTO(UserDTO userDTO) {
        User user = userMapper.toDomain(userDTO);
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        // Set a default password for REST API created users
        String defaultPassword = "defaultPassword123";
        user.setEncodedPassword(passwordEncoder.encode(defaultPassword));
        
        // Use role from DTO or default to ROLE_USER
        if (user.getRole() == null) {
            user.setRole(User.Role.ROLE_USER);
        }
        
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public Optional<UserDTO> updateUserDTO(Long id, UserDTO userDTO) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            user.setUsername(userDTO.username());
            user.setEmail(userDTO.email());
            user.setRole(userDTO.role());
            User savedUser = userRepository.save(user);
            return Optional.of(userMapper.toDTO(savedUser));
        }
        return Optional.empty();
    }

    public boolean deleteUserDTO(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<UserDTO> getUserByUsernameDTO(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(userMapper::toDTO);
    }

    public Optional<UserDTO> getUserByEmailDTO(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(userMapper::toDTO);
    }

    public List<UserBasicDTO> getAllUsersExceptDTO(Long userId) {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(userMapper::toBasicDTO)
                .toList();
    }

    public List<UserBasicDTO> getValidBorrowersDTO(Long lenderId) {
        Optional<User> lenderOpt = userRepository.findById(lenderId);
        if (lenderOpt.isPresent()) {
            List<User> borrowers = userRepository.findAllValidBorrowers(lenderOpt.get());
            return userMapper.toBasicDTOs(borrowers);
        }
        return List.of();
    }
}
