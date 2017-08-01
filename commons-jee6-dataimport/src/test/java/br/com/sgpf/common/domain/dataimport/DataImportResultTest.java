/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import org.junit.Test;

import br.com.sgpf.common.test.pojo.PojoTester;

public class DataImportResultTest {

	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportResult.class);
		pojoTester.validatePojo(PojoTester.getLooseRules(), PojoTester.getStrictTesters());	
	}
}
