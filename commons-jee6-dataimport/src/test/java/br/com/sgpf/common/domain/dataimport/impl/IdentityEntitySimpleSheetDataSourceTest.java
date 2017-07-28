package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.entity.AbstractIdentityEntityImpl;

public class IdentityEntitySimpleSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/IdentityEntitySimpleSheetDataSourceTest.xls");
	
	private static final int SHEET_INDEX = 0;
	
	private static enum TestColumns { ID };
	
	@Test
	public void fileConstructorTest() throws DataImportException {
		IdentityEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new IdentityEntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void inputStreamConstructorTest() throws DataImportException, FileNotFoundException {
		IdentityEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new IdentityEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void readEntityIdTest() throws DataImportException, FileNotFoundException {
		IdentityEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new IdentityEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, AbstractIdentityEntityImpl> importItem = entitySimpleSheetDataSource.next();
		
		assertEquals(Long.valueOf(0L), importItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).readEntityId(TestColumns.ID.name());
		
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void writeEntityIdTest() throws DataImportException, FileNotFoundException {
		IdentityEntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new IdentityEntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, AbstractIdentityEntityImpl> importItem = entitySimpleSheetDataSource.next();
		assertEquals(Long.valueOf(0L), importItem.getData().getId());
		
		importItem.getData().setId(1L);
		
		entitySimpleSheetDataSource.sync(importItem);
		DataImportItem<Integer, AbstractIdentityEntityImpl> updatedImportItem = entitySimpleSheetDataSource.current();
		
		assertEquals(Long.valueOf(1), updatedImportItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).writeEntityId(1, TestColumns.ID.name(), 1L);
		
		// Desfaz as alterações
		updatedImportItem.getData().setId(0L);
		entitySimpleSheetDataSource.sync(updatedImportItem);
		
		entitySimpleSheetDataSource.close();
	}
}
