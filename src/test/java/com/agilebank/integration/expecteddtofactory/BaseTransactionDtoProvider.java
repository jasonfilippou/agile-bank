package com.agilebank.integration.expecteddtofactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.AggregateGetQueryParams;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A base class for a provider of expected transaction DTOs used by {@link com.agilebank.integration.AgileBankIntegrationTests}
 * paginated / sorted GET ALL queries.
 *
 * @author jason
 *
 * @see GetAllQueryTransactionDtoProvider
 * @see GetFromQueryTransactionDtoProvider
 * @see GetToQueryTransactionDtoProvider
 * @see GetBetweenQueryTransactionDtoProvider
 */
@RequiredArgsConstructor
public abstract class BaseTransactionDtoProvider {

    protected final List<TransactionDto> fullList;

    /**
     * Get an expected list of {@link TransactionDto} instances based on the provided parameters.
     * @param aggregateGetQueryParams An {@link AggregateGetQueryParams} instance.
     * @return A {@link List} over {@link TransactionDto} instances that satisfy the provided parameters.
     */
    public abstract List<TransactionDto> getExpectedDtos(AggregateGetQueryParams aggregateGetQueryParams);
}
