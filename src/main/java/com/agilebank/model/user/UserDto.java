package com.agilebank.model.user;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object for application users.
 * @author jason 
 * @see User
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {

  @Schema(example = "agileuser")
  @NonNull
  @NotBlank private String username;

  @Schema(example = "agilepassword")
  @JsonProperty(access = WRITE_ONLY)
  @NonNull
  @ToString.Exclude
  @Size(min = 8, max = 30)
  private String password;
}
