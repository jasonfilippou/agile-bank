package com.agilebank.service.jwtauthentication;

import com.agilebank.util.exceptions.BadPasswordLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationService {

  private final AuthenticationManager authenticationManager;

  @Autowired
  public JwtAuthenticationService(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public void authenticate(String username, String password) throws Exception {
    try {
      // Another check in addition to the check in registration here, just in case somebody
      // injected my DB with a weak password and wants to authenticate with it.
      if (password.length() < 8 || password.length() > 30) {
        throw new BadPasswordLengthException(8, 30);
      }
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}
