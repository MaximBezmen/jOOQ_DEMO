package com.adstore.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.DiagnosticsContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDiagnosticsListener;
import org.springframework.stereotype.Component;


@Component
public class N1Repository {

  private final DSLContext create;

  public N1Repository(DSLContext create) {
    this.create = create;
  }


  @SneakyThrows
  public void n1() {
    // Configuration is configured with the target DataSource, SQLDialect, etc. for instance Oracle.
    try (Connection c = getDiagnosticsConnection();
        Statement s1 = c.createStatement();
        ResultSet a = s1.executeQuery("SELECT id FROM store")) {

      while (a.next()) {
        int id = a.getInt(1);

        // This query is run once for every author, when we could have joined the author table
        try (PreparedStatement s2 = c.prepareStatement(
            "SELECT title FROM store_ad WHERE store_id = ?")) {
          s2.setInt(1, id);

          try (ResultSet b = s2.executeQuery()) {
            while (b.next()) {
              System.out.println("ID: " + id + ", title: " + b.getString(1));
            }
          }
        }
      }
    }
  }

  private Connection getDiagnosticsConnection() {
    DSLContext ctx = DSL.using(create.configuration().set(new DefaultDiagnosticsListener() {
      @Override
      public void repeatedStatements(DiagnosticsContext ctx) {

        // The statement that is being executed and which has duplicates
        System.out.println("Actual statement: " + ctx.actualStatement());

        // A normalised version of the actual statement, which is shared by all duplicates
        // This statement has "normal" whitespace, bind variables, IN-lists, etc.
        System.out.println("Normalised statement: " + ctx.normalisedStatement());

        // All the duplicate actual statements that have produced the same normalised
        // statement in the recent past.
        System.out.println("Repeated statements: " + ctx.repeatedStatements());
      }
    }));

    return ctx.diagnosticsConnection();

  }

}
