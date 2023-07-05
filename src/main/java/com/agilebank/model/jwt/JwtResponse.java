package com.agilebank.model.jwt;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Simple POJO that defines the API's response to the user for a JWT token request.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class JwtResponse implements Serializable {

  @Serial private static final long serialVersionUID = -8091879091924046844L;
  private final String jwtToken;
}
