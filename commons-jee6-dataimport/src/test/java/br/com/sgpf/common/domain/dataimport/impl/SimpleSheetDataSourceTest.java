package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceNoMoreItensException;
import br.com.sgpf.common.domain.vo.SimpleDataElement;

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
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_INVALID);
		simpleSheetDataSource.open();
	}
	
	@Test(expected = DataImportException.class)
	public void nonExistingSheetHeaderOpenTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_NOHEADER);
		simpleSheetDataSource.open();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void openAlreadyOpenDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.open();
	}
	
	@Test
	public void toStringFileDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		assertEquals("File Based SimpleSheetDataSourceImpl: \"" + TEST_SHEET_FILE.getAbsolutePath() + "\"", simpleSheetDataSource.toString());
	}
	
	@Test
	public void toStringInputStreamDataSourceTest() throws DataImportException, FileNotFoundException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		assertEquals("In Memory Based SimpleSheetDataSourceImpl", simpleSheetDataSource.toString());
	}
	
	@Test
	public void openFileDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
	}
	
	@Test
	public void openInputStreamDataSourceTest() throws DataImportException, FileNotFoundException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
	}
	
	@Test
	public void inputStreamIsWritableTest() throws FileNotFoundException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(new FileInputStream(TEST_SHEET_FILE), SHEET_INDEX);
		
		assertFalse(simpleSheetDataSource.isWritable());
	}
	
	@Test
	public void writeableFileIsWritableTest() throws DataSourceFileException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		
		assertTrue(simpleSheetDataSource.isWritable());
	}
	
	@Test
	public void nonWriteableFileIsWritableTest() throws DataSourceFileException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE_UNWRITABLE, SHEET_INDEX);
		
		assertFalse(simpleSheetDataSource.isWritable());
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.hasNext();
	}
	
	@Test
	public void emptyDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		simpleSheetDataSource.open();
		
		assertFalse(simpleSheetDataSource.hasNext());
		
		simpleSheetDataSource.close();
	}
	
	@Test
	public void notEmptyDataSourceHasNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		assertTrue(simpleSheetDataSource.hasNext());
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.next();
	}
	
	@Test(expected = DataSourceNoMoreItensException.class)
	public void emptyDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX_EMPTY);
		simpleSheetDataSource.open();
		simpleSheetDataSource.next();
	}
	
	@Test
	public void notEmptyDataSourceNextTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		DataImportItem<Integer, SimpleDataElement> item = simpleSheetDataSource.next();
		
		assertEquals(Integer.valueOf(1), item.getId());
		assertEquals(new SimpleDataElement(0L), item.getData());
		assertFalse(item.isInsert());
		assertFalse(item.isUpdate());
		assertTrue(item.isMerge());
		assertFalse(item.isRemove());
		assertFalse(item.isForce());
		assertTrue(item.isSync());
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceCurrentTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.current();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void nextNeverCalledDataSourceCurrentTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.current();
	}
	
	@Test
	public void notEmptyDataSourceCurrentTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		simpleSheetDataSource.next();
		DataImportItem<Integer, SimpleDataElement> item = simpleSheetDataSource.current();
		
		assertEquals(Integer.valueOf(1), item.getId());
		assertEquals(new SimpleDataElement(0L), item.getData());
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
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.readCharCell(TestColumns.INVALID.name());
	}
	
	@Test(expected = DataSourceFormatException.class)
	public void readInvalidCharFormatTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a terceira linha de dados que possui conteúdo inválido
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		
		simpleSheetDataSource.readCharCell(TestColumns.CHAR.name());
	}
	
	@Test(expected = DataSourceFormatException.class)
	public void readInvalidYesNoFormatTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a terceira linha de dados que possui conteúdo inválido
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		
		simpleSheetDataSource.readCharCell(TestColumns.YES.name());
	}
	
	@Test
	public void readValuesTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a primeira linha de dados que possui conteúdo válido
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
		
		// Pulo para a segunda linha de dados que é toda nula
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
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.writeStringCell(2, TestColumns.INVALID.name(), STRING_VALUE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeInvalidRowTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		simpleSheetDataSource.writeStringCell(99, TestColumns.STRING.name(), STRING_VALUE);
	}
	
	@Test
	public void writeValuesTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a segunda linha de dados que é toda nula
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();

		// Valido que é tudo nulo
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
		
		// Defino novos valores, validando que houve mudança
		assertTrue(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), STRING_VALUE));
		assertTrue(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), CHAR_VALUE));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), DOUBLE_VALUE));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), FLOAT_VALUE));
		assertTrue(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), LONG_VALUE));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), INTEGER_VALUE));
		assertTrue(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), SHORT_VALUE));
		assertTrue(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), BYTE_VALUE));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), BOOLEAN_VALUE));
		assertTrue(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), CALENDAR_VALUE.getTime()));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), CALENDAR_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), YES_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), NO_VALUE));
		
		// Valido que valores foram alterados
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
		
		// Defino mesmos valores, validando que dessa vez não houve mudança
		assertFalse(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), STRING_VALUE));
		assertFalse(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), CHAR_VALUE));
		assertFalse(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), DOUBLE_VALUE));
		assertFalse(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), FLOAT_VALUE));
		assertFalse(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), LONG_VALUE));
		assertFalse(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), INTEGER_VALUE));
		assertFalse(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), SHORT_VALUE));
		assertFalse(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), BYTE_VALUE));
		assertFalse(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), BOOLEAN_VALUE));
		assertFalse(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), CALENDAR_VALUE.getTime()));
		assertFalse(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), CALENDAR_VALUE));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), YES_VALUE));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), NO_VALUE));
		
		// Reverto valores para null, validando que houve mudança
		assertTrue(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), null));
		assertTrue(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), null));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), null));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), null));
		assertTrue(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), null));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), null));
		assertTrue(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), null));
		assertTrue(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), null));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), null));
		assertTrue(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), null));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), null));

		// Valido que valores foram revertidos para null
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
		
		// Reverto novamente valores para null, validando que dessa vez não houve mudança
		assertFalse(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), null));
		assertFalse(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), null));
		assertFalse(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), null));
		assertFalse(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), null));
		assertFalse(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), null));
		assertFalse(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), null));
		assertFalse(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), null));
		assertFalse(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), null));
		assertFalse(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), null));
		assertFalse(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), null));
		assertFalse(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), null));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), null));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), null));
		
		simpleSheetDataSource.close();
	}
	
	@Test
	public void writeValuesOnDifferentLineTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Defino novos valores, em uma linha que não li ainda (linha originalmente toda nula)
		assertTrue(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), STRING_VALUE));
		assertTrue(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), CHAR_VALUE));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), DOUBLE_VALUE));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), FLOAT_VALUE));
		assertTrue(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), LONG_VALUE));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), INTEGER_VALUE));
		assertTrue(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), SHORT_VALUE));
		assertTrue(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), BYTE_VALUE));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), BOOLEAN_VALUE));
		assertTrue(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), CALENDAR_VALUE.getTime()));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), CALENDAR_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), YES_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), NO_VALUE));
		
		// Pulo para a linha alterada
		simpleSheetDataSource.next();
		simpleSheetDataSource.next();
		
		// Valido que valores foram alterados
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
		
		// Reverto valores para null novamente
		assertTrue(simpleSheetDataSource.writeStringCell(2, TestColumns.STRING.name(), null));
		assertTrue(simpleSheetDataSource.writeCharCell(2, TestColumns.CHAR.name(), null));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, TestColumns.DOUBLE.name(), null));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, TestColumns.FLOAT.name(), null));
		assertTrue(simpleSheetDataSource.writeLongCell(2, TestColumns.LONG.name(), null));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, TestColumns.INTEGER.name(), null));
		assertTrue(simpleSheetDataSource.writeShortCell(2, TestColumns.SHORT.name(), null));
		assertTrue(simpleSheetDataSource.writeByteCell(2, TestColumns.BYTE.name(), null));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, TestColumns.BOOLEAN.name(), null));
		assertTrue(simpleSheetDataSource.writeDateCell(2, TestColumns.DATE.name(), null));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, TestColumns.CALENDAR.name(), null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.YES.name(), null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, TestColumns.NO.name(), null));
		
		simpleSheetDataSource.close();
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closedDataSourceSyncTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		DataImportInstructions instructions = new DataImportInstructions(false, false, true, false, false, true);
		DataImportItem<Integer, SimpleDataElement> item = new DataImportItem<Integer, SimpleDataElement>(1, new SimpleDataElement(1L), instructions);
		simpleSheetDataSource.sync(item);
	}
	
	@Test(expected = DataSourceInvalidStateException.class)
	public void closeAlreadyClosedDataSourceTest() throws DataImportException {
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, 0);
		simpleSheetDataSource.open();
		simpleSheetDataSource.close();
		simpleSheetDataSource.close();
	}
	
	@Test
	public void closedChangedDataSourceTest() throws DataImportException {
		long before = TEST_SHEET_FILE.lastModified();
		
		SimpleSheetDataSource<SimpleDataElement> simpleSheetDataSource = new SimpleSheetDataSourceImpl(TEST_SHEET_FILE, SHEET_INDEX);
		simpleSheetDataSource.open();
		
		// Simulo uma mudança no conteúdo do arquivo
		((SimpleSheetDataSourceImpl)simpleSheetDataSource).setChangedRow(true);
		DataImportInstructions instructions = new DataImportInstructions(false, false, true, false, false, true);
		DataImportItem<Integer, SimpleDataElement> item = new DataImportItem<Integer, SimpleDataElement>(1, new SimpleDataElement(1L), instructions);
		assertTrue(simpleSheetDataSource.sync(item));
		
		simpleSheetDataSource.close();
		
		long after = TEST_SHEET_FILE.lastModified();
		
		assertTrue(after > before);
	}
}
