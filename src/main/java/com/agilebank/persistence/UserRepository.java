package com.agilebank.persistence;

import com.agilebank.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A {@link JpaRepository} for {@link User} objects.
 * @author jason
 * @see com.agilebank.persistence.AccountRepository
 * @see TransactionRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
