/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.excel.impl;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.com.sgpf.common.document.excel.WorkbookWrapper;
import br.com.sgpf.common.document.exception.DocumentException;
import br.com.sgpf.common.document.exception.DocumentFileException;
import br.com.sgpf.common.document.exception.DocumentFormatException;
import br.com.sgpf.common.document.exception.DocumentIOException;

/**
 * Classe Wrapper para documentos Excel que provê métodos facilitadores de acesso aos dados das
 * planilhas.
 * 
 * @author Sergio Puntar
 */
public class WorkbookWrapperImpl implements Serializable, WorkbookWrapper {
	private static final long serialVersionUID = 5664150354430952186L;
	
	private static final String ERROR_FILE_NOT_FOUND = "Não foi possível encontrar o arquivo [%s].";
	private static final String ERROR_NON_READABLE_FILE = "O arquivo [%s] não pode ser lido.";
	private static final String ERROR_UNDEFINED_WORKING_SHEET = "Não há nenhuma planilha de trabalho definida.";
	private static final Object ERROR_OPENING_CLOSED_ONCE_INPUT_STREAM = "Não é possível reabrir um documento baseado em Input Stream que já foi fechado.";
	private static final String ERROR_ENCRYPTED_DOCUMENT = "O documento está criptografado.";
	private static final String ERROR_INVALID_DOCUMENT_FORMAT = "O documento possui um formato inválido.";
	private static final String ERROR_READING_DOCUMENT = "Ocorreu um erro na leitura do documento.";
	private static final String ERROR_NON_EXISTING_SHEET = "O documento não possui planilha com índice [%d].";
	private static final String ERROR_CANNOT_SAVE_UNWRITABLE_DOCUMENT = "O documento não pode ser salvo pois não é gravável.";
	private static final String ERROR_RELEASING_INPUT_STREAM = "Ocorreu um erro ao liberar o input stream do documento.";
	private static final String ERROR_WRITING_CHANGES = "Não foi possível gravar as alterações no documento.";
	private static final String ERROR_CLOSING_DOCUMENT = "Ocorreu um erro ao fechar o documento.";
	private static final String ERROR_NON_EXISTING_ROW = "A planilha não possui uma linha com o índice [%d].";
	private static final String ERROR_NON_EXISTING_CELL = "A linha com o índice [%d] não possui uma célula com o índice [%d].";
	private static final String ERROR_CHAR_FORMAT = "A célula [%d] da linha [%d] não possui conteúdo no formato Character.";
	private static final String ERROR_YES_NO_FORMAT = "A célula [%d] da linha [%d] não possui conteúdo no formato Y/N.";
	private static final String ERROR_DOCUMENT_CLOSED = "O documento está fechado.";
	private static final String ERROR_DOCUMENT_OPEN = "O documento está aberto.";
	
	private static final String ARG_NAME_FILE = "file";
	private static final String ARG_NAME_IS = "is";
	private static final String ARG_NAME_WORKING_SHEET_INDEX = "workingSheetIndex";
	private static final String ARG_NAME_ROW_INDEX = "rowIndex";
	private static final String ARG_NAME_CELL_INDEX = "cellIndex";
	
	private static final String VALUE_STRING_Y = "Y";
	private static final String VALUE_STRING_N = "N";

	private enum Type { FILE, INPUT_STREAM }
	
	private File file;
	private transient InputStream is;
	private Type type;
	private Integer workingSheetIndex;
	private Boolean closedOnce = false;
	
	private transient Workbook workbook;
	private transient Sheet sheet;
	
	private WorkbookWrapperImpl(Type type, Integer workingSheetIndex) {
		super();
		this.type = type;
		this.workingSheetIndex = workingSheetIndex;
	}
	
	/**
	 * Cria um Workbook Wrapper a partir de um arquivo.
	 * 
	 * @param file Arquivo Excel
	 * @throws DocumentFileException Se o arquivo não existir ou for ilegivel
	 */
	public WorkbookWrapperImpl(File file) throws DocumentFileException {
		this(file, null);
	}
	
