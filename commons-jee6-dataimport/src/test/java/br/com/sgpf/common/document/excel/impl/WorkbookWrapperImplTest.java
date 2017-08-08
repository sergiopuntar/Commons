/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.excel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.sgpf.common.document.excel.impl.WorkbookWrapperImpl;
import br.com.sgpf.common.document.exception.DocumentException;
import br.com.sgpf.common.document.exception.DocumentFileException;
import br.com.sgpf.common.document.exception.DocumentFormatException;
import br.com.sgpf.common.document.exception.DocumentIOException;

public class WorkbookWrapperImplTest {
	
	private static final File WORKBOOK_FILE = new File("src/test/resources/br/com/sgpf/common/document/excel/WorkbookWrapperTest.xls");
	private static final File UNREADABLE_WORKBOOK_FILE = Mockito.spy(WORKBOOK_FILE);
	private static final File UNWRITABLE_WORKBOOK_FILE = Mockito.spy(WORKBOOK_FILE);
	private static final File ENCRYPTED_WORKBOOK_FILE = new File("src/test/resources/br/com/sgpf/common/document/excel/EncryptedWorkbookWrapperTest.xls");
	private static final File TEXT_FILE = new File("src/test/resources/br/com/sgpf/common/document/excel/TextFile.txt");
	private static final File INVALID_FILE = new File("src/test/resources/br/com/sgpf/common/document/excel/invalid.xls");
	
	private static final int SHEET_FIRST_INDEX = 0;
	private static final int SHEET_SECOND_INDEX = 1;
	private static final int SHEET_INVALID_INDEX = 99;
	
	private static final String SHEET_FIRST_NAME = "PRIMEIRA";
	private static final String SHEET_SECOND_NAME = "SEGUNDA";
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
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
		Mockito.when(UNREADABLE_WORKBOOK_FILE.canRead()).thenReturn(false);
		Mockito.when(UNWRITABLE_WORKBOOK_FILE.canWrite()).thenReturn(false);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullInputStreamConstructorTest() {
		InputStream is = null;
		new WorkbookWrapperImpl(is, SHEET_FIRST_INDEX);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullFileConstructorTest() throws DocumentFileException {
		File file = null;
		new WorkbookWrapperImpl(file, SHEET_FIRST_INDEX);
	}
	
	@Test(expected = DocumentFileException.class)
	public void invalidFileConstructorTest() throws DocumentFileException {
		new WorkbookWrapperImpl(INVALID_FILE, SHEET_FIRST_INDEX);
	}
	
	@Test(expected = DocumentFileException.class)
	public void unreadableFileConstructorTest() throws DocumentFileException {
		new WorkbookWrapperImpl(UNREADABLE_WORKBOOK_FILE, SHEET_FIRST_INDEX);
	}
	
	@Test
	public void isWriteableTest() throws DocumentFileException, FileNotFoundException {
		WorkbookWrapperImpl writeableFileWorkbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		WorkbookWrapperImpl unwriteableFileWorkbook = new WorkbookWrapperImpl(UNWRITABLE_WORKBOOK_FILE, SHEET_FIRST_INDEX);
		WorkbookWrapperImpl inputStreamWorkbook = new WorkbookWrapperImpl(new FileInputStream(WORKBOOK_FILE), SHEET_FIRST_INDEX);
		
		assertTrue(writeableFileWorkbook.isWritable());
		assertFalse(unwriteableFileWorkbook.isWritable());
		assertFalse(inputStreamWorkbook.isWritable());
	}
	
	@Test(expected = IllegalStateException.class)
	public void openAlreadyOpenWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.open();
	}
	
