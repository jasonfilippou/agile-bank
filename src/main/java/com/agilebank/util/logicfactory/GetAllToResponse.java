package com.agilebank.util.logicfactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

@Component
public class GetAllToResponse extends BaseResponse {
    @Autowired
    public GetAllToResponse(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
        super(transactionService, transactionModelAssembler);
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponseEntity(AggregateGetQueryParams params) {
    return ResponseEntity.ok(
        transactionModelAssembler.toCollectionModel(
            transactionService.getAllTransactionsTo(
                Long.valueOf(params.getTransactionQueryParams().get(TARGET_ACCOUNT_ID)),
                params.getPage(),
                params.getPageSize(),
                params.getSortByField(),
                params.getSortOrder()),
                params.getTransactionQueryParams()));
  }
}
