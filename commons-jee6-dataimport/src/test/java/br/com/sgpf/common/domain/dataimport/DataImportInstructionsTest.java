/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.test.pojo.PojoTester.looseTesters;
import static br.com.sgpf.common.test.pojo.PojoTester.strictRules;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.sgpf.common.test.pojo.PojoTester;

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
		pojoTester.validatePojo(strictRules(), looseTesters());	
	}
}
