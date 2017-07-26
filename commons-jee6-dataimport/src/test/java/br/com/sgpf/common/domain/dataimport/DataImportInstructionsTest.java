package br.com.sgpf.common.domain.dataimport;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.sgpf.common.test.PojoTester;

public class DataImportInstructionsTest {

	@Test
	public void constructorTest() {
		DataImportInstructions dataImportInstructions = new DataImportInstructions(true, true, true, true, true, true);
		assertTrue(dataImportInstructions.isInsert());
		assertTrue(dataImportInstructions.isUpdate());
		assertTrue(dataImportInstructions.isMerge());
		assertTrue(dataImportInstructions.isRemove());
		assertTrue(dataImportInstructions.isForce());
		assertTrue(dataImportInstructions.isSync());
	}
	
	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportInstructions.class);
		pojoTester.validatePojo(PojoTester.getStrictRules(), PojoTester.getLooseTesters());	
	}
}