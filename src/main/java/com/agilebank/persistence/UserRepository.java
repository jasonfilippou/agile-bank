package com.agilebank.persistence;

import com.agilebank.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A {@link JpaRepository} for {@link User} objects.
 * @author jason
 * @see AccountRepository
 * @see TransactionRepository
 */
@CustomRepositoryAnnotation
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
