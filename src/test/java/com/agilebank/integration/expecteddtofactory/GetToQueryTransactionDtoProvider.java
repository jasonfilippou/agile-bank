package com.agilebank.integration.expecteddtofactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestUtils.compareFieldsInGivenOrder;

public class GetToQueryTransactionDtoProvider extends BaseTransactionDtoProvider {
    public GetToQueryTransactionDtoProvider(List<TransactionDto> fullList) {
        super(fullList);
    }

    @Override
    public List<TransactionDto> getExpectedDtos(AggregateGetQueryParams aggregateGetQueryParams) {
        Integer page = aggregateGetQueryParams.getPage();
        Integer pageSize = aggregateGetQueryParams.getPageSize();
        String sortByField = aggregateGetQueryParams.getSortByField();
        SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
        Map<String, String> transactionParams = aggregateGetQueryParams.getTransactionQueryParams();
        return fullList.stream()
                .filter(
                        transactionDto ->
                                transactionDto
                                        .getTargetAccountId()
                                        .equals(Long.valueOf(transactionParams.get(TARGET_ACCOUNT_ID))))
                .sorted(
                        (t1, t2) ->
                                compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                .collect(Collectors.toList())
                .subList(page * pageSize, pageSize * (page + 1));    }
}
