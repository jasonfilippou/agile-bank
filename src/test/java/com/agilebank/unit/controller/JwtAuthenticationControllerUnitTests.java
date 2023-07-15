package com.agilebank.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.agilebank.controller.JwtAuthenticationController;
import com.agilebank.model.jwt.JwtRequest;
import com.agilebank.model.jwt.JwtResponse;
import com.agilebank.model.user.UserDto;
import com.agilebank.service.jwtauthentication.JwtAuthenticationService;
import com.agilebank.service.jwtauthentication.JwtUserDetailsService;
import com.agilebank.util.JwtTokenUtil;
import com.agilebank.util.TestUserDetailsImpl;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationControllerUnitTests {

  private static final TestUserDetailsImpl TEST_USER_DETAILS =
      new TestUserDetailsImpl("username", "password");
  private static final JwtRequest TEST_JWT_REQUEST = new JwtRequest("username", "password");
  private static final UserDto TEST_USER_DTO = new UserDto("username", "password");
  @InjectMocks private JwtAuthenticationController jwtAuthenticationController;
  @Mock private JwtTokenUtil jwtTokenUtil;
  @Mock private JwtUserDetailsService userDetailsService;
  @Mock private JwtAuthenticationService jwtAuthenticationService;

  @Test
  public void whenUserIsAuthenticatedInDB_thenReturnNewToken() throws Exception {
    doNothing().when(jwtAuthenticationService).authenticate(anyString(), anyString());
    when(userDetailsService.loadUserByUsername(anyString())).thenReturn(TEST_USER_DETAILS);
    when(jwtTokenUtil.generateToken(TEST_USER_DETAILS)).thenReturn("token");
    assertEquals(
        Objects.requireNonNull(
            jwtAuthenticationController.createAuthenticationToken(TEST_JWT_REQUEST).getBody()),
        new JwtResponse("token"));
  }

  @Test(expected = Exception.class)
  public void
      whenAuthenticatingUser_andAuthenticationServiceThrowsException_thenExceptionBubblesUp()
          throws Exception {
    doThrow(new Exception("some message"))
        .when(jwtAuthenticationService)
        .authenticate(anyString(), anyString());
    jwtAuthenticationController.createAuthenticationToken(TEST_JWT_REQUEST);
  }

  @Test(expected = UsernameNotFoundException.class)
  public void
      whenAuthenticatingUser_andDetailsServiceThrowsUsernameNotFoundException_thenExceptionBubblesUp()
          throws Exception {
    doNothing().when(jwtAuthenticationService).authenticate(anyString(), anyString());
    doThrow(new UsernameNotFoundException("some message"))
        .when(userDetailsService)
        .loadUserByUsername(anyString());
    jwtAuthenticationController.createAuthenticationToken(TEST_JWT_REQUEST);
  }
}
