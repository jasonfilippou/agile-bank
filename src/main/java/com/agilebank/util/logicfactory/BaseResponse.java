package com.agilebank.util.logicfactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

/**
 *
 */
public abstract class BaseResponse {
    
    protected TransactionService transactionService;

    protected TransactionModelAssembler transactionModelAssembler;

    public BaseResponse(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler){
        this.transactionService = transactionService;
        this.transactionModelAssembler = transactionModelAssembler;
    }
    public abstract ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponseEntity(AggregateGetQueryParams aggregateGetQueryParams);
}