	@Test(expected = IllegalStateException.class)
	public void openWorkbookWithUndefinedSheetIndexTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE);
		workbook.open();
	}
	
	@Test(expected = IllegalStateException.class)
	public void openClosedOnceInputStreamWorkbookTest() throws DocumentException, FileNotFoundException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(new FileInputStream(WORKBOOK_FILE), SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(false);
		workbook.open();
	}
	
	@Test
	public void openClosedOnceFileWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(false);
		workbook.open();
		workbook.close(false);
	}
	
	@Test(expected = DocumentFileException.class)
	public void openWorkbookFromDeletedFileTest() throws DocumentException, IOException {
		INVALID_FILE.createNewFile();
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(INVALID_FILE, SHEET_FIRST_INDEX);
		INVALID_FILE.delete();
		workbook.open();
	}
	
	@Test(expected = DocumentException.class)
	public void openWorkbookFromEcryptedFileTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(ENCRYPTED_WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
	}
	
	@Test(expected = DocumentException.class)
	public void openWorkbookFromTextFileTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(TEXT_FILE, SHEET_FIRST_INDEX);
		workbook.open();
	}
	
	@Test(expected = DocumentIOException.class)
	public void openWorkbookFromInvalidInputStreamTest() throws DocumentException, IOException {
		FileInputStream fis = Mockito.spy(new FileInputStream(WORKBOOK_FILE));
		doThrow(new IOException()).when(fis).read();
		doThrow(new IOException()).when(fis).read(any(byte[].class));
		doThrow(new IOException()).when(fis).read(any(byte[].class), anyInt(), anyInt());
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(fis, SHEET_FIRST_INDEX);
		workbook.open();
	}
	
	@Test(expected = DocumentException.class)
	public void openWorkbookFromWithInvalidSheetIndexTest() throws DocumentException, IOException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_INVALID_INDEX);
		workbook.open();
	}
	
	@Test
	public void openWorkbookTest() throws DocumentException, IOException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		assertEquals(SHEET_FIRST_NAME, workbook.getWorkingSheet().getSheetName());
		workbook.close(false);
	}
	
	@Test(expected = NullPointerException.class)
	public void setNullWorkingSheetIdTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		Integer workingSheetIndex = null;
		workbook.setWorkingSheetId(workingSheetIndex);
	}
	
	@Test
	public void setWorkingSheetIdClosedWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.setWorkingSheetId(SHEET_SECOND_INDEX);
		workbook.open();
		assertEquals(SHEET_SECOND_NAME, workbook.getWorkingSheet().getSheetName());
		workbook.close(false);
	}
	
	@Test(expected = DocumentException.class)
	public void setInvalidWorkingSheetIdOpenWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		try {
			workbook.setWorkingSheetId(SHEET_INVALID_INDEX);
		} finally {
			workbook.close(false);
		}
	}
	
	@Test
	public void setValidWorkingSheetIdOpenWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.setWorkingSheetId(SHEET_SECOND_INDEX);
		assertEquals(SHEET_SECOND_NAME, workbook.getWorkingSheet().getSheetName());
		workbook.close(false);
	}
	
	@Test(expected = IllegalStateException.class)
	public void closeUnopenedWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.close(false);
	}
	
	@Test(expected = IllegalStateException.class)
	public void closeAlreadyClosedWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(false);
		workbook.close(false);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void closeSavingInputStreamWorkbookTest() throws DocumentException, FileNotFoundException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(new FileInputStream(WORKBOOK_FILE), SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(true);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void closeSavingUnwriteableFileWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(UNWRITABLE_WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(true);
	}
	
	@Test
	public void closeSaveWorkbookTest() throws DocumentException, IOException {
		long before = WORKBOOK_FILE.lastModified();
		
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		workbook.close(true);
		
		long after = WORKBOOK_FILE.lastModified();
		
		assertTrue(after > before);
	}
	
	@Test(expected = IllegalStateException.class)
	public void getWorkbookFromClosedWrapperTest() throws DocumentFileException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.getWorkbook();
	}
	
	@Test
	public void getWorkbookTest() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		assertNotNull(workbook.getWorkbook());
		workbook.close(false);
	}
	
	@Test(expected = IllegalStateException.class)
	public void getWorkingSheetFromClosedWrapperTest() throws DocumentFileException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.getWorkingSheet();
	}
	
	@Test
	public void getWorkingSheet() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		assertNotNull(workbook.getWorkingSheet());
		workbook.close(false);
	}
	
	@Test(expected = IllegalStateException.class)
	public void getRowCellFromClosedWorkbook() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.getRowCell(0, 0);
	}
	
	@Test(expected = NullPointerException.class)
	public void getRowCellNullRow() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		try {
			Integer row = null;
			Integer cell = 0;
			workbook.getRowCell(row, cell);
		} finally {
			workbook.close(false);
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void getRowCellNullCell() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		try {
			Integer row = 0;
			Integer cell = null;
			workbook.getRowCell(row, cell);
		} finally {
			workbook.close(false);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getRowCellNonExistingRow() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		try {
			workbook.open();workbook.getRowCell(99, 0);
		} finally {
			workbook.close(false);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getRowCellNonExistingCell() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		try {
			workbook.getRowCell(0, 99);
		} finally {
			workbook.close(false);
		}
	}
	
	@Test
	public void getRowCell() throws DocumentException {
		WorkbookWrapperImpl workbook = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		workbook.open();
		assertNotNull(workbook.getRowCell(0, 0));
		workbook.close(false);
	}
	
	@Test
	public void readValuesTest() throws DocumentException {
		WorkbookWrapperImpl simpleSheetDataSource = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		simpleSheetDataSource.open();
		
		// Pulo para a primeira linha de dados que possui conteúdo válido
		assertEquals(STRING_VALUE, simpleSheetDataSource.readStringCell(1, 0));
		assertEquals(CHAR_VALUE, simpleSheetDataSource.readCharCell(1, 1));
		assertEquals(DOUBLE_VALUE, simpleSheetDataSource.readDoubleCell(1, 2));
		assertEquals(FLOAT_VALUE, simpleSheetDataSource.readFloatCell(1, 3));
		assertEquals(LONG_VALUE, simpleSheetDataSource.readLongCell(1, 4));
		assertEquals(INTEGER_VALUE, simpleSheetDataSource.readIntegerCell(1, 5));
		assertEquals(SHORT_VALUE, simpleSheetDataSource.readShortCell(1, 6));
		assertEquals(BYTE_VALUE, simpleSheetDataSource.readByteCell(1, 7));
		assertTrue(simpleSheetDataSource.readBooleanCell(1, 8));
		assertEquals(CALENDAR_VALUE.getTime(), simpleSheetDataSource.readDateCell(1, 9));
		assertEquals(CALENDAR_VALUE, simpleSheetDataSource.readCalendarCell(1, 10));
		assertTrue(simpleSheetDataSource.readYesNoCell(1, 11));
		assertFalse(simpleSheetDataSource.readYesNoCell(1, 12));
		
		// Pulo para a segunda linha de dados que é toda nula
		assertNull(simpleSheetDataSource.readStringCell(2, 0));
		assertNull(simpleSheetDataSource.readCharCell(2, 1));
		assertNull(simpleSheetDataSource.readDoubleCell(2, 2));
		assertNull(simpleSheetDataSource.readFloatCell(2, 3));
		assertNull(simpleSheetDataSource.readLongCell(2, 4));
		assertNull(simpleSheetDataSource.readIntegerCell(2, 5));
		assertNull(simpleSheetDataSource.readShortCell(2, 6));
		assertNull(simpleSheetDataSource.readByteCell(2, 7));
		assertNull(simpleSheetDataSource.readBooleanCell(2, 8));
		assertNull(simpleSheetDataSource.readDateCell(2, 9));
		assertNull(simpleSheetDataSource.readCalendarCell(2, 10));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 11));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 12));
		
		simpleSheetDataSource.close(false);
	}
	
	@Test
	public void writeValuesTest() throws DocumentException {
		WorkbookWrapperImpl simpleSheetDataSource = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		simpleSheetDataSource.open();
		
		// Valido que é tudo nulo
		assertNull(simpleSheetDataSource.readStringCell(2, 0));
		assertNull(simpleSheetDataSource.readCharCell(2, 1));
		assertNull(simpleSheetDataSource.readDoubleCell(2, 2));
		assertNull(simpleSheetDataSource.readFloatCell(2, 3));
		assertNull(simpleSheetDataSource.readLongCell(2, 4));
		assertNull(simpleSheetDataSource.readIntegerCell(2, 5));
		assertNull(simpleSheetDataSource.readShortCell(2, 6));
		assertNull(simpleSheetDataSource.readByteCell(2, 7));
		assertNull(simpleSheetDataSource.readBooleanCell(2, 8));
		assertNull(simpleSheetDataSource.readDateCell(2, 9));
		assertNull(simpleSheetDataSource.readCalendarCell(2, 10));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 11));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 12));
		
		// Defino novos valores, validando que houve mudança
		assertTrue(simpleSheetDataSource.writeStringCell(2, 0, STRING_VALUE));
		assertTrue(simpleSheetDataSource.writeCharCell(2, 1, CHAR_VALUE));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, 2, DOUBLE_VALUE));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, 3, FLOAT_VALUE));
		assertTrue(simpleSheetDataSource.writeLongCell(2, 4, LONG_VALUE));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, 5, INTEGER_VALUE));
		assertTrue(simpleSheetDataSource.writeShortCell(2, 6, SHORT_VALUE));
		assertTrue(simpleSheetDataSource.writeByteCell(2, 7, BYTE_VALUE));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, 8, BOOLEAN_VALUE));
		assertTrue(simpleSheetDataSource.writeDateCell(2, 9, CALENDAR_VALUE.getTime()));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, 10, CALENDAR_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, 11, YES_VALUE));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, 12, NO_VALUE));
		
		// Valido que valores foram alterados
		assertEquals(STRING_VALUE, simpleSheetDataSource.readStringCell(2, 0));
		assertEquals(CHAR_VALUE, simpleSheetDataSource.readCharCell(2, 1));
		assertEquals(DOUBLE_VALUE, simpleSheetDataSource.readDoubleCell(2, 2));
		assertEquals(FLOAT_VALUE, simpleSheetDataSource.readFloatCell(2, 3));
		assertEquals(LONG_VALUE, simpleSheetDataSource.readLongCell(2, 4));
		assertEquals(INTEGER_VALUE, simpleSheetDataSource.readIntegerCell(2, 5));
		assertEquals(SHORT_VALUE, simpleSheetDataSource.readShortCell(2, 6));
		assertEquals(BYTE_VALUE, simpleSheetDataSource.readByteCell(2, 7));
		assertTrue(simpleSheetDataSource.readBooleanCell(2, 8));
		assertEquals(CALENDAR_VALUE.getTime(), simpleSheetDataSource.readDateCell(2, 9));
		assertEquals(CALENDAR_VALUE, simpleSheetDataSource.readCalendarCell(2, 10));
		assertTrue(simpleSheetDataSource.readYesNoCell(2, 11));
		assertFalse(simpleSheetDataSource.readYesNoCell(2, 12));
		
		// Defino mesmos valores, validando que dessa vez não houve mudança
		assertFalse(simpleSheetDataSource.writeStringCell(2, 0, STRING_VALUE));
		assertFalse(simpleSheetDataSource.writeCharCell(2, 1, CHAR_VALUE));
		assertFalse(simpleSheetDataSource.writeDoubleCell(2, 2, DOUBLE_VALUE));
		assertFalse(simpleSheetDataSource.writeFloatCell(2, 3, FLOAT_VALUE));
		assertFalse(simpleSheetDataSource.writeLongCell(2, 4, LONG_VALUE));
		assertFalse(simpleSheetDataSource.writeIntegerCell(2, 5, INTEGER_VALUE));
		assertFalse(simpleSheetDataSource.writeShortCell(2, 6, SHORT_VALUE));
		assertFalse(simpleSheetDataSource.writeByteCell(2, 7, BYTE_VALUE));
		assertFalse(simpleSheetDataSource.writeBooleanCell(2, 8, BOOLEAN_VALUE));
		assertFalse(simpleSheetDataSource.writeDateCell(2, 9, CALENDAR_VALUE.getTime()));
		assertFalse(simpleSheetDataSource.writeCalendarCell(2, 10, CALENDAR_VALUE));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, 11, YES_VALUE));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, 12, NO_VALUE));
		
		// Reverto valores para null, validando que houve mudança
		assertTrue(simpleSheetDataSource.writeStringCell(2, 0, null));
		assertTrue(simpleSheetDataSource.writeCharCell(2, 1, null));
		assertTrue(simpleSheetDataSource.writeDoubleCell(2, 2, null));
		assertTrue(simpleSheetDataSource.writeFloatCell(2, 3, null));
		assertTrue(simpleSheetDataSource.writeLongCell(2, 4, null));
		assertTrue(simpleSheetDataSource.writeIntegerCell(2, 5, null));
		assertTrue(simpleSheetDataSource.writeShortCell(2, 6, null));
		assertTrue(simpleSheetDataSource.writeByteCell(2, 7, null));
		assertTrue(simpleSheetDataSource.writeBooleanCell(2, 8, null));
		assertTrue(simpleSheetDataSource.writeDateCell(2, 9, null));
		assertTrue(simpleSheetDataSource.writeCalendarCell(2, 10, null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, 11, null));
		assertTrue(simpleSheetDataSource.writeYesNoCell(2, 12, null));

		// Valido que valores foram revertidos para null
		assertNull(simpleSheetDataSource.readStringCell(2, 0));
		assertNull(simpleSheetDataSource.readCharCell(2, 1));
		assertNull(simpleSheetDataSource.readDoubleCell(2, 2));
		assertNull(simpleSheetDataSource.readFloatCell(2, 3));
		assertNull(simpleSheetDataSource.readLongCell(2, 4));
		assertNull(simpleSheetDataSource.readIntegerCell(2, 5));
		assertNull(simpleSheetDataSource.readShortCell(2, 6));
		assertNull(simpleSheetDataSource.readByteCell(2, 7));
		assertNull(simpleSheetDataSource.readBooleanCell(2, 8));
		assertNull(simpleSheetDataSource.readDateCell(2, 9));
		assertNull(simpleSheetDataSource.readCalendarCell(2, 10));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 11));
		assertNull(simpleSheetDataSource.readYesNoCell(2, 12));
		
		// Reverto novamente valores para null, validando que dessa vez não houve mudança
		assertFalse(simpleSheetDataSource.writeStringCell(2, 0, null));
		assertFalse(simpleSheetDataSource.writeCharCell(2, 1, null));
		assertFalse(simpleSheetDataSource.writeDoubleCell(2, 2, null));
		assertFalse(simpleSheetDataSource.writeFloatCell(2, 3, null));
		assertFalse(simpleSheetDataSource.writeLongCell(2, 4, null));
		assertFalse(simpleSheetDataSource.writeIntegerCell(2, 5, null));
		assertFalse(simpleSheetDataSource.writeShortCell(2, 6, null));
		assertFalse(simpleSheetDataSource.writeByteCell(2, 7, null));
		assertFalse(simpleSheetDataSource.writeBooleanCell(2, 8, null));
		assertFalse(simpleSheetDataSource.writeDateCell(2, 9, null));
		assertFalse(simpleSheetDataSource.writeCalendarCell(2, 10, null));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, 11, null));
		assertFalse(simpleSheetDataSource.writeYesNoCell(2, 12, null));
		
		simpleSheetDataSource.close(false);
	}
	
	@Test(expected = DocumentFormatException.class)
	public void readInvalidCharFormatTest() throws DocumentException {
		WorkbookWrapperImpl simpleSheetDataSource = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		simpleSheetDataSource.open();
		assertNull(simpleSheetDataSource.readCharCell(3, 1));
		simpleSheetDataSource.close(false);
	}
	
	@Test(expected = DocumentFormatException.class)
	public void readInvalidYesNoFormatTest() throws DocumentException {
		WorkbookWrapperImpl simpleSheetDataSource = new WorkbookWrapperImpl(WORKBOOK_FILE, SHEET_FIRST_INDEX);
		simpleSheetDataSource.open();
		assertNull(simpleSheetDataSource.readYesNoCell(3, 11));
		simpleSheetDataSource.close(false);
	}
}
