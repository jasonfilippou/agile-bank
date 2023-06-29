package com.javainuse.unit.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.javainuse.model.UserDao;
import com.javainuse.model.UserDto;
import com.javainuse.persistence.UserRepository;
import com.javainuse.service.JwtUserDetailsService;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class JwtUserDetailsServiceTests {

  @InjectMocks private JwtUserDetailsService jwtUserDetailsService;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  private static final UserDao TEST_USER_DAO = new UserDao("username", "password");
  private static final UserDto TEST_USER_DTO = new UserDto("username", "password");

  @Test
  public void whenUserIsInDB_thenAppropriateUserDetailsReturned() {
    when(userRepository.findByUsername("username")).thenReturn(Optional.of(TEST_USER_DAO));
    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("username");
    assertEquals(userDetails.getUsername(), "username");
    assertEquals(userDetails.getPassword(), "password");
  }

  @Test(expected = UsernameNotFoundException.class)
  public void whenUserIsNotInDB_thenUsernameNotFoundExceptionIsThrown() {
    when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
    jwtUserDetailsService.loadUserByUsername(RandomStringUtils.randomAlphanumeric(10));
  }

  @Test
  public void whenSavingNewUser_thenTheirInformationIsReturned() {
    when(passwordEncoder.encode(TEST_USER_DTO.getPassword()))
        .thenReturn(TEST_USER_DTO.getPassword()); // Encoder basically does nothing.
    when(userRepository.save(any())).thenReturn(TEST_USER_DAO);
      assertEquals(TEST_USER_DTO, jwtUserDetailsService.save(TEST_USER_DTO));
  }
}
