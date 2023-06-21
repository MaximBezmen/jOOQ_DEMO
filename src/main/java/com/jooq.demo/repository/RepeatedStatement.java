package com.adstore.repository;

import org.jooq.DiagnosticsContext;
import org.jooq.impl.DefaultDiagnosticsListener;
import org.springframework.stereotype.Component;

// A custom DiagnosticsListener SPI implementation
//@Component
class RepeatedStatement extends DefaultDiagnosticsListener {
  @Override
  public void duplicateStatements(DiagnosticsContext ctx){
    // The statement that is being executed and which has duplicates
    System.out.println("Actual statement: " + ctx.actualStatement());

    // A normalised version of the actual statement, which is shared by all duplicates
    // This statement has "normal" whitespace, bind variables, IN-lists, etc.
    System.out.println("Normalised statement: " + ctx.normalisedStatement());

    // All the duplicate actual statements that have produced the same normalised
    // statement in the recent past.
    System.out.println("Repeated statements: " + ctx.repeatedStatements());
  }

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
}
