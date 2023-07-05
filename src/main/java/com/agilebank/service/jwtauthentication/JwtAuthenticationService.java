package com.agilebank.service.jwtauthentication;

import com.agilebank.util.exceptions.BadPasswordLengthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * A service class that provides a single authentication method for users.
 *
 * @author jason
 *
 * @see #authenticate(String, String)
 */
@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

  private final AuthenticationManager authenticationManager;

  /**
   * Authenticates the &lt; username, password &gt; pair provided.
   * @param username The user's username.
   * @param password The user's password.
   * @throws BadPasswordLengthException if the password's length is smaller than 8 or greater than 30 characters.
   * @throws Exception if the underlying {@link AuthenticationManager} throws a {@link DisabledException} or {@link BadCredentialsException}.
   * @see AuthenticationManager
   * @see UsernamePasswordAuthenticationToken
   */
  public void authenticate(String username, String password) throws BadPasswordLengthException, Exception {
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
