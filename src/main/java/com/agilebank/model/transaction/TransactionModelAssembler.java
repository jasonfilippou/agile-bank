package com.agilebank.model.transaction;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.TransactionController;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TransactionModelAssembler
    implements RepresentationModelAssembler<TransactionDto, EntityModel<TransactionDto>> {

  @Override
  public @NonNull EntityModel<TransactionDto> toModel(@NonNull TransactionDto transactionDto) {
    return EntityModel.of(
        transactionDto,
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(
                            "sourceAccountId",
                            transactionDto.getSourceAccountId(),
                            "targetAccountId",
                            transactionDto.getTargetAccountId())))
            .withRel("all_transactions_between"),
        linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
            .withRel("all_transactions"));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<TransactionDto>> toCollectionModel(
          @NonNull Iterable<? extends TransactionDto> entities) {
    return CollectionModel.of(
            IterableUtils.toList(entities).stream().map(this::toModel).collect(Collectors.toList()));
  }

  public CollectionModel<EntityModel<TransactionDto>> toCollectionModel(Iterable<? extends TransactionDto> entities,
                                                                        Map<String, String> params){
    CollectionModel<EntityModel<TransactionDto>> collectionModel = toCollectionModel(entities);
    collectionModel.add(linkTo(methodOn(TransactionController.class).getAllTransactions(params)).withSelfRel());
    return collectionModel;
  }
}
