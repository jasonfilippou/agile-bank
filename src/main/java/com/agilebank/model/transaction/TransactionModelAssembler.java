package com.agilebank.model.transaction;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.TransactionController;
import java.util.Map;
import lombok.NonNull;
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
            linkTo(methodOn(TransactionController.class).getAllTransactions(Map.of())).withRel("all_transactions"));
  }
}
