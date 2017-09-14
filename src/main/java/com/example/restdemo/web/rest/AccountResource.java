package com.example.restdemo.web.rest;

import com.example.restdemo.annotation.PATCH;
import com.example.restdemo.domain.dao.AccountDao;
import com.example.restdemo.domain.entity.Account;
import com.example.restdemo.domain.util.AccountSearchCriteria;
import com.example.restdemo.domain.util.GenericBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Stream;

@Named
@Path("/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final String NOT_FOUND = "{\"error\": \"Account not found\"}";

    @Inject
    private AccountDao accountDao;

    @Path("/account")
    @GET
    public Response getAccounts(@QueryParam("customerName") String customerName,
                                @QueryParam("customerName") String currency) {
        if (Stream.of(customerName, currency).allMatch(StringUtils::isEmpty)) {
            return Response.ok(accountDao.getAllAccounts()).build();
        }

        AccountSearchCriteria criteria = GenericBuilder.of(AccountSearchCriteria::new)
                .with(AccountSearchCriteria::setCustomerName, customerName)
                .with(AccountSearchCriteria::setCurrency, currency)
                .build();

        return Response.ok(accountDao.search(criteria)).build();
    }

    @Path("/account/{id}")
    @GET
    public Response getAccountById(@PathParam("id") String accountId) {
        Account account = accountDao.getAccountById(accountId).orElse(null);
        if (account != null) {
            return Response.ok(account).build();
        }
        return Response.ok(NOT_FOUND).build();
    }

    @Path("/account")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response createAccount(Account account) {
        return Response.ok(accountDao.createAccount(account)).build();
    }

    @Path("/account/{id}")
    @Consumes("application/json-patch+json")
    @PATCH
    public Response updateAccount(Account account) {
        return Response.ok(accountDao.updateAccount(account)).build();
    }

    @Path("/account/{id}/clone")
    @POST
    public Response cloneAccount(@PathParam("id") String accountId) {
        return Response.ok(accountDao.clone(accountId)).build();
    }
}
