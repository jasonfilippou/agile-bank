package com.agilebank.model.account;

import static com.agilebank.util.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import com.agilebank.util.SortOrder;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * {@link RepresentationModelAssembler} for {@link AccountDto} instances. Provides methods for transforming a single or multiple
 * {@link AccountDto} instance(s) to HAL-formatted JSONs with links to relevant resources.
 * 
 * @author jason 
 * 
 * @see org.springframework.hateoas.server.mvc.WebMvcLinkBuilder#linkTo(Method) 
 * @see org.springframework.hateoas.server.mvc.WebMvcLinkBuilder#methodOn(Class, Object...) 
 */
@Component
public class AccountModelAssembler
    implements RepresentationModelAssembler<AccountDto, EntityModel<AccountDto>> {

  @Override
  public @NonNull EntityModel<AccountDto> toModel(@NonNull AccountDto accountDto) {

    return EntityModel.of(
        accountDto,
        linkTo(methodOn(AccountController.class).getAccount(accountDto.getId())).withSelfRel(),
        linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE), DEFAULT_SORT_BY_FIELD, SortOrder.ASC)).withRel(ALL_ACCOUNTS));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<AccountDto>> toCollectionModel(
      @NonNull Iterable<? extends AccountDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream().map(this::toModel).collect(Collectors.toList()),
        linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX), 
                Integer.parseInt(DEFAULT_PAGE_SIZE), DEFAULT_SORT_BY_FIELD, SortOrder.ASC)).withSelfRel());
  }
}
