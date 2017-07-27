package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.DELETED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.ERROR;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.FORCE_UPDATED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.IGNORED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.INSERTED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.OVERRIDDEN;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.UPDATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import br.com.sgpf.common.test.equals.EqualsTester;

public class DataImportItemTest {

	@Test
	public void constructorTest() {
		DataImportInstructions dataImportInstructions = new DataImportInstructions(true, true, true, true, true, true);
		DataImportItem<Integer, Integer> dataImportItem = new DataImportItem<>(1, 1, dataImportInstructions);
		
		assertEquals(Integer.valueOf(1), dataImportItem.getId());
		assertEquals(Integer.valueOf(1), dataImportItem.getData());
		assertEquals(dataImportInstructions.isInsert(), dataImportItem.isInsert());
		assertEquals(dataImportInstructions.isUpdate(), dataImportItem.isUpdate());
		assertEquals(dataImportInstructions.isMerge(), dataImportItem.isMerge());
		assertEquals(dataImportInstructions.isRemove(), dataImportItem.isRemove());
		assertEquals(dataImportInstructions.isForce(), dataImportItem.isForce());
		assertEquals(dataImportInstructions.isSync(), dataImportItem.isSync());
	}
	
	@Test
	public void equalsHashCodeTest() {
		class DataImportItemSub<ID extends Serializable, T extends Serializable> extends DataImportItem<ID, T> {
			private static final long serialVersionUID = 1L;

			public DataImportItemSub(ID id, T data, DataImportInstructions dataImportInstructions) {
				super(id, data, dataImportInstructions);
			}

			@Override
			public boolean canEqual(Object obj) {
				return obj instanceof DataImportItemSub;
			}
		};
		
		EqualsTester<?> equalsTester = new EqualsTester<>(DataImportItem.class, false, DataImportItemSub.class, "id");
		equalsTester.validate();
	}
	
	@Test
	public void dataChangedTest() {
		DataImportInstructions dataImportInstructions = new DataImportInstructions(false, false, false, false, false, false);
		DataImportItem<?, ?> dataImportItem = new DataImportItem<>(null, null, dataImportInstructions);
		
		assertFalse(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(INSERTED);
		assertTrue(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(UPDATED);
		assertTrue(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(FORCE_UPDATED);
		assertTrue(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(DELETED);
		assertTrue(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(OVERRIDDEN);
		assertTrue(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(ERROR);
		assertFalse(dataImportItem.dataChanged());
		
		dataImportItem.getResult().setStatus(IGNORED);
		assertFalse(dataImportItem.dataChanged());
	}
}
