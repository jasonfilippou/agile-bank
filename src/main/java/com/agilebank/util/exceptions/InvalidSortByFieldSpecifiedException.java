package com.agilebank.util.exceptions;

import java.util.List;
import lombok.Getter;

/**
 * A {@link RuntimeException} thrown in paginated queries when the user provides an invalid field to sort by.
 * 
 * @author jason 
 */
@Getter
public class InvalidSortByFieldSpecifiedException extends RuntimeException{

    private final String fieldSpecified;
    private final List<String> acceptableFields;
    public InvalidSortByFieldSpecifiedException(String fieldSpecified, List<String> acceptableFields){
        super("Invalid sort by field: " + fieldSpecified + " specified. Acceptable fields are: " +
                acceptableFields.toString() + ".");
        this.fieldSpecified = fieldSpecified;
        this.acceptableFields = List.copyOf(acceptableFields);
    }
}
