package com.agilebank.model.user;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

/**
 * Data Transfer Object for application users.
 * @author jason 
 * @see User
 */
@Data
@AllArgsConstructor
public class UserDto {

  @Schema(example = "agileuser")
  @NonNull private String username;

  @Schema(example = "agilepassword")
  @JsonProperty(access = WRITE_ONLY)
  @NonNull
  @ToString.Exclude
  @Size(min = 8, max = 30)
  private String password;
}
