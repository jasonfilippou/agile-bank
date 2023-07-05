package com.agilebank.model.user;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Data Transfer Object for application users.
 * @author jason 
 * @see User
 */
@Data
@AllArgsConstructor
public class UserDto {

  @NonNull private String username;

  @JsonProperty(access = WRITE_ONLY)
  @NonNull
  private String password;
}
