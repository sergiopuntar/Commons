package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractEntity;

public class EntitySimpleSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/EntitySimpleSheetDataSourceTest.xls");
	
	private static final int SHEET_INDEX = 0;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private static enum TestColumns {ID, CREATION_DATE, UPDATE_DATE, VERSION };
	
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
		
		DataElement expectedEntity = new DataElement(0L, 0L);
		expectedEntity.setCreationDate(DATE_FORMAT.parse("01/01/2017"));
		expectedEntity.setUpdateDate(DATE_FORMAT.parse("01/01/2017"));
		DataImportInstructions instructions = new DataImportInstructions(false, false, true, false, false, true);
		DataImportItem<Integer, DataElement> expectedImportItem = new DataImportItem<Integer, EntitySimpleSheetDataSourceTest.DataElement>(1, expectedEntity, instructions);
		
		assertEquals(expectedImportItem, entitySimpleSheetDataSource.next());
		Mockito.verify(entitySimpleSheetDataSource).readCurrentItemData();
		
		entitySimpleSheetDataSource.close();
	}
	
	//@Test
	public void syncRowTest() throws DataImportException, ParseException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = new EntitySimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		entitySimpleSheetDataSource.open();
		
		DataElement expectedEntity = new DataElement(1L, 1L);
		expectedEntity.setCreationDate(DATE_FORMAT.parse("10/01/2017"));
		expectedEntity.setUpdateDate(DATE_FORMAT.parse("10/01/2017"));
		DataImportInstructions instructions = new DataImportInstructions(false, false, true, false, false, true);
		DataImportItem<Integer, DataElement> expectedImportItem = new DataImportItem<Integer, EntitySimpleSheetDataSourceTest.DataElement>(1, expectedEntity, instructions);
		
		DataImportItem<Integer, DataElement> importItem = entitySimpleSheetDataSource.next();
		assertEquals(Long.valueOf(1L), importItem.getData().getId());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getCreationDate());
		assertEquals(DATE_FORMAT.parse("01/01/2017"), importItem.getData().getUpdateDate());
		assertEquals(Long.valueOf(1L), importItem.getData().getVersion());
		
		importItem.getData().setId(1L);
		importItem.getData().setCreationDate(DATE_FORMAT.parse("10/01/2017"));
		importItem.getData().setUpdateDate(DATE_FORMAT.parse("10/01/2017"));
		importItem.getData().setVersion(1L);
		
		entitySimpleSheetDataSource.sync(importItem);
		
		// TODO comparar item gravado
		
		entitySimpleSheetDataSource.close();
	}

	class EntitySimpleSheetDataSourceImpl extends EntitySimpleSheetDataSource<Long, DataElement> {
		private static final long serialVersionUID = 1L;

		public EntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
			super(file, sheetId);
		}

		public EntitySimpleSheetDataSourceImpl(InputStream is, int sheetId) {
			super(is, sheetId);
		}

		@Override
		protected Long readEntityId(String columnName) {
			return readLongCell(TestColumns.ID.name());
		}

		@Override
		protected DataElement readCurrentItemData(Long id, Date creationDate, Date updateDate, Long version) {
			DataElement entity = new DataElement(id, version);
			entity.setCreationDate(creationDate);
			entity.setUpdateDate(updateDate);
			return null;
		}

		@Override
		protected void writeEntityId(Integer rowIndex, String columnName, Long id) {
			
		}

		@Override
		protected void writeItemData(Integer rowIndex, DataElement data) {
			
		}
	};
	
	class DataElement extends AbstractEntity<Long> {
		private static final long serialVersionUID = 1L;
		
		private Long id;
		
		public DataElement(Long id, Long versao) {
			super();
			this.id = id;
			setVersion(versao);
		}

		@Override
		public Long getId() {
			return id;
		}

		@Override
		public void setId(Long id) {
			this.id = id;
		}
		
		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return id.equals(((DataElement)obj).getId());
		}

		@Override
		public boolean canEqual(Object obj) {
			return obj instanceof DataElement;
		}
	}
}
