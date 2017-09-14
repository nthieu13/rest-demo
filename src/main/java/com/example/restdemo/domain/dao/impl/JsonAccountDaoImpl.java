package com.example.restdemo.domain.dao.impl;

import com.example.restdemo.domain.dao.AccountDao;
import com.example.restdemo.domain.entity.Account;
import com.example.restdemo.domain.util.AccountSearchCriteria;
import com.example.restdemo.domain.util.GenericBuilder;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Named
public class JsonAccountDaoImpl implements AccountDao {
    private static final Logger log = LoggerFactory.getLogger(JsonAccountDaoImpl.class);

    private static final String DATA_FILE = "/account-data.json";
    private static final Map<String, Account> cache = new ConcurrentHashMap<>();
    private static boolean cacheLoaded = false;

    @Override
    public Optional<Account> getAccountById(String accountId) {
        return Optional.ofNullable(getCache().get(accountId));
    }

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(getCache().values());
    }

    @Override
    public Account createAccount(Account account) {
        String id = UUID.randomUUID().toString();
        account.setId(id);
        cache.put(id, account);
        flushDataToFile();
        return account;
    }

    @Override
    public Account updateAccount(Account account) {
        cache.put(account.getId(), account);
        flushDataToFile();
        return account;
    }

    @Override
    public List<Account> search(AccountSearchCriteria criteria) {
        return cache.values()
                .stream()
                .filter(criteria.toPredicate())
                .collect(Collectors.toList());
    }

    @Override
    public Account clone(String accountId) {
        Account exitedAccount = getAccountById(accountId).orElse(null);
        if (exitedAccount == null) {
            throw new IllegalArgumentException("invalid account id");
        }

        Account cloneAccount = GenericBuilder.of(Account::new)
                .with(Account::setCustomerName, exitedAccount.getCustomerName())
                .with(Account::setAmount, exitedAccount.getAmount())
                .with(Account::setCurrency, exitedAccount.getCurrency())
                .build();

        return createAccount(cloneAccount);
    }

    private static Map<String, Account> getCache() {
        if (!cacheLoaded) {
            loadDataFromFile();
            cacheLoaded = true;
        }
        return cache;
    }

    private static void loadDataFromFile() {
        try(InputStream is = JsonAccountDaoImpl.class.getResourceAsStream(DATA_FILE)) {
            String dataStr = IOUtils.toString(is, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(dataStr).get("accounts");
            if (root.isArray()) {
                for (JsonNode node: root) {
                    Account account = mapper.convertValue(node, Account.class);
                    cache.put(account.getId(), account);
                }
            }
        } catch (IOException  e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void flushDataToFile() {
        try(FileOutputStream out = new FileOutputStream(JsonAccountDaoImpl.class.getResource(DATA_FILE).getFile())) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            ObjectNode root = mapper.createObjectNode();
            ArrayNode data = mapper.valueToTree(cache.values());
            root.putArray("accounts").addAll(data);
            writer.writeValue(out, root);
        } catch (IOException  e) {
            log.error(e.getMessage(), e);
        }
    }
}
