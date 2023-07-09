package com.agilebank.controller;

import com.agilebank.model.jwt.JwtRequest;
import com.agilebank.model.jwt.JwtResponse;
import com.agilebank.model.user.User;
import com.agilebank.model.user.UserDto;
import com.agilebank.service.jwtauthentication.JwtAuthenticationService;
import com.agilebank.service.jwtauthentication.JwtUserDetailsService;
import com.agilebank.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestController} responsible for exposing POST endpoints for registering and authenticating users.
 *
 * @author jason
 * @see User
 * @see JwtRequest
 * @see JwtResponse
 * @see JwtTokenUtil
 */
@RestController
@RequestMapping("/bankapi")
@CrossOrigin
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Authentication API", version = "v1"))
public class JwtAuthenticationController {

  private final JwtTokenUtil jwtTokenUtil;
  private final JwtUserDetailsService userDetailsService;

  private final JwtAuthenticationService jwtAuthenticationService;

  /**
   * POST endpoint for JWT user authentication.
   * @param authenticationRequest An instance of {@link JwtRequest} containing the user's username and password. The password
   *                              is stored in the database in encrypted format.
   * @return A {@link ResponseEntity} over {@link JwtResponse} instances.
   * @throws Exception if the {@link JwtAuthenticationService} throws it.
   */
  @PostMapping(value = "/authenticate")
  public ResponseEntity<JwtResponse> createAuthenticationToken(
      @RequestBody JwtRequest authenticationRequest) throws Exception {

    jwtAuthenticationService.authenticate(
        authenticationRequest.getUsername(), authenticationRequest.getPassword());
    final UserDetails userDetails =
        userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    final String token = jwtTokenUtil.generateToken(userDetails);
    return ResponseEntity.ok(new JwtResponse(token));
  }

  /**
   * A POST endpoint for registering users.
   * @param user An instance of {@link UserDto} containing a username and password for the user. The password will be stored in
   *             encrypted format in the database.
   * @return An instance of {@link ResponseEntity} over a {@link UserDto} instance.
   */
  @PostMapping(value = "/register")
  public ResponseEntity<UserDto> registerUser(@RequestBody UserDto user) {
    return new ResponseEntity<>(userDetailsService.save(user), HttpStatus.CREATED);
  }
}
