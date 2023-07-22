package com.agilebank.util.logicfactory;

import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.SortOrder;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A factory class that simplifies the logic of calling the {@link TransactionService} from within
 * {@link com.agilebank.controller.TransactionController#getAllTransactions(Map, Integer, Integer, String, SortOrder)}.
 * 
 * @author jason 
 * 
 * @see #getResponseEntitySupplier(Boolean, Boolean) 
 * @see com.agilebank.controller.TransactionController
 * @see BaseResponse
 */
public class TransactionParameterLogicFactory {

  private final Map<Pair<Boolean, Boolean>, Supplier<BaseResponse>> creators;

  public TransactionParameterLogicFactory(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler){
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

  /** Returns an appropriate instance of {@link BaseResponse} depending on the provided parameters.
   *
   * @param paramsContainSourceAccountId {@literal true} if the transaction query parameters included &quot; sourceAccountId &quot,
   *                                                    {@literal false} otherwise.
   * @param paramsContainTargetAccountId {@literal true} if the transaction query parameters included &quot; targetAccountId &quot,
   *      *                                                    {@literal false} otherwise.
   * @return An appropriate subclass of {@link BaseResponse}.
   */
  public BaseResponse getResponseEntitySupplier(
      Boolean paramsContainSourceAccountId, Boolean paramsContainTargetAccountId) {
    return creators
        .get(Pair.of(paramsContainSourceAccountId, paramsContainTargetAccountId))
        .get();
  }
}
