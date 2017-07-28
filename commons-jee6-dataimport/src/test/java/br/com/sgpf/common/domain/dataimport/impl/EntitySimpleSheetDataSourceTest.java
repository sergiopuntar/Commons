package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.entity.AbstractEntityImpl;

public class EntitySimpleSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/EntitySimpleSheetDataSourceTest.xls");
	
	private static final int SHEET_INDEX = 0;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private static enum TestColumns { ID };
	
	@Test
	public void fileConstructorTest() throws DataImportException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new EntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void inputStreamConstructorTest() throws DataImportException, FileNotFoundException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new EntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void readCurrentItemDataTest() throws DataImportException, ParseException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new EntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, AbstractEntityImpl> importItem = entitySimpleSheetDataSource.next();
		assertEquals(Long.valueOf(0L), importItem.getData().getId());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getCreationDate());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getUpdateDate());
		assertEquals(Long.valueOf(0L), importItem.getData().getVersion());
		
		Mockito.verify(entitySimpleSheetDataSource).readCurrentItemData();
		
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void syncRowTest() throws DataImportException, ParseException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new EntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, AbstractEntityImpl> importItem = entitySimpleSheetDataSource.next();
		assertEquals(Long.valueOf(0L), importItem.getData().getId());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getCreationDate());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getUpdateDate());
		assertEquals(Long.valueOf(0L), importItem.getData().getVersion());
		
		importItem.getData().setId(1L);
		importItem.getData().setCreationDate(DATE_FORMAT.parse("10/01/2017"));
		importItem.getData().setUpdateDate(DATE_FORMAT.parse("10/01/2017"));
		importItem.getData().setVersion(1L);
		
		entitySimpleSheetDataSource.sync(importItem);
		DataImportItem<Integer, AbstractEntityImpl> updatedImportItem = entitySimpleSheetDataSource.current();
		
		Mockito.verify(entitySimpleSheetDataSource).writeEntityId(importItem.getId(), TestColumns.ID.name(), Long.valueOf(1L));
		assertEquals(DATE_FORMAT.parse("10/01/2017"), updatedImportItem.getData().getCreationDate());
		assertEquals(DATE_FORMAT.parse("10/01/2017"), updatedImportItem.getData().getUpdateDate());
		assertEquals(Long.valueOf(1L), updatedImportItem.getData().getVersion());
		Mockito.verify(entitySimpleSheetDataSource).writeItemData(importItem.getId(), importItem.getData());
		
		// Desfaz as alterações
		updatedImportItem.getData().setId(0L);
		updatedImportItem.getData().setCreationDate(DATE_FORMAT.parse("01/01/2017"));
		updatedImportItem.getData().setUpdateDate(DATE_FORMAT.parse("01/01/2017"));
		updatedImportItem.getData().setVersion(0L);
		entitySimpleSheetDataSource.sync(updatedImportItem);
		
		entitySimpleSheetDataSource.close();
	}
}
