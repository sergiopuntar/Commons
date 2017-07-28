package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.entity.AbstractUUIDEntityImpl;
import br.com.sgpf.common.domain.entity.EntityUtil;

public class UUIDEntitySimpleSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/UUIDEntitySimpleSheetDataSourceTest.xls");
	
	private static final int SHEET_INDEX = 0;
	
	private static enum TestColumns { ID };
	
	private static final String ID_VALUE = "C7E2F819-3383-4399-B8ED-707F59C96929";
	
	@Test
	public void fileConstructorTest() throws DataImportException {
		UUIDEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new UUIDEntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void inputStreamConstructorTest() throws DataImportException, FileNotFoundException {
		UUIDEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new UUIDEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void readEntityIdTest() throws DataImportException, FileNotFoundException {
		UUIDEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new UUIDEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, AbstractUUIDEntityImpl> importItem = entitySimpleSheetDataSource.next();
		
		assertEquals(ID_VALUE, importItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).readEntityId(TestColumns.ID.name());
		
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void writeEntityIdTest() throws DataImportException, FileNotFoundException {
		UUIDEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new UUIDEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		String newId = EntityUtil.generateUUID();
		
		DataImportItem<Integer, AbstractUUIDEntityImpl> importItem = entitySimpleSheetDataSource.next();
		assertEquals(ID_VALUE, importItem.getData().getId());
		
		importItem.getData().setId(newId);
		
		entitySimpleSheetDataSource.sync(importItem);
		DataImportItem<Integer, AbstractUUIDEntityImpl> updatedImportItem = entitySimpleSheetDataSource.current();
		
		assertEquals(newId, updatedImportItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).writeEntityId(1, TestColumns.ID.name(), newId);
		
		// Desfaz as alterações
		updatedImportItem.getData().setId(ID_VALUE);
		entitySimpleSheetDataSource.sync(updatedImportItem);
		
		entitySimpleSheetDataSource.close();
	}
}
