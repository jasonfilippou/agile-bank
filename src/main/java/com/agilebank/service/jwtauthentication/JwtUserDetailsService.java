package com.agilebank.service.jwtauthentication;

import com.agilebank.model.user.User;
import com.agilebank.model.user.UserDto;
import com.agilebank.persistence.UserRepository;
import com.agilebank.util.exceptions.BadPasswordLengthException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
   * @throws BadPasswordLengthException If the password provided is less than 8 or more than 30 characters.
   */
  public UserDto save(UserDto newUser) throws BadPasswordLengthException {
    String newUserPassword = newUser.getPassword();
    if (newUserPassword.length() < 8 || newUserPassword.length() > 30) {
      throw new BadPasswordLengthException(8, 30);
    }
    User savedUser =
        userRepository.save(new User(newUser.getUsername(), encoder.encode(newUserPassword)));
    return new UserDto(savedUser.getUsername(), savedUser.getPassword());
  }
}
