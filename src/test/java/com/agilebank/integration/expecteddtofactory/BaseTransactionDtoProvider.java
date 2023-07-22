package com.agilebank.integration.expecteddtofactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.AggregateGetQueryParams;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class BaseTransactionDtoProvider {

    protected final List<TransactionDto> fullList;

    public abstract List<TransactionDto> getExpectedDtos(AggregateGetQueryParams aggregateGetQueryParams);
}
