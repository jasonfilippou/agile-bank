package com.agilebank.util.exceptions;

import lombok.Getter;

/** A {@link java.lang.RuntimeException} thrown when the user tries to register with a username that already exists
 * in the database.
 * @author jason
 */
@Getter
public class UsernameAlreadyInDatabaseException extends RuntimeException{

    private final String username;

    public UsernameAlreadyInDatabaseException(String username){
        super("Username " + username + " already in database.");
        this.username = username;
    }
}
