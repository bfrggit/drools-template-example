package com.rhc.drools.build;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.decisiontable.InputType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.drools.FileUtils;
import com.rhc.drools.model.Loan;

public class TestLoanMaxAmountDynamic {
   private static final Logger logger = LoggerFactory.getLogger(TestLoanMaxAmountDynamic.class);

   private static final String KIE_BASE = "loanKieBase";
   private static final String DRT_PATH = "src/main/resources/com/rhc/drools/loan-template.drt";
   private static final String XLS_PATH = "src/main/resources/com/rhc/drools/LoanData.xls";
   private static final String DRL_PATH = "src/main/resources/com/rhc/drools/loan.drl";

   private KieContainer kieContainer;
   private KieBase kieBase;

   @BeforeEach
   public void setUp() throws IOException {
      logger.warn("\n\n-------------------- setUp -----------------------\n");

      KieFileSystem kieFileSystem = new KieFileSystemImpl();
      byte[] drtFile = FileUtils.readFileRaw(DRT_PATH);
      byte[] xlsFile = FileUtils.readFileRaw(XLS_PATH);
      final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
      final String drlFile = converter
            .compile(new ByteArrayInputStream(xlsFile), new ByteArrayInputStream(drtFile), InputType.XLS, 2, 1);
      logger.debug("Built DRL file:\n{}\n", drlFile.trim());
      kieFileSystem.write(DRL_PATH, drlFile);

      KieServices kieServices = KieServices.Factory.get();
      KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
      kieModuleModel.newKieBaseModel(KIE_BASE);
      kieFileSystem.writeKModuleXML(kieModuleModel.toXML());

      KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
      try {
         kieBuilder.buildAll();
      } catch (NullPointerException e) {
         e.printStackTrace();
         throw e;
      }
      kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
      kieBase = kieContainer.getKieBase(KIE_BASE);
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
