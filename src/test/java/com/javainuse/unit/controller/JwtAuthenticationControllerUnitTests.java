package com.javainuse.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.javainuse.controller.JwtAuthenticationController;
import com.javainuse.model.JwtRequest;
import com.javainuse.model.JwtResponse;
import com.javainuse.service.JwtAuthenticationService;
import com.javainuse.service.JwtUserDetailsService;
import com.javainuse.util.JwtTokenUtil;
import com.javainuse.util.TestUserDetailsImpl;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationControllerUnitTests {

  @InjectMocks private JwtAuthenticationController jwtAuthenticationController;
  @Mock private JwtTokenUtil jwtTokenUtil;
  @Mock private JwtUserDetailsService userDetailsService;
  @Mock private JwtAuthenticationService jwtAuthenticationService;
  
  private static final TestUserDetailsImpl TEST_USER_DETAILS = new TestUserDetailsImpl("username", "password");
  private static final JwtRequest TEST_JWT_REQUEST = new JwtRequest("username", "password");

  @Test
  public void whenUserIsAuthenticatedInDB_thenReturnNewToken() throws Exception {
    doNothing().when(jwtAuthenticationService).authenticate(anyString(), anyString());
    when(userDetailsService.loadUserByUsername(anyString())).thenReturn(TEST_USER_DETAILS);
    when(jwtTokenUtil.generateToken(TEST_USER_DETAILS)).thenReturn("token");
    assertEquals(Objects.requireNonNull(jwtAuthenticationController.createAuthenticationToken(
                    TEST_JWT_REQUEST).getBody()),
            new JwtResponse("token"));
  }
  
  @Test(expected = Exception.class)
  public void whenAuthenticationServiceThrowsException_thenExceptionBubblesUp() throws Exception {
    doThrow(new Exception("some message")).when(jwtAuthenticationService).authenticate(anyString(), anyString());
    jwtAuthenticationController.createAuthenticationToken(TEST_JWT_REQUEST);
  }
  
  @Test(expected = UsernameNotFoundException.class)
  public void wheUserDetailsServiceThrowsException_thenExceptionBubblesUp() throws Exception {
    doNothing().when(jwtAuthenticationService).authenticate(anyString(), anyString());
    doThrow(new UsernameNotFoundException("some message")).when(userDetailsService).loadUserByUsername(anyString());
    jwtAuthenticationController.createAuthenticationToken(TEST_JWT_REQUEST);
  }
}
