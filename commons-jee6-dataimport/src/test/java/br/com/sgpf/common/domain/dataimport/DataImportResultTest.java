/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.test.pojo.PojoTester.looseRules;
import static br.com.sgpf.common.test.pojo.PojoTester.strictTesters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import br.com.sgpf.common.domain.dataimport.DataImportResult.Status;
import br.com.sgpf.common.test.pojo.PojoTester;

public class DataImportResultTest {

	@Test
	public void constructorTest() {
		String message = "Mensagem de erro";
		Exception exception = new Exception("Mensagem da exceção");
		DataImportResult dataImportResult =  new DataImportResult(message, exception);
		
		assertEquals(message, dataImportResult.getMessage());
		assertEquals(exception, dataImportResult.getException());
		assertEquals(Status.ERROR, dataImportResult.getStatus());
		assertFalse(dataImportResult.isSynced());
	}
	
	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportResult.class);
		pojoTester.validatePojo(looseRules(), strictTesters());	
	}
}
