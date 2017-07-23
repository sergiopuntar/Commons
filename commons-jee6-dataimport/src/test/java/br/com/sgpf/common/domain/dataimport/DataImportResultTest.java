package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.test.PojoTester.LOOSE_RULES;
import static br.com.sgpf.common.test.PojoTester.STRICT_TESTERS;

import org.junit.Test;

import br.com.sgpf.common.test.PojoTester;

public class DataImportResultTest {

	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportResult.class);
		pojoTester.validatePojo(LOOSE_RULES, STRICT_TESTERS);	
	}
}
