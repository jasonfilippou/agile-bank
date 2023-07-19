package com.agilebank.util.logicfactory;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

public class ParameterLogicFactory {

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  private final Map<Pair<Boolean, Boolean>, Supplier<BaseResponse>> creators;

  public ParameterLogicFactory(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler){
      this.transactionService = transactionService;
      this.transactionModelAssembler = transactionModelAssembler;
      this.creators =
      Map.of(
              Pair.of(false, false),
              () -> new GetAllResponse(transactionService, transactionModelAssembler),
              Pair.of(false, true),
              () -> new GetAllToResponse(transactionService, transactionModelAssembler),
              Pair.of(true, false),
              () -> new GetAllFromResponse(transactionService, transactionModelAssembler),
              Pair.of(true, true),
              () -> new GetAllBetweenResponse(transactionService, transactionModelAssembler));
  }
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getResponse(
      Boolean paramsContainSourceAccountId,
      Boolean paramsContainTargetAccountId,
      AggregateGetQueryParams aggregateGetQueryParams) {
    return creators
        .get(Pair.of(paramsContainSourceAccountId, paramsContainTargetAccountId))
        .get()
        .getResponseEntity(aggregateGetQueryParams);
  }
}
