package com.agilebank.unit.controller.mockinglogicfactory;

import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class TransactionDependencyMockingFactory {

  private Map<Pair<Boolean, Boolean>, BaseMocker> creators;

  public TransactionDependencyMockingFactory(
      TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
    this.creators =
        Map.of(
            Pair.of(false, false),
            new GetAllMocker(transactionService, transactionModelAssembler),
            Pair.of(false, true),
            new GetToMocker(transactionService, transactionModelAssembler),
            Pair.of(true, false),
            new GetFromMocker(transactionService, transactionModelAssembler),
            Pair.of(true, true),
            new GetBetweenMocker(transactionService, transactionModelAssembler));
  }

  public void mockWithParams(
      Boolean paramsContainSourceAccountId,
      Boolean paramsContainTargetAccountId,
      AggregateGetQueryParams params) {
    creators
        .get(Pair.of(paramsContainSourceAccountId, paramsContainTargetAccountId))
        .accept(params);
  }
}
