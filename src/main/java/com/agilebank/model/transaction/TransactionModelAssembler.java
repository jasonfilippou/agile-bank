package com.agilebank.model.transaction;

import static com.agilebank.util.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.TransactionController;
import com.agilebank.util.SortOrder;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * {@link RepresentationModelAssembler} for {@link TransactionDto} instances. Provides methods for
 * transforming a single or multiple {@link TransactionDto} instance(s) to HAL-formatted JSONs with
 * links to relevant resources.
 *
 * @author jason
 * @see org.springframework.hateoas.server.mvc.WebMvcLinkBuilder#linkTo(Method)
 * @see org.springframework.hateoas.server.mvc.WebMvcLinkBuilder#methodOn(Class, Object...)
 */
@Component
public class TransactionModelAssembler
    implements RepresentationModelAssembler<TransactionDto, EntityModel<TransactionDto>> {

  @Override
  public @NonNull EntityModel<TransactionDto> toModel(@NonNull TransactionDto transactionDto) {
    return EntityModel.of(
        transactionDto,
        linkTo(methodOn(TransactionController.class).getTransaction(transactionDto.getId()))
            .withSelfRel(),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(SOURCE_ACCOUNT_ID, transactionDto.getSourceAccountId().toString()),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_FROM),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(TARGET_ACCOUNT_ID, transactionDto.getTargetAccountId().toString()),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_TO),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(
                            SOURCE_ACCOUNT_ID,
                            Long.toString(transactionDto.getSourceAccountId()),
                            TARGET_ACCOUNT_ID,
                            Long.toString(transactionDto.getTargetAccountId())),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_BETWEEN),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<TransactionDto>> toCollectionModel(
      @NonNull Iterable<? extends TransactionDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream().map(this::toModel).collect(Collectors.toList()));
  }

  public CollectionModel<EntityModel<TransactionDto>> toCollectionModel(
      Iterable<? extends TransactionDto> entities, Map<String, String> params) {
    CollectionModel<EntityModel<TransactionDto>> collectionModel = toCollectionModel(entities);
    collectionModel.add(
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        params,
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withSelfRel());
    if (params.size() > 0) {
      collectionModel.add(
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Collections.emptyMap(),
                          Integer.parseInt(DEFAULT_PAGE_IDX),
                          Integer.parseInt(DEFAULT_PAGE_SIZE),
                          DEFAULT_SORT_BY_FIELD,
                          SortOrder.ASC))
              .withRel(ALL_TRANSACTIONS));
    }
    return collectionModel;
  }
}
