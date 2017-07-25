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
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceNoMoreItensException;

public class SimpleSheetDataSourceTest {
	private static final File TEST_SHEET_FILE = new File("src/test/resources/br/com/sgpf/common/domain/dataimport/impl/SimpleSheetDataSourceTest.xls");
	private static final File TEST_SHEET_FILE_UNREADABLE = Mockito.spy(TEST_SHEET_FILE);
	private static final File TEST_SHEET_FILE_UNWRITABLE = Mockito.spy(TEST_SHEET_FILE);
	
	private static final int SHEET_INDEX = 0;
	private static final int SHEET_INDEX_NOHEADER = 1;
	private static final int SHEET_INDEX_EMPTY = 2;
	private static final int SHEET_INDEX_INVALID = 99;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static enum TestColumns { ID, STRING, CHAR, DOUBLE, FLOAT, LONG, INTEGER, SHORT, BYTE, BOOLEAN, DATE, CALENDAR, YES, NO, INVALID }
	
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
		new SimpleSheetDataSourceImpl(is, SHEET_INDEX);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullFileConstructorTest() throws DataSourceFileException {
		File file = null;
		new SimpleSheetDataSourceImpl(file, SHEET_INDEX);
	}
	
	@Test(expected = DataSourceFileException.class)
	public void invalidFileConstructorTest() throws DataSourceFileException {
		new SimpleSheetDataSourceImpl(new File("invalid.xls"), SHEET_INDEX);
	}
	
	@Test(expected = DataSourceFileException.class)
	public void unreadableFileConstructorTest() throws DataImportException {
		TEST_SHEET_FILE_UNREADABLE.setReadable(false);
		try {
			new SimpleSheetDataSourceImpl(TEST_SHEET_FILE_UNREADABLE, SHEET_INDEX);
		} catch (Exception e) {
			throw e;
		} finally {
			TEST_SHEET_FILE_UNREADABLE.setReadable(true);			
		}
	}
	
