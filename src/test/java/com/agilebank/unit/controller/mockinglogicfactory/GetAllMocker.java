package com.agilebank.unit.controller.mockinglogicfactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.SortOrder;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestUtils.*;
import static org.mockito.Mockito.when;

public class GetAllMocker extends BaseMocker {
    public GetAllMocker(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
        super(transactionService, transactionModelAssembler);
    }
    

    @Override
    public void accept(AggregateGetQueryParams aggregateGetQueryParams) {
        Integer page = aggregateGetQueryParams.getPage();
        Integer pageSize = aggregateGetQueryParams.getPageSize();
        String sortByField = aggregateGetQueryParams.getSortByField();
        SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
        Map<String, String> transactionParams = aggregateGetQueryParams.getTransactionQueryParams();
        assert !(transactionParams.containsKey(SOURCE_ACCOUNT_ID)
                || transactionParams.containsKey(TARGET_ACCOUNT_ID));
        List<TransactionDto> subListOfPage =
                TEST_VALID_TRANSACTION_DTOS.stream()
                        .sorted(
                                (t1, t2) ->
                                        compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                        .collect(Collectors.toList())
                        .subList(page * pageSize, pageSize * (page + 1));
        when(transactionService.getAllTransactions(page, pageSize, sortByField, sortOrder))
                .thenReturn(new PageImpl<>(subListOfPage));
        when(transactionModelAssembler.toCollectionModel(new PageImpl<>(subListOfPage), transactionParams))
                .thenReturn(transactionDtosToCollectionModel(subListOfPage, transactionParams));
    }
}
