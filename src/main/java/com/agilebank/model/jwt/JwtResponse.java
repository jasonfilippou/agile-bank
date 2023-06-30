package com.agilebank.model.jwt;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class JwtResponse implements Serializable {

  @Serial private static final long serialVersionUID = -8091879091924046844L;
  private final String jwtToken;
}
