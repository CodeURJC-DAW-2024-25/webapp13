package es.codeurjc13.librored.service;

import es.codeurjc13.librored.dto.UserCreateDTO;
import es.codeurjc13.librored.dto.UserDTO;
import es.codeurjc13.librored.mapper.UserMapper;
import es.codeurjc13.librored.model.User;
import es.codeurjc13.librored.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    // ✅ Registro desde panel de administración o interfaz web
    public void registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        if (user.getEncodedPassword() != null && !user.getEncodedPassword().startsWith("$2a$")) {
            user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        }

        if (user.getRole() == null) {
            user.setRole(User.Role.ROLE_USER);
        }

        userRepository.save(user);
    }

    // ✅ Registro desde API REST
    public void registerUser(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setEncodedPassword(passwordEncoder.encode(dto.rawPassword()));
        user.setRole(User.Role.ROLE_USER);
        userRepository.save(user);
    }

    // ✅ Conversión segura para exponer al frontend/API
    public UserDTO toDTO(User user) {
        return userMapper.toDTO(user);
    }

    // ✅ CRUD básico
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ Buscar por email/username
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // ✅ Actualización de usuario
    public void updateUser(Long id, User updatedUser) {
        userRepository.findById(id).ifPresent(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());

            if (updatedUser.getEncodedPassword() != null && !updatedUser.getEncodedPassword().isEmpty()) {
                user.setEncodedPassword(passwordEncoder.encode(updatedUser.getEncodedPassword()));
            }

            userRepository.save(user);
        });
    }

    // Methods to validate user credentials and roles
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsersExcept(User user) {
        List<User> users = userRepository.findAll();
        users.remove(user);
        return users;
    }

    public List<User> getValidBorrowers(User lender) {
        return userRepository.findAllValidBorrowers(lender);
    }

    // Métodos auxiliares que puedes borrar si no se usan en ningún controlador
    public void updateUsername(User user, String newUsername) {
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void updatePassword(User user, String newEncodedPassword) {
        user.setEncodedPassword(newEncodedPassword);
        userRepository.save(user);
    }

    public void saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        userRepository.save(user);
    }
}
