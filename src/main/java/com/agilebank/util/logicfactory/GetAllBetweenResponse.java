package com.agilebank.util.logicfactory;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component

public class GetAllBetweenResponse extends BaseResponse{

    @Autowired
    public GetAllBetweenResponse(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
        super(transactionService, transactionModelAssembler);
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponseEntity(AggregateGetQueryParams params) {
        return ResponseEntity.ok(
                transactionModelAssembler.toCollectionModel(
                        transactionService.getAllTransactionsBetween(
                                Long.valueOf(params.getTransactionQueryParams().get(SOURCE_ACCOUNT_ID)),
                                Long.valueOf(params.getTransactionQueryParams().get(TARGET_ACCOUNT_ID)),
                                params.getPage(), params.getPageSize(), params.getSortByField(), params.getSortOrder()),
                        params.getTransactionQueryParams()));
    }
}
