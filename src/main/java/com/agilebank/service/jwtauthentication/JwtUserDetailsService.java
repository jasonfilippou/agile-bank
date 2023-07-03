package com.agilebank.service.jwtauthentication;

import com.agilebank.model.user.UserDao;
import com.agilebank.model.user.UserDto;
import com.agilebank.persistence.UserRepository;
import com.agilebank.util.exceptions.BadPasswordLengthException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UserDao> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      // Return an instance of org.springframework.security.core.userdetails.User
      return new User(user.get().getUsername(), user.get().getPassword(), Collections.emptyList());
    } else {
      throw new UsernameNotFoundException("User with username: " + username + " not found.");
    }
  }

  public UserDto save(UserDto newUser) throws BadPasswordLengthException {
    String newUserPassword = newUser.getPassword();
    if (newUserPassword.length() < 8 || newUserPassword.length() > 30) {
      throw new BadPasswordLengthException(8, 30);
    }
    UserDao savedUserDao =
        userRepository.save(new UserDao(newUser.getUsername(), encoder.encode(newUserPassword)));
    return new UserDto(savedUserDao.getUsername(), savedUserDao.getPassword());
  }
}
