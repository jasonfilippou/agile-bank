package com.agilebank.persistence;

import com.agilebank.model.user.UserDao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Long> {
    Optional<UserDao> findByUsername(String username);

}
