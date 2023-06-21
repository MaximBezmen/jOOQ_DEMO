package com.jooq.demo.mapper;


import com.jooq.demo.Account;
import org.springframework.stereotype.Component;


@Component
public class RecordAccountMapper implements RecordMapper<AccountRecord, Account> {

  @Override
  public Account map(AccountRecord record) {
    return new Account(
        record.getId(), record.getLastName(), record.getFirsName(), record.getUserName(),
        record.getEmail(), null, record.getRoleId().toString()
    );
  }
}
