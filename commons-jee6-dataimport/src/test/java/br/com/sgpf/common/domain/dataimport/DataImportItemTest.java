package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.DELETED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.ERROR;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.FORCE_UPDATED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.IGNORED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.INSERTED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.OVERRIDDEN;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.UPDATED;
import static br.com.sgpf.common.test.PojoTester.UNMUTABLE_RULES;
import static br.com.sgpf.common.test.PojoTester.UNMUTABLE_TESTERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import br.com.sgpf.common.test.EqualsTester;
import br.com.sgpf.common.test.PojoTester;

public class DataImportItemTest {

	@Test
	public void constructorTest() {
		DataImportItem<Integer, Integer> dataImportItem = new DataImportItem<>(1, 1, true, true, true, true, true, true);
		
		assertEquals(Integer.valueOf(1), dataImportItem.getId());
		assertEquals(Integer.valueOf(1), dataImportItem.getData());
		assertTrue(dataImportItem.isInsert());
		assertTrue(dataImportItem.isUpdate());
		assertTrue(dataImportItem.isMerge());
		assertTrue(dataImportItem.isRemove());
		assertTrue(dataImportItem.isForce());
		assertTrue(dataImportItem.isSync());
	}
	
	@Test
	public void pojoTest() {
		PojoTester pojoTester =  new PojoTester(DataImportItem.class);
		pojoTester.validatePojo(UNMUTABLE_RULES, UNMUTABLE_TESTERS);	
	}
	
	@Test
	public void equalsHashCodeTest() {
		class DataImportItemSub<ID extends Serializable, T extends Serializable> extends DataImportItem<ID, T> {
			private static final long serialVersionUID = 1L;

			public DataImportItemSub(ID id, T data, Boolean insert, Boolean update, Boolean merge, Boolean remove, Boolean force, Boolean sync) {
				super(id, data, insert, update, merge, remove, force, sync);
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
		DataImportItem<?, ?> dataImportItem = new DataImportItem<>(null, null, false, false, false, false, false, false);
		
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
