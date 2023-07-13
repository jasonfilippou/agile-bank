package com.agilebank.util;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A {@link Mapper} to assist with PATCH logic.
 *
 * @author jason
 *
 * @see Mapper
 */
@Mapper(componentModel = "spring")
@Component
public abstract class UpdateMapper {

    @Autowired
    protected CurrencyLedger currencyLedger;

    @Mapping(target = "balance", expression = "java(updateBalanceIfNecessary(entity.getCurrency(), dto.getCurrency(), " +
            "entity.getBalance(), dto.getBalance()))")
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "dto.currency", target = "currency")
    public abstract Account updateAccountFromDto(AccountDto dto, Account entity);
    
    protected BigDecimal updateBalanceIfNecessary(Currency entityCurrency, Currency dtoCurrency, BigDecimal entityBalance,
                                                  BigDecimal dtoBalance){
        if(dtoBalance != null){ // If the dto provides a balance, just use it and be done with it.
            return dtoBalance;
        }
        // Otherwise, if the dto provides a currency that is different from the entity's, perform the appropriate conversion.
        if(dtoCurrency != null && dtoCurrency != entityCurrency){
            return currencyLedger.convertAmountToTargetCurrency(entityCurrency, dtoCurrency, entityBalance);
        }
        // Finally, if the dto either does not provide a currency or provides the same currency, return the entity's balance.
        return entityBalance;
    }
}
