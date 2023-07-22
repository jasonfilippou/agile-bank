package com.agilebank.integration.expecteddtofactory;


import com.agilebank.model.transaction.TransactionDto;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

public class ExpectedTransactionDtoFactory {

  private final Map<Pair<Boolean, Boolean>, Supplier<BaseTransactionDtoProvider>> dtoResolver;

  public ExpectedTransactionDtoFactory(List<TransactionDto> fullList) {
    this.dtoResolver = initMap(fullList);
  }

  private Map<Pair<Boolean, Boolean>, Supplier<BaseTransactionDtoProvider>> initMap(List<TransactionDto> fullList) {
      return Map.of(
              Pair.of(false, false), () -> new GetAllQueryTransactionDtoProvider(fullList),
              Pair.of(false, true), () -> new GetToQueryTransactionDtoProvider(fullList),
              Pair.of(true, false), () -> new GetFromQueryTransactionDtoProvider(fullList),
              Pair.of(true, true), () -> new GetBetweenQueryTransactionDtoProvider(fullList)
      );
  }
  
  public BaseTransactionDtoProvider getDtoProvider(Boolean sourceAccountInParameterMap, Boolean targetAccountInParameterMap){
    return dtoResolver.get(Pair.of(sourceAccountInParameterMap, targetAccountInParameterMap)).get();
  }
}
