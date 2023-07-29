package com.agilebank.service.jwtauthentication;

import com.agilebank.model.user.User;
import com.agilebank.model.user.UserDto;
import com.agilebank.persistence.UserRepository;
import com.agilebank.util.logger.Logged;
import com.agilebank.util.exceptions.UsernameAlreadyInDatabaseException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class that talks to the database to retrieve and store user information.
 * 
 * @author jason 
 * 
 * @see JwtRequestFilter
 * @see JwtAuthenticationService
 * @see com.agilebank.controller.JwtAuthenticationController
 */
@Service
@RequiredArgsConstructor
@Logged
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  /**
   * Load a user from the database given their username.
   * @param username the user's username
   * @return An instance of {@link org.springframework.security.core.userdetails.User} (not to be confused with our own {@link User})
   * that contains the user's username, password and authorities.
   * @throws UsernameNotFoundException if no user with username {@literal username} exists in the database.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      // Return an instance of org.springframework.security.core.userdetails.User
      return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), Collections.emptyList());
    } else {
      throw new UsernameNotFoundException("User with username: " + username + " not found.");
    }
  }

  /**
   * Save a new user in the database.
   * @param newUser A {@link UserDto} with the information of the new user to store in the database.
   * @return A {@link UserDto} corresponding to the just persisted user.
   * @throws UsernameAlreadyInDatabaseException If the username provided already exists in the database.
   */
  public UserDto save(UserDto newUser) throws UsernameAlreadyInDatabaseException {
    String newUserPassword = newUser.getPassword();
    try {
    User savedUser =
        userRepository.save(new User(newUser.getUsername().trim(), encoder.encode(newUserPassword)));
    return new UserDto(savedUser.getUsername(), savedUser.getPassword());
    } catch(DataIntegrityViolationException integrityViolationException){
      throw new UsernameAlreadyInDatabaseException(newUser.getUsername().trim());
    }
  }
}
