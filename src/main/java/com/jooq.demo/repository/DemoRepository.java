package com.jooq.demo.repository;

import com.jooq.demo.mapper.AccountRecordMapper;
import com.jooq.demo.mapper.RecordAccountMapper;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class DemoRepository {

  private final DSLContext create;
  private final AccountRecordMapper accountRecordMapper;
  private final RecordAccountMapper recordAccountMapper;

  public DemoRepository(
      DSLContext create, AccountRecordMapper accountRecordMapper,
      RecordAccountMapper recordAccountMapper
  ) {
    this.create = create;
    this.accountRecordMapper = accountRecordMapper;
    this.recordAccountMapper = recordAccountMapper;
  }

  //CRUD ---------------------------------------------------------------------
  public List<Long> createAccount() {
    return create.insertInto(ACCOUNT, ACCOUNT.LAST_NAME, ACCOUNT.FIRS_NAME, ACCOUNT.EMAIL,
            ACCOUNT.USER_NAME, ACCOUNT.PASSWORD, ACCOUNT.ROLE_ID
        )
        .values("Marly1", "Bob1", "bob1@a.com", "bob1", "bobbobo", 1L)
        .values("Marly12", "Bob2", "bob2@a.com", "bob2", "bobbobo", 1L)
        .returning(ACCOUNT.ID)
        .fetch()
        .map(AccountRecord::getId);
  }

  public int updateAccount() {
    int updatedRecords = create.update(ACCOUNT)
        .set(ACCOUNT.LAST_NAME, "1")
        .set(ACCOUNT.FIRS_NAME, "2")
        .set(ACCOUNT.EMAIL, "bob@boby.com")
        .set(ACCOUNT.USER_NAME, "bobibob")
        .set(ACCOUNT.PASSWORD, "bobibob")
        .where(ACCOUNT.LAST_NAME.eq("Marly"))
        .execute();

    return updatedRecords;
  }

  public Account getAccount() {
    return create.select(ACCOUNT.ID, ACCOUNT.LAST_NAME, ACCOUNT.FIRS_NAME, ACCOUNT.USER_NAME,
            ACCOUNT.EMAIL, ROLE.ROLE_NAME)
        .from(ACCOUNT)
        .leftJoin(ROLE).on(ACCOUNT.ROLE_ID.eq(ROLE.ID))
        .where(ACCOUNT.LAST_NAME.eq("1"))
        .fetchOne()
        .map(record -> new Account(
            record.getValue(ACCOUNT.ID),
            record.getValue(ACCOUNT.FIRS_NAME),
            record.getValue(ACCOUNT.LAST_NAME),
            record.getValue(ACCOUNT.EMAIL),
            record.getValue(ACCOUNT.USER_NAME),
            null,
            record.getValue(ROLE.ROLE_NAME)
        ));
  }

  public int deleteAccount() {
    return create.delete(ACCOUNT)
        .where(ACCOUNT.LAST_NAME.eq("Marly1"))
        .execute();
  }

  // with mapper
  public Account selectAccountWithMapper(Long id) {
    return create.selectFrom(ACCOUNT)
        .where(ACCOUNT.ID.eq(id))
        .fetchAny()
        .map(record -> recordAccountMapper.map((AccountRecord) record));
  }



  public long createAccountAndRoleInTransaction() {
    return create.transactionResult(configuration -> {
      var dsl = configuration.dsl();
      AccountRecord record = dsl.insertInto(ACCOUNT)
          .set(ACCOUNT.LAST_NAME, "Marly")
          .set(ACCOUNT.FIRS_NAME, "Bob")
          .set(ACCOUNT.EMAIL, "bob@boby.com")
          .set(ACCOUNT.USER_NAME, "bobibob")
          .set(ACCOUNT.PASSWORD, "bobibob")
          .set(ACCOUNT.ROLE_ID, 1L)
          .returning(ACCOUNT.ID)
          .fetchOne();

      dsl.insertInto(ROLE)
          .set(ROLE.ROLE_NAME, "DEMO")
          .execute();

      return record.getId();
    });
  }


  public void batchInsertAccount() {
    create.batch(
            create.insertInto(ACCOUNT)
                .set(ACCOUNT.LAST_NAME, "Marly1")
                .set(ACCOUNT.FIRS_NAME, "Bob")
                .set(ACCOUNT.EMAIL, "bob@boby.com")
                .set(ACCOUNT.USER_NAME, "bobibob1")
                .set(ACCOUNT.PASSWORD, "bobibob")
                .set(ACCOUNT.ROLE_ID, 1L),
            create.insertInto(ACCOUNT)
                .set(ACCOUNT.LAST_NAME, "Marly2")
                .set(ACCOUNT.FIRS_NAME, "Bob")
                .set(ACCOUNT.EMAIL, "bob@boby.com")
                .set(ACCOUNT.USER_NAME, "bobibob2")
                .set(ACCOUNT.PASSWORD, "bobibob")
                .set(ACCOUNT.ROLE_ID, 1L),
            create.insertInto(ACCOUNT)
                .set(ACCOUNT.LAST_NAME, "Marly3")
                .set(ACCOUNT.FIRS_NAME, "Bob")
                .set(ACCOUNT.EMAIL, "bob@boby.com")
                .set(ACCOUNT.USER_NAME, "bobibob3")
                .set(ACCOUNT.PASSWORD, "bobibob")
                .set(ACCOUNT.ROLE_ID, 1L))
        .execute();
  }

  public int[] batchUpdateAccount(List<Account> accountList) {
    return create.batchUpdate(accountList
            .stream()
            .map(account -> {
              var accountUpdated = new AccountRecord();
              accountUpdated.setLastName("Bla bla");
              accountUpdated.setId(account.id());
              // Prevent setting the ID to itself
              accountUpdated.changed(ACCOUNT.ID, false);
              return accountUpdated;
            })
            .toList())
        .execute();
  }

  // CTE
  private List<AccountStore> selectWithCommonTableExpression(Long id) {

    var STORE_CTE = DSL.name("store_cte")
        .fields("store_id", "store_name", "account_id")
        .as(
            DSL.select(
                    STORE.ID,
                    STORE.NAME,
                    STORE.ACCOUNT_ID
                )
                .from(STORE)
                .where(
                    STORE.ACCOUNT_ID.eq(1L)
                )
        );

    Field<Long> STORE_I = STORE_CTE
        .field("store_id", Long.class);
    Field<String> STORE_NAME = STORE_CTE
        .field("store_name", String.class);
    Field<Long> ACCOUNT_ID_CTE = STORE_CTE
        .field("account_id", Long.class);

    return create.with(STORE_CTE).select(
            ACCOUNT.ID, ACCOUNT.FIRS_NAME, ACCOUNT.USER_NAME, ACCOUNT.LAST_NAME,
            ACCOUNT.EMAIL
        )
        .from(ACCOUNT)
        .leftJoin(STORE_CTE).on(ACCOUNT_ID_CTE.eq(ACCOUNT.ID))
        .where(ACCOUNT.ID.eq(id))
        .orderBy(ACCOUNT.EMAIL)
        .fetch()
        .map(record -> new AccountStore(
                record.getValue(ACCOUNT.ID),
                record.getValue(ACCOUNT.FIRS_NAME),
                record.getValue(ACCOUNT.USER_NAME),
                record.getValue(ACCOUNT.LAST_NAME),
                record.getValue(ACCOUNT.EMAIL),
                record.getValue(STORE_NAME),
                record.getValue(STORE_I)
            )
        );
  }

}
