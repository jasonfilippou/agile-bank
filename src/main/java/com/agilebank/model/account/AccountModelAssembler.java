package com.agilebank.model.account;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AccountModelAssembler
    implements RepresentationModelAssembler<AccountDto, EntityModel<AccountDto>> {

  @Override
  public @NonNull EntityModel<AccountDto> toModel(@NonNull AccountDto accountDto) {

    return EntityModel.of(
        accountDto,
        linkTo(methodOn(AccountController.class).getAccount(accountDto.getId())).withSelfRel(),
        linkTo(methodOn(AccountController.class).getAllAccounts()).withRel("all_accounts"));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<AccountDto>> toCollectionModel(
      @NonNull Iterable<? extends AccountDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream().map(this::toModel).collect(Collectors.toList()),
        linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());
  }
}