	@Test(expected = DataSourceDocumentException.class)
	public void nonExistingSheetOpenTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_INVALID);
		simpleSheetDataSource.open();
	}
	
	@Test(expected = DataImportException.class)
	public void nonExistingSheetHeaderOpenTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_NOHEADER);
		simpleSheetDataSource.open();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void openAlreadyOpenDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.open();
	}
	
	@Test
	public void openFileDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
	}
	
	@Test
	public void openInputStreamDataSourceTest() throws DataImportException, FileNotFoundException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
	}
	
	
	@Test
	public void inputStreamIsWritableTest() throws FileNotFoundException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		
		assertFalse(simpleSheetDataSource.isWritable());
	}
	
	@Test
	public void writeableFileIsWritableTest() throws DataSourceFileException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		
		assertTrue(simpleSheetDataSource.isWritable());
	}
	
	@Test
	public void nonWriteableFileIsWritableTest() throws DataSourceFileException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE_UNWRITABLE, SHEET_INDEX);
		
		assertFalse(simpleSheetDataSource.isWritable());
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.hasNext();
	}
	
	@Test
	public void emptyDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		simpleSheetDataSource.open();
		
		assertFalse(simpleSheetDataSource.hasNext());
		
		simpleSheetDataSource.close();
	}
	
	@Test
	public void notEmptyDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		assertTrue(simpleSheetDataSource.hasNext());
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.next();
	}
	
	@Test(expected = DataSourceNoMoreItensException.class)
	public void emptyDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		simpleSheetDataSource.open();
		simpleSheetDataSource.next();
	}
	
	@Test
	public void notEmptyDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		DataImportItem<Integer, DataElement> item = simpleSheetDataSource.next();
		
		assertEquals(Integer.valueOf(1), item.getId());
		assertEquals(new DataElement(0L), item.getData());
		assertFalse(item.isInsert());
		assertFalse(item.isUpdate());
		assertTrue(item.isMerge());
		assertFalse(item.isRemove());
		assertFalse(item.isForce());
		assertTrue(item.isSync());
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readInvalidColumnTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.readCharCell(TestColumns.INVALID.name());
	}
	
	@Test(expected = DataSourceFormatException.class)
	public void readInvalidCharFormatTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a terceira linha de dados
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		
		simpleSheetDataSource.readCharCell(TestColumns.CHAR.name());
	}
	
	@Test(expected = DataSourceFormatException.class)
	public void readInvalidYesNoFormatTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a terceira linha de dados
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		
		simpleSheetDataSource.readCharCell(TestColumns.YES.name());
	}
	
	@Test
	public void readValuesTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a primeira linha de dados
		simpleSheetDataSource.next();

		assertEquals(STRING_VALUE, simpleSheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertEquals(CHAR_VALUE, simpleSheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertEquals(DOUBLE_VALUE, simpleSheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertEquals(FLOAT_VALUE, simpleSheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertEquals(LONG_VALUE, simpleSheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertEquals(INTEGER_VALUE, simpleSheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertEquals(SHORT_VALUE, simpleSheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertEquals(BYTE_VALUE, simpleSheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertTrue(simpleSheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertEquals(CALENDAR_VALUE.getTime(), simpleSheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertEquals(CALENDAR_VALUE, simpleSheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertTrue(simpleSheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertFalse(simpleSheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		// Pulo para a segunda linha de dados
		simpleSheetDataSource.next();
		
		assertNull(simpleSheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(simpleSheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(simpleSheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(simpleSheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(simpleSheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(simpleSheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(simpleSheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(simpleSheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(simpleSheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(simpleSheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(simpleSheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeInvalidColumnTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.writeStringCell(2, TestColumns.INVALID.name(), STRING_VALUE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeInvalidRowTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.writeStringCell(99, TestColumns.STRING.name(), STRING_VALUE);
	}
	
	@Test
	public void writeValuesTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a segunda linha de dados
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();

		assertNull(simpleSheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(simpleSheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(simpleSheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(simpleSheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(simpleSheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(simpleSheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(simpleSheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(simpleSheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(simpleSheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(simpleSheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(simpleSheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), STRING_VALUE);
		simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), CHAR_VALUE);
		simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), DOUBLE_VALUE);
		simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), FLOAT_VALUE);
		simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), LONG_VALUE);
		simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), INTEGER_VALUE);
		simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), SHORT_VALUE);
		simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), BYTE_VALUE);
		simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), BOOLEAN_VALUE);
		simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), CALENDAR_VALUE.getTime());
		simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), CALENDAR_VALUE);
		simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), YES_VALUE);
		simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), NO_VALUE);
		
		assertEquals(STRING_VALUE, simpleSheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertEquals(CHAR_VALUE, simpleSheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertEquals(DOUBLE_VALUE, simpleSheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertEquals(FLOAT_VALUE, simpleSheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertEquals(LONG_VALUE, simpleSheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertEquals(INTEGER_VALUE, simpleSheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertEquals(SHORT_VALUE, simpleSheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertEquals(BYTE_VALUE, simpleSheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertTrue(simpleSheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertEquals(CALENDAR_VALUE.getTime(), simpleSheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertEquals(CALENDAR_VALUE, simpleSheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertTrue(simpleSheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertFalse(simpleSheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), null);
		simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), null);
		simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), null);
		simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), null);
		simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), null);
		simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), null);
		simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), null);
		simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), null);
		simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), null);
		simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), null);
		simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), null);
		simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), null);
		simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), null);

		assertNull(simpleSheetDataSource.readStringCell(TestColumns.STRING.name()));
		assertNull(simpleSheetDataSource.readCharCell(TestColumns.CHAR.name()));
		assertNull(simpleSheetDataSource.readDoubleCell(TestColumns.DOUBLE.name()));
		assertNull(simpleSheetDataSource.readFloatCell(TestColumns.FLOAT.name()));
		assertNull(simpleSheetDataSource.readLongCell(TestColumns.LONG.name()));
		assertNull(simpleSheetDataSource.readIntegerCell(TestColumns.INTEGER.name()));
		assertNull(simpleSheetDataSource.readShortCell(TestColumns.SHORT.name()));
		assertNull(simpleSheetDataSource.readByteCell(TestColumns.BYTE.name()));
		assertNull(simpleSheetDataSource.readBooleanCell(TestColumns.BOOLEAN.name()));
		assertNull(simpleSheetDataSource.readDateCell(TestColumns.DATE.name()));
		assertNull(simpleSheetDataSource.readCalendarCell(TestColumns.CALENDAR.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.YES.name()));
		assertNull(simpleSheetDataSource.readYesNoCell(TestColumns.NO.name()));
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceSyncTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		DataImportItem<Integer, DataElement> item = new DataImportItem<Integer, SimpleSheetDataSourceTest.DataElement>(1, new DataElement(1L), false, false, true, false, false, true);
		simpleSheetDataSource.sync(item);
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closeAlreadyClosedDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, 0);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
		simpleSheetDataSource.close();
	}
	
	@Test
	public void closedChangedDataSourceTest() throws DataImportException {
		long before = TEST_SHEET_FILE.lastModified();
		
		SimpleSheetDataSource<DataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Simulo uma mudança no conteúdo do arquivo
		((SimpleSheetDataSourceImpl)simpleSheetDataSource).setChangedRow(true);
		DataImportItem<Integer, DataElement> item = new DataImportItem<Integer, SimpleSheetDataSourceTest.DataElement>(1, new DataElement(1L), false, false, true, false, false, true);
		simpleSheetDataSource.sync(item);
		
		simpleSheetDataSource.close();
		
		long after = TEST_SHEET_FILE.lastModified();
		
		assertTrue(after > before);
	}

	class SimpleSheetDataSourceImpl extends SimpleSheetDataSource<DataElement> {
		private static final long serialVersionUID = 1L;
		
		private boolean changedRow = false;

		public SimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
			super(file, sheetId);
		}

		public SimpleSheetDataSourceImpl(InputStream is, int sheetId) {
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
