package com.jooq.demo.mapper;

import com.adstore.dto.account.Account;
import org.springframework.stereotype.Component;


@Component
public class AccountRecordMapper implements RecordUnmapper<Account, AccountRecord> {

  @Override
  public AccountRecord unmap(Account source) {
    AccountRecord record = new AccountRecord();
    record.setLastName(source.lastName());
    record.setFirsName(source.firstName());
    record.setEmail(source.email());
    record.setUserName(source.userName());
    record.setPassword("dummy");
    record.setRoleId(1L);
    return record;
  }
}
