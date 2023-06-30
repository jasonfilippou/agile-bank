package com.agilebank.model.account;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import lombok.NonNull;
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
}
