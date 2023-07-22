package com.agilebank.unit.controller.mockinglogicfactory;

import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;

import java.util.function.Consumer;

public abstract class BaseMocker implements Consumer<AggregateGetQueryParams>{
    protected TransactionService transactionService;
    protected TransactionModelAssembler transactionModelAssembler;

    public BaseMocker(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler){
        this.transactionService = transactionService;
        this.transactionModelAssembler = transactionModelAssembler;
    }
}
