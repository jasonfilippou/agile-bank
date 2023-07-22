package com.agilebank.util.logicfactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

/**
 * An abstract class used by {@link TransactionParameterLogicFactory}.
 *
 * @author jason
 */
public abstract class BaseResponse {
    
    protected TransactionService transactionService;

    protected TransactionModelAssembler transactionModelAssembler;

    public BaseResponse(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler){
        this.transactionService = transactionService;
        this.transactionModelAssembler = transactionModelAssembler;
    }

    /**
     * Use the provided params to call the {@link TransactionService} and {@link TransactionModelAssembler} beans
     * and construct a {@link ResponseEntity} that describes the result of these calls.
     * @param aggregateGetQueryParams An instance encapsulating the parameters of the transaction.
     * @return A {@link ResponseEntity} describing the result of the GET ALL operation.
     */
    public abstract ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponseEntity(AggregateGetQueryParams aggregateGetQueryParams);
}
