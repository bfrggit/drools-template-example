package com.rhc.drools.build;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.drools.model.Loan;

public class TestLoanMaxAmount {
   private static final Logger logger = LoggerFactory.getLogger(TestLoanMaxAmount.class);

   private static final String KIE_BASE = "loanKieBase";
   private KieContainer kieContainer;
   private KieBase kieBase;

   @BeforeEach
   public void setUp() {
      logger.warn("\n\n-------------------- setUp -----------------------\n");

      kieContainer = KieServices.Factory.get().getKieClasspathContainer();
      try {
         kieBase = kieContainer.getKieBase(KIE_BASE);
      } catch (NullPointerException e) {
         e.printStackTrace();
         throw e;
      }
   }

   @AfterEach
   public void tearDown() {
      logger.warn("\n\n-------------------- tearDown --------------------\n");

      kieContainer.dispose();
   }

   @Test
   public void testSimpleLoan() {
      Loan loan = new Loan();
      loan.setIdentifier("simple");
      KieSession kieSession;

      try {
         kieSession = kieBase.newKieSession();
      } catch (NullPointerException e) {
         e.printStackTrace();
         throw e;
      }

      kieSession.insert(loan);
      kieSession.fireAllRules();

      assertEquals((Integer) 1000, loan.getMaxAmount());
   }

   @Test
   public void testComplicatedLoan() {
      Loan loan = new Loan();
      loan.setIdentifier("complex");
      KieSession kieSession;

      try {
         kieSession = kieBase.newKieSession();
      } catch (NullPointerException e) {
         e.printStackTrace();
         throw e;
      }

      kieSession.insert(loan);
      kieSession.fireAllRules();

      assertEquals((Integer) 5000, loan.getMaxAmount());
   }
}