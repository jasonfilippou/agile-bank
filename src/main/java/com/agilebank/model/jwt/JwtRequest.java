package com.agilebank.model.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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
@ToString
public class JwtRequest implements Serializable {

  private static final long serialVersionId = 5926468583005150707L;

  @Schema(example = "agileuser")
  @NonNull private String username;
  @Schema(example = "agilepassword")
  @Size(min = 8, max = 30)
  @NonNull @ToString.Exclude String password;
}
