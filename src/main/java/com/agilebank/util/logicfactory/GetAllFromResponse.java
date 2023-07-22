package com.agilebank.util.logicfactory;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

/**
 * Specification of {@link BaseResponse} that corresponds to the GET ALL TRANSACTIONS FROM SOURCE_ACCOUNT logic flow.
 * 
 * @author jason 
 */
public class GetAllFromResponse extends BaseResponse{

    public GetAllFromResponse(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
        super(transactionService, transactionModelAssembler);
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponseEntity(AggregateGetQueryParams params) {
        return ResponseEntity.ok(
                transactionModelAssembler.toCollectionModel(
                        transactionService.getAllTransactionsFrom(Long.valueOf(params.getTransactionQueryParams().get(SOURCE_ACCOUNT_ID)), 
                                params.getPage(), params.getPageSize(), params.getSortByField(), params.getSortOrder()), 
                        params.getTransactionQueryParams()));
    }
}
