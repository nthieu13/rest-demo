package com.example.restdemo.domain.util;

import com.example.restdemo.domain.entity.Account;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class AccountSearchCriteria {

    private String customerName;
    private String currency;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Predicate<Account> toPredicate() {
        return x -> {
            boolean result = true;
            if (StringUtils.isNotEmpty(customerName)) {
                result = customerName.equalsIgnoreCase(x.getCustomerName());
            }
            if (StringUtils.isNotEmpty(currency)) {
                result &= currency.equalsIgnoreCase(x.getCustomerName());
            }
            return result;
        };
    }
}
