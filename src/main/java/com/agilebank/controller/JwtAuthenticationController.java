package com.agilebank.controller;

import com.agilebank.model.jwt.JwtRequest;
import com.agilebank.model.jwt.JwtResponse;
import com.agilebank.model.user.User;
import com.agilebank.model.user.UserDto;
import com.agilebank.service.jwtauthentication.JwtAuthenticationService;
import com.agilebank.service.jwtauthentication.JwtUserDetailsService;
import com.agilebank.util.JwtTokenUtil;
import com.agilebank.util.logger.Logged;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "1. Authentication API")
@Validated
@Logged
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
  @Operation(summary = "Authenticate with your username and password")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Authentication successful, JWT returned.",
                          content = {
                                  @Content(
                                          mediaType = "application/json",
                                          schema = @Schema(implementation = JwtResponse.class))
                          }),
                  @ApiResponse(
                          responseCode = "401",
                          description = "Bad password.",
                          content = @Content),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Username not found.",
                          content = @Content)
          })
  @PostMapping(value = "/authenticate")
  public ResponseEntity<JwtResponse> createAuthenticationToken(
      @RequestBody @Valid JwtRequest authenticationRequest) throws Exception {

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
  @Operation(summary = "Register with your username and password")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "201",
                          description = "Registration successful.",
                          content = {
                                  @Content(
                                          mediaType = "application/json",
                                          schema = @Schema(implementation = UserDto.class))
                          }),
                  @ApiResponse(
                          responseCode = "400",
                          description = "Invalid password length provided; passwords should be from 8 to 30 characters.",
                          content = {
                                  @Content(
                                          mediaType = "application/json",
                                          schema = @Schema(implementation = UserDto.class))
                          }),
                  @ApiResponse(
                          responseCode = "409",
                          description = "Username already taken.",
                          content = @Content)
          })
  @PostMapping(value = "/register")
  public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto user) {
    return new ResponseEntity<>(userDetailsService.save(user), HttpStatus.CREATED);
  }
}
