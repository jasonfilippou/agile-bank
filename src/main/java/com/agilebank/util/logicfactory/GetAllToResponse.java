package com.agilebank.util.logicfactory;

import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

/**
 * Specification of {@link BaseResponse} that corresponds to the GET ALL TRANSACTIONS TO TARGET_ACCOUNT logic flow.
 *
 * @author jason
 */
public class GetAllToResponse extends BaseResponse {

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
