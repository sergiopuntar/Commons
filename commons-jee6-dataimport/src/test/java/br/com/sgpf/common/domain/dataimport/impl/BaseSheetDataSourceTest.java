package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceNoMoreItensException;

@RunWith(MockitoJUnitRunner.class)
public class BaseSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/BaseSheetDataSourceTest.xls");
	private static final File TEST_SHEET_FILE_UNREADABLE = Mockito.spy(TEST_SHEET_FILE);
	private static final File TEST_SHEET_FILE_UNWRITABLE = Mockito.spy(TEST_SHEET_FILE);
	
	private static final int SHEET_INDEX = 0;
	private static final int SHEET_INDEX_NOHEADER = 1;
	private static final int SHEET_INDEX_EMPTY = 2;
	private static final int SHEET_INDEX_INVALID = 99;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static enum TestColumns {ID, STRING, CHAR, DOUBLE, FLOAT, LONG, INTEGER, SHORT, BYTE, BOOLEAN, DATE, CALENDAR, YES, NO, INVALID}
	
	private static final String STRING_VALUE = "String";
	private static final Character CHAR_VALUE = Character.valueOf('C');
	private static final Double DOUBLE_VALUE = Double.valueOf(1.5);
	private static final Float FLOAT_VALUE = Float.valueOf(1.5F);
	private static final Long LONG_VALUE = Long.valueOf(1L);
	private static final Integer INTEGER_VALUE = Integer.valueOf(1);
	private static final Short SHORT_VALUE = Short.valueOf("1");
	private static final Byte BYTE_VALUE = Byte.valueOf("1");
	private static final Boolean BOOLEAN_VALUE = Boolean.TRUE;
	private static final Calendar CALENDAR_VALUE = Calendar.getInstance();
	private static final Boolean YES_VALUE = Boolean.TRUE;
	private static final Boolean NO_VALUE = Boolean.FALSE;
	
	@BeforeClass
	public static void beforeClass() throws ParseException {
		CALENDAR_VALUE.setTime(DATE_FORMAT.parse("01/01/2017"));
		Mockito.when(TEST_SHEET_FILE_UNREADABLE.canRead()).thenReturn(false);
		Mockito.when(TEST_SHEET_FILE_UNWRITABLE.canWrite()).thenReturn(false);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullInputStreamConstructorTest() throws IllegalArgumentException {
		InputStream is = null;
		new BaseSheetDataSourceImpl(is, SHEET_INDEX);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullFileConstructorTest() throws ImportDataSourceFileException {
		File file = null;
		new BaseSheetDataSourceImpl(file, SHEET_INDEX);
	}
	
	@Test(expected = ImportDataSourceFileException.class)
	public void invalidFileConstructorTest() throws ImportDataSourceFileException {
		new BaseSheetDataSourceImpl(new File("invalid.xls"), SHEET_INDEX);
	}
	
	@Test(expected = ImportDataSourceFileException.class)
	public void unreadableFileConstructorTest() throws ImportDataSourceException {
		TEST_SHEET_FILE_UNREADABLE.setReadable(false);
		try {
			new BaseSheetDataSourceImpl(TEST_SHEET_FILE_UNREADABLE, SHEET_INDEX);
		} catch (Exception e) {
			throw e;
		} finally {
			TEST_SHEET_FILE_UNREADABLE.setReadable(true);			
		}
	}
	
	@Test(expected = ImportDataSourceDocumentException.class)
	public void nonExistingSheetOpenTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_INVALID);
		sheetDataSource.open();
	}
	
	@Test(expected = ImportDataSourceException.class)
	public void nonExistingSheetHeaderOpenTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_NOHEADER);
		sheetDataSource.open();
	}
	
	@Test(expected = ImportDataSourceInvalidStateException.class)
	public void openAlreadyOpenDataSourceTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		sheetDataSource.open();
	}
	
	@Test
	public void openFileDataSourceTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		sheetDataSource.close();
	}
	
	@Test
	public void openInputStreamDataSourceTest() throws ImportDataSourceException, FileNotFoundException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		sheetDataSource.open();
		sheetDataSource.close();
	}
	
	
	@Test
	public void inputStreamIsWritableTest() throws FileNotFoundException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		
		assertFalse(sheetDataSource.isWritable());
	}
	
	@Test
	public void writeableFileIsWritableTest() throws ImportDataSourceFileException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		
		assertTrue(sheetDataSource.isWritable());
	}
	
	@Test
	public void nonWriteableFileIsWritableTest() throws ImportDataSourceFileException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE_UNWRITABLE, SHEET_INDEX);
		
		assertFalse(sheetDataSource.isWritable());
	}
	
	@Test(expected = ImportDataSourceInvalidStateException.class)
	public void closedDataSourceHasNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.hasNext();
	}
	
	@Test
	public void emptyDataSourceHasNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		sheetDataSource.open();
		
		assertFalse(sheetDataSource.hasNext());
		
		sheetDataSource.close();
	}
	
	@Test
	public void notEmptyDataSourceHasNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		assertTrue(sheetDataSource.hasNext());
		
		sheetDataSource.close();
	}
	
	@Test(expected = ImportDataSourceInvalidStateException.class)
	public void closedDataSourceNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.next();
	}
	
	@Test(expected = ImportDataSourceNoMoreItensException.class)
	public void emptyDataSourceNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		sheetDataSource.open();
		sheetDataSource.next();
	}
	
	@Test
	public void notEmptyDataSourceNextTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		DataImportItem<Integer, DataElement> item = sheetDataSource.next();
		
		assertEquals(Integer.valueOf(1), item.getId());
		assertEquals(new DataElement(0L), item.getData());
		assertFalse(item.isInsert());
		assertFalse(item.isUpdate());
		assertTrue(item.isMerge());
		assertFalse(item.isRemove());
		assertFalse(item.isForce());
		assertTrue(item.isSync());
		
		sheetDataSource.close();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readInvalidColumnTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		sheetDataSource.readCharCell(TestColumns.INVALID.name());
	}
	
	@Test(expected = ImportDataSourceFormatException.class)
	public void readInvalidCharFormatTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		// Pulo para a terceira linha de dados
		sheetDataSource.next();
		sheetDataSource.next();
		sheetDataSource.next();
		
		sheetDataSource.readCharCell(TestColumns.CHAR.name());
	}
	
	@Test(expected = ImportDataSourceFormatException.class)
	public void readInvalidYesNoFormatTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		// Pulo para a terceira linha de dados
		sheetDataSource.next();
		sheetDataSource.next();
		sheetDataSource.next();
		
		sheetDataSource.readCharCell(TestColumns.YES.name());
	}
	
	@Test
	public void readValuesTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		// Pulo para a primeira linha de dados
		sheetDataSource.next();

		assertEquals(STRING_VALUE, sheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertEquals(CHAR_VALUE, sheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertEquals(DOUBLE_VALUE, sheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertEquals(FLOAT_VALUE, sheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertEquals(LONG_VALUE, sheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertEquals(INTEGER_VALUE, sheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertEquals(SHORT_VALUE, sheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertEquals(BYTE_VALUE, sheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertTrue(sheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertEquals(CALENDAR_VALUE.getTime(), sheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertEquals(CALENDAR_VALUE, sheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertTrue(sheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertFalse(sheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		// Pulo para a segunda linha de dados
		sheetDataSource.next();
		
		assertNull(sheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(sheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(sheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(sheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(sheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(sheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(sheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(sheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(sheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(sheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(sheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		sheetDataSource.close();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeInvalidColumnTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		sheetDataSource.writeStringCell(2, TestColumns.INVALID.name(), STRING_VALUE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeInvalidRowTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		sheetDataSource.writeStringCell(99, TestColumns.STRING.name(), STRING_VALUE);
	}
	
	@Test
	public void writeValuesTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		// Pulo para a segunda linha de dados
		sheetDataSource.next();
		sheetDataSource.next();

		assertNull(sheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(sheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(sheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(sheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(sheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(sheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(sheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(sheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(sheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(sheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(sheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		sheetDataSource.writeStringCell(2, TestColumns.STRING.name(), STRING_VALUE);
		sheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), CHAR_VALUE);
		sheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), DOUBLE_VALUE);
		sheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), FLOAT_VALUE);
		sheetDataSource.writeLongCell(2, TestColumns.LONG.name(), LONG_VALUE);
		sheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), INTEGER_VALUE);
		sheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), SHORT_VALUE);
		sheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), BYTE_VALUE);
		sheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), BOOLEAN_VALUE);
		sheetDataSource.writeDateCell(2, TestColumns.DATE.name(), CALENDAR_VALUE.getTime());
		sheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), CALENDAR_VALUE);
		sheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), YES_VALUE);
		sheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), NO_VALUE);
		
		assertEquals(STRING_VALUE, sheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertEquals(CHAR_VALUE, sheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertEquals(DOUBLE_VALUE, sheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertEquals(FLOAT_VALUE, sheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertEquals(LONG_VALUE, sheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertEquals(INTEGER_VALUE, sheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertEquals(SHORT_VALUE, sheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertEquals(BYTE_VALUE, sheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertTrue(sheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertEquals(CALENDAR_VALUE.getTime(), sheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertEquals(CALENDAR_VALUE, sheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertTrue(sheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertFalse(sheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		sheetDataSource.writeStringCell(2, TestColumns.STRING.name(), null);
		sheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), null);
		sheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), null);
		sheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), null);
		sheetDataSource.writeLongCell(2, TestColumns.LONG.name(), null);
		sheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), null);
		sheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), null);
		sheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), null);
		sheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), null);
		sheetDataSource.writeDateCell(2, TestColumns.DATE.name(), null);
		sheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), null);
		sheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), null);
		sheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), null);

		assertNull(sheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(sheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(sheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(sheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(sheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(sheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(sheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(sheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(sheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(sheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(sheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(sheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		sheetDataSource.close();
	}
	
	@Test(expected = ImportDataSourceInvalidStateException.class)
	public void closedDataSourceSyncTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		DataImportItem<Integer, DataElement> item = new DataImportItem<Integer, BaseSheetDataSourceTest.DataElement>(1, new DataElement(1L), false, false, true, false, false, true);
		sheetDataSource.sync(item);
	}
	
	@Test(expected = ImportDataSourceInvalidStateException.class)
	public void closeAlreadyClosedDataSourceTest() throws ImportDataSourceException {
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, 0);
		sheetDataSource.open();
		sheetDataSource.close();
		sheetDataSource.close();
	}
	
	@Test
	public void closedChangedDataSourceTest() throws ImportDataSourceException {
		long before = TEST_SHEET_FILE.lastModified();
		
		BaseSheetDataSource<DataElement> sheetDataSource = new BaseSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		sheetDataSource.open();
		
		// Simulo uma mudança no conteúdo do arquivo
		((BaseSheetDataSourceImpl)sheetDataSource).setChangedRow(true);
		DataImportItem<Integer, DataElement> item = new DataImportItem<Integer, BaseSheetDataSourceTest.DataElement>(1, new DataElement(1L), false, false, true, false, false, true);
		sheetDataSource.sync(item);
		
		sheetDataSource.close();
		
		long after = TEST_SHEET_FILE.lastModified();
		
		assertTrue(after > before);
	}

	class BaseSheetDataSourceImpl extends BaseSheetDataSource<DataElement> {
		private static final long serialVersionUID = 1L;
		
		private boolean changedRow = false;

		public BaseSheetDataSourceImpl(File file, int sheetId) throws ImportDataSourceFileException {
			super(file, sheetId);
		}

		public BaseSheetDataSourceImpl(InputStream is, int sheetId) {
			super(is, sheetId);
		}

		public void setChangedRow(boolean changedRow) {
			this.changedRow = changedRow;
		}

		@Override
		protected DataElement readCurrentItemData() {
			return new DataElement(readLongCell(TestColumns.ID.name()));
		}

		@Override
		protected boolean syncRow(Integer rowIndex, DataElement data) {
			return changedRow;
		}
	};
	
	class DataElement implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Long id;
		
		public DataElement(Long id) {
			super();
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
		
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
	}
}
