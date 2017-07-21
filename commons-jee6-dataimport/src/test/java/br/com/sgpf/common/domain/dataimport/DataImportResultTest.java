package br.com.sgpf.common.domain.dataimport;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.sgpf.common.test.PojoTester;

@RunWith(MockitoJUnitRunner.class)
public class DataImportResultTest {

	@Test
	public void constructorTest() {
		DataImportResult dir = new DataImportResult();
		
		assertNull(dir.getStatus());
		assertFalse(dir.isSynced());
		assertNull(dir.getMessage());
		assertNull(dir.getException());
	}

	@Test
	public void getSetTest() {
		PojoTester pojoTester =  new PojoTester(DataImportResult.class);
		pojoTester.loosePojoStructAndBehaviourTest();	
	}
}
