package br.com.sgpf.common.domain.dataimport;

import org.junit.Test;

import br.com.sgpf.common.test.PojoTester;

public class DataImportResultTest {

	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportResult.class);
		pojoTester.validatePojo(PojoTester.getLooseRules(), PojoTester.getStrictTesters());	
	}
}
