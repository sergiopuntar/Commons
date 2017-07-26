package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractUUIDEntity;
import br.com.sgpf.common.domain.entity.EntityUtil;

public class UUIDEntitySimpleSheetDataSourceTest2 {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/UUIDEntitySimpleSheetDataSourceTest.xls");
	
	private static final int SHEET_INDEX = 0;
	
	private static enum TestColumns { ID };
	
	private static final String ID_VALUE = "C7E2F819-3383-4399-B8ED-707F59C96929";
	
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
	public void readEntityIdTest() throws DataImportException, FileNotFoundException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new EntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		DataImportItem<Integer, EntityElement> importItem = entitySimpleSheetDataSource.next();
		
		assertEquals(ID_VALUE, importItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).readEntityId(TestColumns.ID.name());
		
		entitySimpleSheetDataSource.close();
	}
	
	@Test
	public void writeEntityIdTest() throws DataImportException, FileNotFoundException {
		EntitySimpleSheetDataSourceImpl entitySimpleSheetDataSource = Mockito.spy(new EntitySimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX));
		entitySimpleSheetDataSource.open();
		
		String newId = EntityUtil.generateUUID();
		
		DataImportItem<Integer, EntityElement> importItem = entitySimpleSheetDataSource.next();
		assertEquals(ID_VALUE, importItem.getData().getId());
		
		importItem.getData().setId(newId);
		
		entitySimpleSheetDataSource.sync(importItem);
		DataImportItem<Integer, EntityElement> updatedImportItem = entitySimpleSheetDataSource.current();
		
		assertEquals(newId, updatedImportItem.getData().getId());
		Mockito.verify(entitySimpleSheetDataSource).writeEntityId(1, TestColumns.ID.name(), newId);
		
		// Desfaz as alterações
		updatedImportItem.getData().setId(ID_VALUE);
		entitySimpleSheetDataSource.sync(updatedImportItem);
		
		entitySimpleSheetDataSource.close();
	}

	class EntitySimpleSheetDataSourceImpl extends UUIDEntitySimpleSheetDataSource<EntityElement> {
		private static final long serialVersionUID = 1L;

		public EntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
			super(file, sheetId);
		}

		public EntitySimpleSheetDataSourceImpl(InputStream is, int sheetId) {
			super(is, sheetId);
		}

		@Override
		protected EntityElement createEntityInstance() {
			return new EntityElement();
		}

		@Override
		protected boolean writeItemData(Integer rowIndex, EntityElement data) {
			return false;
		}
	};
	
	class EntityElement extends AbstractUUIDEntity {
		private static final long serialVersionUID = 1L;
		
		public EntityElement() {
			super();
		}
		
		public EntityElement(String id, Date creationDate, Date updateDate, Long versao) {
			this();
			setId(id);
			setCreationDate(creationDate);
			setUpdateDate(updateDate);
			setVersion(versao);
		}

		@Override
		public boolean canEqual(Object obj) {
			return obj instanceof EntityElement;
		}
	}
}
