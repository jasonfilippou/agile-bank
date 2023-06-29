package com.agilebank.model.jwt;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class JwtRequest implements Serializable {

    private static final long serialVersionId = 5926468583005150707L;

    private String username;
    private String password;
    
    public JwtRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}
