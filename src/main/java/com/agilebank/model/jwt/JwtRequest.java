package com.agilebank.model.jwt;

import java.io.Serializable;
import lombok.*;

/**
 * Simple POJO for defining a user request for a JWT token.
 * 
 * @author jason 
 * 
 * @see JwtResponse
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JwtRequest implements Serializable {

  private static final long serialVersionId = 5926468583005150707L;

  @NonNull private String username;
  @NonNull private String password;
}
