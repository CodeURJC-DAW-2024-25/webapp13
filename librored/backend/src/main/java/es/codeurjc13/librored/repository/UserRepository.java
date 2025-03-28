package es.codeurjc13.librored.repository;

import es.codeurjc13.librored.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); // BAD if username means display name

    Optional<User> findByEmail(String email); // PREFERRED for clarity


    @Query("SELECT u FROM User u WHERE u <> :lender AND u.role = 'ROLE_USER'")
    List<User> findAllValidBorrowers(@Param("lender") User lender);

}