	/**
	 * Cria um Workbook Wrapper a partir de um arquivo, já definindo uma planilha de trabalho
	 * inicial.
	 * 
	 * @param file Arquivo Excel
	 * @param workingSheetIndex Índice da planilha de trabalho
	 * @throws DocumentFileException Se o arquivo não existir ou for ilegivel
	 */
	public WorkbookWrapperImpl(File file, Integer workingSheetIndex) throws DocumentFileException {
		this(Type.FILE, workingSheetIndex);
		this.file = checkNotNull(file, ERROR_NULL_ARGUMENT, ARG_NAME_FILE);
		
		if (!file.exists()) {
			throw new DocumentFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()));
		} else if (!file.canRead()) {
			throw new DocumentFileException(String.format(ERROR_NON_READABLE_FILE, file.getAbsolutePath()));
		}
	}
	
	/**
	 * Cria um Workbook Wrapper a partir de um input stream de um Excel.
	 * 
	 * @param is Input Stream com os dados do Excel
	 */
	public WorkbookWrapperImpl(InputStream is) {
		this(is, null);
	}
	
	/**
	 * Cria um Workbook Wrapper a partir de um input stream de um Excel, já definindo uma planilha
	 * de trabalho inicial.
	 * 
	 * @param is Input Stream com os dados do Excel
	 * @param workingSheetIndex Índice da planilha de trabalho
	 */
	public WorkbookWrapperImpl(InputStream is, Integer workingSheetIndex) {
		this(Type.INPUT_STREAM, workingSheetIndex);
		this.is = checkNotNull(is, ERROR_NULL_ARGUMENT, ARG_NAME_IS);
	}

	@Override
	public boolean isWritable() {
		return type == Type.FILE && file != null && file.canWrite();
	}

	@Override
	public void open() throws DocumentException {
		checkState(workbook == null, ERROR_DOCUMENT_OPEN);
		checkState(workingSheetIndex != null, ERROR_UNDEFINED_WORKING_SHEET);
		checkState(type != Type.INPUT_STREAM || !closedOnce, ERROR_OPENING_CLOSED_ONCE_INPUT_STREAM);
		
		if (type.equals(Type.FILE)) {
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new DocumentFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()));
			}
		}
		
		try {
			workbook = WorkbookFactory.create(is);
		} catch (EncryptedDocumentException e) {
			throw new DocumentException(ERROR_ENCRYPTED_DOCUMENT, e);
		} catch (InvalidFormatException e) {
			throw new DocumentException(ERROR_INVALID_DOCUMENT_FORMAT, e);
		} catch (IOException e) {
			throw new DocumentIOException(ERROR_READING_DOCUMENT, e);
		}
		
		updateWorkingSheet(workingSheetIndex);
	}
	
	@Override
	public void setWorkingSheetId(Integer workingSheetIndex) throws DocumentException {
		checkNotNull(workingSheetIndex, ERROR_NULL_ARGUMENT, ARG_NAME_WORKING_SHEET_INDEX);
		
		if (workbook != null) {
			updateWorkingSheet(workingSheetIndex);			
		}
		
		this.workingSheetIndex = workingSheetIndex;
	}

	/**
	 * Atualiza a planilha de trabalho de acordo com o índice passado.
	 * 
	 * @param workingSheetIndex Índice da planilha de trabalho
	 * @throws DocumentException Caso não exista uma planilha com o índice definido
	 */
	private void updateWorkingSheet(Integer workingSheetIndex) throws DocumentException {
		try {
			sheet = workbook.getSheetAt(workingSheetIndex);
		} catch (IllegalArgumentException e) {
			throw new DocumentException(String.format(ERROR_NON_EXISTING_SHEET, workingSheetIndex), e);
		}
	}
	
	@Override
	public void close(Boolean save) throws DocumentFileException, DocumentIOException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		checkArgument(!save || isWritable(), ERROR_CANNOT_SAVE_UNWRITABLE_DOCUMENT);
				
		try {
			if (save && isWritable()) {
				flush();
			}
			
			workbook.close();
		} catch (IOException e) {
			throw new DocumentIOException(ERROR_CLOSING_DOCUMENT, e);
		} finally {
			is = null;
			workbook = null;
			sheet = null;
			closedOnce = true;
		}
	}
	
	/**
	 * Salva o workbook no mesmo arquivo de onde foi lido.
	 * 
	 * @throws DocumentFileException Se o arquivo não for encontrado
	 * @throws DocumentIOException Se ocorrer um erro na escrita do arquivo
	 */
	private void flush() throws DocumentFileException, DocumentIOException {
		if (type.equals(Type.FILE) && file != null && file.canWrite()) {
			try {
				is.close();
			} catch (IOException e) {
				throw new DocumentIOException(ERROR_RELEASING_INPUT_STREAM, e);
			}
			
			try {
				workbook.write(new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				throw new DocumentFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()), e);
			} catch (IOException e) {
				throw new DocumentIOException(ERROR_WRITING_CHANGES, e);
			}
		}
	}
	
	@Override
	public Workbook getWorkbook() {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		return workbook;
	}

	@Override
	public Sheet getWorkingSheet() {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		checkState(sheet != null, ERROR_UNDEFINED_WORKING_SHEET);
		return sheet;
	}
	
	@Override
	public String readStringCell(Integer rowIndex, Integer cellIndex) {
		Cell cell = getRowCell(rowIndex, cellIndex);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getStringCellValue();
	}
	
	@Override
	public boolean writeStringCell(Integer rowIndex, Integer cellIndex, String value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		String currVal = getRowCell(rowIndex, cellIndex).getStringCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value);
		}
		
		return change;
	}
	
	@Override
	public Character readCharCell(Integer rowIndex, Integer cellIndex) throws DocumentFormatException {
		String stringValue = readStringCell(rowIndex, cellIndex);
		
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		} else if (stringValue.length() > 1) {
			throw new DocumentFormatException(String.format(ERROR_CHAR_FORMAT, cellIndex, rowIndex));
		}
		
		return stringValue.charAt(0);
	}
	
	@Override
	public boolean writeCharCell(Integer rowIndex, Integer cellIndex, Character value) {
		return writeStringCell(rowIndex, cellIndex, value == null ? null : String.valueOf(value));
	}
	
	@Override
	public Double readDoubleCell(Integer rowIndex, Integer cellIndex) {
		Cell cell = getRowCell(rowIndex, cellIndex);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getNumericCellValue();
	}
	
	@Override
	public boolean writeDoubleCell(Integer rowIndex, Integer cellIndex, Double value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		Double currVal = getRowCell(rowIndex, cellIndex).getNumericCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value);
		}
		
		return change;
	}
	
	@Override
	public Float readFloatCell(Integer rowIndex, Integer cellIndex) {
		Double doubleValue = readDoubleCell(rowIndex, cellIndex);
		return doubleValue == null ? null : doubleValue.floatValue();
	}
	
	@Override
	public boolean writeFloatCell(Integer rowIndex, Integer cellIndex, Float value) {
		return writeDoubleCell(rowIndex, cellIndex, value == null ? null : Double.valueOf(value));
	}
	
	@Override
	public Long readLongCell(Integer rowIndex, Integer cellIndex) {
		Double doubleValue = readDoubleCell(rowIndex, cellIndex);
		return doubleValue == null ? null : doubleValue.longValue();
	}
	
	@Override
	public boolean writeLongCell(Integer rowIndex, Integer cellIndex, Long value) {
		return writeDoubleCell(rowIndex, cellIndex, value == null ? null : Double.valueOf(value));
	}
	
	@Override
	public Integer readIntegerCell(Integer rowIndex, Integer cellIndex) {
		Double doubleValue = readDoubleCell(rowIndex, cellIndex);
		return doubleValue == null ? null : doubleValue.intValue();
	}
	
	@Override
	public boolean writeIntegerCell(Integer rowIndex, Integer cellIndex, Integer value) {
		return writeDoubleCell(rowIndex, cellIndex, value == null ? null : Double.valueOf(value));
	}
	
	@Override
	public Short readShortCell(Integer rowIndex, Integer cellIndex) {
		Double doubleValue = readDoubleCell(rowIndex, cellIndex);
		return doubleValue == null ? null : doubleValue.shortValue();
	}

	@Override
	public boolean writeShortCell(Integer rowIndex, Integer cellIndex, Short value) {
		return writeDoubleCell(rowIndex, cellIndex, value == null ? null : Double.valueOf(value));
	}
	
	@Override
	public Byte readByteCell(Integer rowIndex, Integer cellIndex) {
		Double doubleValue = readDoubleCell(rowIndex, cellIndex);
		return doubleValue == null ? null : doubleValue.byteValue();
	}
	
	@Override
	public boolean writeByteCell(Integer rowIndex, Integer cellIndex, Byte value) {
		return writeDoubleCell(rowIndex, cellIndex, value == null ? null : Double.valueOf(value));
	}
	
	@Override
	public Boolean readBooleanCell(Integer rowIndex, Integer cellIndex) {
		Cell cell = getRowCell(rowIndex, cellIndex);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getBooleanCellValue();
	}
	
	@Override
	public boolean writeBooleanCell(Integer rowIndex, Integer cellIndex,Boolean value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		Boolean currVal = getRowCell(rowIndex, cellIndex).getBooleanCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value);
		}
		
		return change;
	}
	
	@Override
	public Date readDateCell(Integer rowIndex, Integer cellIndex) {
		Cell cell = getRowCell(rowIndex, cellIndex);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getDateCellValue();
	}
	
	@Override
	public boolean writeDateCell(Integer rowIndex, Integer cellIndex, Date value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		Date currVal = getRowCell(rowIndex, cellIndex).getDateCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value);
		}
		
		return change;
	}
	
	@Override
	public Calendar readCalendarCell(Integer rowIndex, Integer cellIndex) {
		Date date = readDateCell(rowIndex, cellIndex);
		
		if (date == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return calendar;
	}
	
	@Override
	public boolean writeCalendarCell(Integer rowIndex, Integer cellIndex, Calendar value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		Date dateVal = getRowCell(rowIndex, cellIndex).getDateCellValue();
		Calendar currVal = null;
		
		if (dateVal != null) {
			currVal = Calendar.getInstance();
			currVal.setTime(dateVal);
		}
		
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value);
		}
		
		return change;
	}

	@Override
	public Boolean readYesNoCell(Integer rowIndex, Integer cellIndex) throws DocumentFormatException {
		String value = getRowCell(rowIndex, cellIndex).getStringCellValue();
		
		if (VALUE_STRING_Y.equalsIgnoreCase(value)) {
			return true;
		} else if (VALUE_STRING_N.equalsIgnoreCase(value)) {
			return false;
		} else if (value != null && !value.isEmpty()) {
			throw new DocumentFormatException(String.format(ERROR_YES_NO_FORMAT, cellIndex, rowIndex));
		}
		
		return null;
	}
	
	@Override
	public boolean writeYesNoCell(Integer rowIndex, Integer cellIndex, Boolean value) {
		if (value == null) {
			return writeNullValue(rowIndex, cellIndex);
		}
		
		String currVal = getRowCell(rowIndex, cellIndex).getStringCellValue();
		boolean change = (!VALUE_STRING_Y.equals(currVal) && !VALUE_STRING_N.equals(currVal))
				|| (VALUE_STRING_Y.equals(currVal) && !value)
				|| (VALUE_STRING_N.equals(currVal) && value);
		
		if (change) {
			getRowCell(rowIndex, cellIndex).setCellValue(value ? VALUE_STRING_Y : VALUE_STRING_N);
		}
		
		return change;
	}
	
	@Override
	public boolean writeNullValue(Integer rowIndex, Integer cellIndex) {
		Cell cell = getRowCell(rowIndex, cellIndex);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())) {
			return false;
		}
		
		String value = null;
		cell.setCellValue(value);
		cell.setCellType(CellType.BLANK);
		
		return true;
	}
	
	@Override
	public Cell getRowCell(Integer rowIndex, Integer cellIndex) {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		checkState(sheet != null, ERROR_UNDEFINED_WORKING_SHEET);
		checkNotNull(rowIndex, ERROR_NULL_ARGUMENT, ARG_NAME_ROW_INDEX);
		checkNotNull(cellIndex, ERROR_NULL_ARGUMENT, ARG_NAME_CELL_INDEX);
		
		Row row = sheet.getRow(rowIndex);
		checkArgument(row != null, ERROR_NON_EXISTING_ROW, rowIndex);
		
		Cell cell = row.getCell(cellIndex);
		checkArgument(cell != null, ERROR_NON_EXISTING_CELL, rowIndex, cellIndex);
		
		return cell;
	}
}
