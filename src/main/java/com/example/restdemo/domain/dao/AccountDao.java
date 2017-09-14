package com.example.restdemo.domain.dao;

import com.example.restdemo.domain.entity.Account;
import com.example.restdemo.domain.util.AccountSearchCriteria;

import java.util.List;
import java.util.Optional;

public interface AccountDao {

    /**
     *
     * @param accountId
     * @return
     */
    Optional<Account> getAccountById(String accountId);

    /**
     *
     * @return
     */
    List<Account> getAllAccounts();

    /**
     *
     * @param account
     * @return
     */
    Account createAccount(Account account);

    /**
     *
     * @param account
     * @return
     */
    Account updateAccount(Account account);

    /**
     *
     * @param criteria
     * @return
     */
    List<Account> search(AccountSearchCriteria criteria);

    /**
     *
     * @param accountId
     * @return
     */
    Account clone(String accountId);
}
