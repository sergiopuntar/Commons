package br.com.sgpf.common.domain.dataimport;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import nl.jqno.equalsverifier.EqualsVerifier;

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
	public void equalsTest() {
		class DataImportResultSub extends DataImportResult {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canEqual(Object obj) {
				return obj instanceof DataImportResultSub;
			}
		}
		
		EqualsVerifier.forClass(DataImportResult.class)
			.withRedefinedSubclass(DataImportResultSub.class)
			.verify();
	}
}
