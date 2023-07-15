package com.agilebank.util.exceptions;

import lombok.Getter;

/**
 * An exception thrown by pagination flows when the page number or page size parameters provided are invalid.
 *
 * @author jason
 */
@Getter
public class InvalidPaginationParametersSpecifiedException extends RuntimeException {

    private final Integer pageNumber;
    private final Integer pageSize;
    public InvalidPaginationParametersSpecifiedException(Integer pageNumber, Integer pageSize){
        super("Invalid pagination parameters specified. Provided: pageNumber = " + pageNumber + ", pageSize =  " + pageSize + ". " +
                "Constraints: pageNumber >=0, pageSize >= 1.");
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
