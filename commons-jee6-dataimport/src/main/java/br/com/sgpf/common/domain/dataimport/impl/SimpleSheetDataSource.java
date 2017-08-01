/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceIOException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceNoMoreItensException;

/**
 * Implementação fonte de dados em planilha Excel simples.
 *
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public abstract class SimpleSheetDataSource<T extends Serializable> implements ImportDataSource<Integer, T> {
	private static final long serialVersionUID = -7387063988593887736L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSheetDataSource.class);
	
	private static final String ERROR_FILE_NOT_FOUND = "Não foi possível encontrar o arquivo [%s].";
	private static final String ERROR_READING_DOCUMENT = "Ocorreu um erro na leitura do documento.";
	private static final String ERROR_ENCRYPTED_DOCUMENT = "O documento está criptografado.";
	private static final String ERROR_INVALID_DOCUMENT_FORMAT = "O documento possui um formato inválido.";
	private static final String ERROR_NON_READABLE_FILE = "O arquivo [%s] não pode ser lido.";
	private static final String ERROR_NON_EXISTING_SHEET = "O documento não possui planilha com índice [%d].";
	private static final String ERROR_NO_HEADER = "A planilha não possui um cabeçalho.";
	private static final String ERROR_NO_MORE_ITENS = "A Planilha não possui mais itens.";
	private static final String ERROR_NEXT_NEVER_CALLED = "Não existe elemento corrente porque o método next() não foi chamado desde que o Data Source foi aberto.";
	private static final String ERROR_NON_EXISTING_COLUMN = "A planilha não possui uma coluna com o nome [%s].";
	private static final String ERROR_NON_EXISTING_ROW = "A planilha não possui uma linha com o índice[%d].";
	private static final String ERROR_CHAR_FORMART = "A coluna [%s] não possui conteúdo no formato Character.";
	private static final String ERROR_YES_NO_FORMAT = "A coluna [%s] não possui conteúdo no formato Y/N.";
	private static final String ERROR_RELEASING_INPUT_STREAM = "Ocorreu um erro ao liberar o input stream do documento.";
	private static final String ERROR_WRITING_CHANGES = "Não foi possível gravar as alterações no documento.";
	private static final String ERROR_CLOSING_DOCUMENT = "Ocorreu um erro ao fechar o documento.";
	private static final String ERROR_DOCUMENT_CLOSED = "O documento está fechado.";
	private static final String ERROR_DOCUMENT_OPEN = "O documento está aberto.";
	
	private static final String ARG_NAME_FILE = "file";
	private static final String ARG_NAME_IS = "is";
	
	private static final String VALUE_STRING_Y = "Y";
	private static final String VALUE_STRING_N = "N";
	
	private static final String TO_STRING_PATTERN_FILE = "File Based %s: \"%s\"";
	private static final String TO_STRING_PATTERN_INPUT_STREAM = "In Memory Based %s";

	private enum Type { FILE, INPUT_STREAM }
	
	private enum ImportActionHeader { INSERT, UPDATE, MERGE, REMOVE, FORCE, SYNC }
	
	private File file;
	private transient InputStream is;
	private Type type;
	private int sheetId;
	
	private transient Workbook workbook;
	private transient Sheet sheet;
	private int currRow;
	private boolean changed;
	private Map<String, Integer> columnMap = new HashMap<>();
	
	private SimpleSheetDataSource(Type type, int sheetId) {
		super();
		this.type = type;
		this.sheetId = sheetId;
	}
	
	/**
	 * Cria uma fonte de dados a partir do arquivo da planilha.
	 * 
	 * @param file Arquivo da planilha
	 * @param sheetId Índice da planilha
	 * @throws DataSourceFileException Se o arquivo não for encontrado
	 */
	public SimpleSheetDataSource(File file, int sheetId) throws DataSourceFileException {
		this(Type.FILE, sheetId);
		this.file = checkNotNull(file, ERROR_NULL_ARGUMENT, ARG_NAME_FILE);
		
		if (!file.exists()) {
			throw new DataSourceFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()));
		} else if (!file.canRead()) {
			throw new DataSourceFileException(String.format(ERROR_NON_READABLE_FILE, file.getAbsolutePath()));
		}
	}
	
	/**
	 * Cria uma fonte de dados a partir de um input stream.
	 * 
	 * @param is Input Stream com os dados da planilha
	 * @param sheetId Índice da planilha
	 */
	public SimpleSheetDataSource(InputStream is, int sheetId) {
		this(Type.INPUT_STREAM, sheetId);
		this.is = checkNotNull(is, ERROR_NULL_ARGUMENT, ARG_NAME_IS);
	}

	@Override
	public void open() throws DataImportException {
		checkState(workbook == null, ERROR_DOCUMENT_OPEN);
		
		if (type.equals(Type.FILE)) {
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new DataSourceFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()));
			}
		}
		
		try {
			workbook = WorkbookFactory.create(is);
		} catch (EncryptedDocumentException e) {
			throw new DataSourceDocumentException(ERROR_ENCRYPTED_DOCUMENT, e);
		} catch (InvalidFormatException e) {
			throw new DataSourceDocumentException(ERROR_INVALID_DOCUMENT_FORMAT, e);
		} catch (IOException e) {
			throw new DataSourceIOException(ERROR_READING_DOCUMENT, e);
		}
		
		try {
			sheet = workbook.getSheetAt(sheetId);
		} catch (IllegalArgumentException e) {
			throw new DataSourceDocumentException(String.format(ERROR_NON_EXISTING_SHEET, sheetId), e);
		}
		
		reset();
		mapColumns();
	}

	/**
	 * Mapeia as colunas da planilha a partir do seu cabeçalho.
	 * 
	 * @throws DataImportException Se a planilha não possui um cabeçalho 
	 */
	private void mapColumns() throws DataImportException {
		Row row = sheet.getRow(++currRow);
		
		if (row == null) {
			throw new DataImportException(ERROR_NO_HEADER);
		}
		
		Iterator<Cell> it = row.iterator();
		
		while (it.hasNext()) {
			Cell cell = it.next();
			columnMap.put(cell.getStringCellValue().toUpperCase(), cell.getColumnIndex());
		}
		
		LOGGER.debug("Colunas mapeadas: {0}", columnMap);
	}

	@Override
	public boolean isWritable() {
		if (type == Type.FILE && file != null) {
			return file.canWrite();
		}
		
		return false;
	}

	@Override
	public boolean hasNext() throws DataSourceInvalidStateException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		return sheet.getRow(currRow + 1) != null;
	}

	@Override
	public DataImportItem<Integer, T> next() throws DataSourceInvalidStateException, DataSourceNoMoreItensException, DataSourceFormatException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		
		if (!hasNext()) {
			throw new DataSourceNoMoreItensException(ERROR_NO_MORE_ITENS);
		}
		
		currRow++;
		
		return current();
	}

	@Override
	public DataImportItem<Integer, T> current() throws DataSourceInvalidStateException, DataSourceFormatException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		checkState(currRow >= 1, ERROR_NEXT_NEVER_CALLED);
		
		if (currRow < 1) {
			throw new DataSourceInvalidStateException(ERROR_NEXT_NEVER_CALLED);
		}
		
		Boolean insert = readYesNoCell(ImportActionHeader.INSERT.name());
		Boolean update = readYesNoCell(ImportActionHeader.UPDATE.name());
		Boolean merge = readYesNoCell(ImportActionHeader.MERGE.name());
		Boolean remove = readYesNoCell(ImportActionHeader.REMOVE.name());
		Boolean force = readYesNoCell(ImportActionHeader.FORCE.name());
		Boolean sync = readYesNoCell(ImportActionHeader.SYNC.name());
		DataImportInstructions instructions = new DataImportInstructions(insert, update, merge, remove, force, sync);
		
		return new DataImportItem<>(currRow, readCurrentItemData(), instructions);
	}

	/**
	 * Lê os dados do item atual.
	 * 
	 * @return Dados do item atual
	 */
	protected abstract T readCurrentItemData();
	
	/**
	 * Lê o conteúdo de uma celula do tipo String.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected String readStringCell(String columnName) {
		Cell cell = getCurrentRowCell(columnName);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getStringCellValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo String.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeStringCell(Integer rowIndex, String columnName, String value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		String currVal = getRowCell(rowIndex, columnName).getStringCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
		
		return change;
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Character.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 * @throws DataSourceFormatException Se a célula não possui conteúdo no formado Character
	 */
	protected Character readCharCell(String columnName) throws DataSourceFormatException {
		String stringValue = readStringCell(columnName);
		
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		} else if (stringValue.length() > 1) {
			throw new DataSourceFormatException(String.format(ERROR_CHAR_FORMART, columnName));
		}
		
		return stringValue.charAt(0);
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Character.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @return value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeCharCell(Integer rowIndex, String columnName, Character value) {
		return writeStringCell(rowIndex, columnName, value == null ? null : String.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Double.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Double readDoubleCell(String columnName) {
		Cell cell = getCurrentRowCell(columnName);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getNumericCellValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Double.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeDoubleCell(Integer rowIndex, String columnName, Double value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		Double currVal = getRowCell(rowIndex, columnName).getNumericCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
		
		return change;
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Float.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Float readFloatCell(String columnName) {
		Double doubleValue = readDoubleCell(columnName);
		return doubleValue == null ? null : doubleValue.floatValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Float.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeFloatCell(Integer rowIndex, String columnName, Float value) {
		return writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Long.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Long readLongCell(String columnName) {
		Double doubleValue = readDoubleCell(columnName);
		return doubleValue == null ? null : doubleValue.longValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Long.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeLongCell(Integer rowIndex, String columnName, Long value) {
		return writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Integer.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Integer readIntegerCell(String columnName) {
		Double doubleValue = readDoubleCell(columnName);
		return doubleValue == null ? null : doubleValue.intValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Integer.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeIntegerCell(Integer rowIndex, String columnName, Integer value) {
		return writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Short.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Short readShortCell(String columnName) {
		Double doubleValue = readDoubleCell(columnName);
		return doubleValue == null ? null : doubleValue.shortValue();
	}

	/**
	 * Escreve o conteúdo de uma celula do tipo Short.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeShortCell(Integer rowIndex, String columnName, Short value) {
		return writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Byte.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Byte readByteCell(String columnName) {
		Double doubleValue = readDoubleCell(columnName);
		return doubleValue == null ? null : doubleValue.byteValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Byte.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeByteCell(Integer rowIndex, String columnName, Byte value) {
		return writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Boolean.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Boolean readBooleanCell(String columnName) {
		Cell cell = getCurrentRowCell(columnName);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getBooleanCellValue();
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Boolean.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeBooleanCell(Integer rowIndex, String columnName,Boolean value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		Boolean currVal = getRowCell(rowIndex, columnName).getBooleanCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
		
		return change;
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Date.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Date readDateCell(String columnName) {
		Cell cell = getCurrentRowCell(columnName);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())){
			return null;
		}
		
		return cell.getDateCellValue();
	}
	
	/**
	 * escreve o conteúdo de uma celula do tipo Date.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeDateCell(Integer rowIndex, String columnName, Date value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		Date currVal = getRowCell(rowIndex, columnName).getDateCellValue();
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
		
		return change;
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Calendar.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Calendar readCalendarCell(String columnName) {
		Date date = readDateCell(columnName);
		
		if (date == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return calendar;
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Calendar.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeCalendarCell(Integer rowIndex, String columnName, Calendar value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		Date dateVal = getRowCell(rowIndex, columnName).getDateCellValue();
		Calendar currVal = null;
		
		if (dateVal != null) {
			currVal = Calendar.getInstance();
			currVal.setTime(dateVal);
		}
		
		boolean change = !value.equals(currVal);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
		
		return change;
	}

	/**
	 * Lê o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param columnName Nome da coluna
	 * @return True se o conteúdo for 'Y', False se o conteúdo for 'N' e null se for indefinido
	 * @throws DataSourceFormatException Se a célula não possui conteúdo no formato Y/N
	 */
	protected Boolean readYesNoCell(String columnName) throws DataSourceFormatException {
		String value = getCurrentRowCell(columnName).getStringCellValue();
		
		if (VALUE_STRING_Y.equalsIgnoreCase(value)) {
			return true;
		} else if (VALUE_STRING_N.equalsIgnoreCase(value)) {
			return false;
		} else if (value != null && !value.isEmpty()) {
			throw new DataSourceFormatException(String.format(ERROR_YES_NO_FORMAT, columnName));
		}
		
		return null;
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value True para o conteúdo 'Y', False para o conteúdo 'N'
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeYesNoCell(Integer rowIndex, String columnName, Boolean value) {
		if (value == null) {
			return writeNullValue(rowIndex, columnName);
		}
		
		String currVal = getRowCell(rowIndex, columnName).getStringCellValue();
		boolean change = (!VALUE_STRING_Y.equals(currVal) && !VALUE_STRING_N.equals(currVal))
				|| (VALUE_STRING_Y.equals(currVal) && !value)
				|| (VALUE_STRING_N.equals(currVal) && value);
		
		if (change) {
			getRowCell(rowIndex, columnName).setCellValue(value ? VALUE_STRING_Y : VALUE_STRING_N);
		}
		
		return change;
	}
	
	/**
	 * Escreve o conteúdo nulo de uma celula.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	private boolean writeNullValue(Integer rowIndex, String columnName) {
		Cell cell = getRowCell(rowIndex, columnName);
		
		if (CellType.BLANK.equals(cell.getCellTypeEnum())) {
			return false;
		}
		
		String value = null;
		cell.setCellValue(value);
		cell.setCellType(CellType.BLANK);
		
		return true;
	}
	
	/**
	 * Recupera uma célula da linha atual a partir do nome da sua coluna.
	 * 
	 * @param name Nome da coluna da célula
	 * @return Célula encontrada
	 * @throws IllegalArgumentException Se o nome da coluna for inválido ou se a coluna for
	 * inexistente
	 */
	private Cell getCurrentRowCell(String name) {
		return getRowCell(currRow, name);
	}
	
	/**
	 * Recupera uma célula de uma linha a partir do nome da sua coluna.
	 * 
	 * @param rowIndex Índice da linha
	 * @param name Nome da coluna da célula
	 * @return Célula encontrada
	 * @throws IllegalArgumentException Se o nome da coluna for inválido ou se a coluna for
	 * inexistente
	 */
	private Cell getRowCell(Integer rowIndex, String name) {
		checkNotNull(rowIndex, ERROR_NULL_ARGUMENT, "rowIndex");
		checkNotNull(name, ERROR_NULL_ARGUMENT, "name");
		
		if (!columnMap.containsKey(name)) {
			throw new IllegalArgumentException(String.format(ERROR_NON_EXISTING_COLUMN, name));
		}
		
		Row row = sheet.getRow(rowIndex);
		
		if (row == null) {
			throw new IllegalArgumentException(String.format(ERROR_NON_EXISTING_ROW, rowIndex));
		}
		
		return row.getCell(columnMap.get(name));
	}
	
	@Override
	public boolean sync(DataImportItem<Integer, T> item) throws DataImportException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		boolean rowChanged = syncRow(item.getId(), item.getData());
		changed = changed || rowChanged;
		return changed;
	}

	/**
	 * Sincroniza os dados de uma linha da planilha.
	 * 
	 * @param rowIndex Índice da linha
	 * @param data Dados da linha
	 * @return True se houve uma mudança real nos dados da planilha, False caso contrário.
	 */
	protected abstract boolean syncRow(Integer rowIndex, T data);

	@Override
	public void close() throws DataImportException {
		checkState(workbook != null, ERROR_DOCUMENT_CLOSED);
		
		try {
			is.close();
		} catch (IOException e) {
			throw new DataSourceIOException(ERROR_RELEASING_INPUT_STREAM, e);
		} finally {
			is = null;
		}
		
		if (changed && isWritable()) {
			flush();
		}
		
		try {
			workbook.close();
		} catch (IOException e) {
			throw new DataSourceIOException(ERROR_CLOSING_DOCUMENT, e);
		} finally {
			workbook = null;
			sheet = null;
			reset();
		}
	}
	
	/**
	 * Salva o workbook no arquivo de onde foi lido.
	 * 
	 * @throws DataSourceFileException Se o arquivo não for encontrado
	 * @throws DataSourceIOException Se ocorrer um erro na escrita do arquivo
	 */
	private void flush() throws DataSourceFileException, DataSourceIOException {
		if (type.equals(Type.FILE) && file != null && file.canWrite()) {
			try {
				workbook.write(new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				throw new DataSourceFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()), e);
			} catch (IOException e) {
				throw new DataSourceIOException(ERROR_WRITING_CHANGES, e);
			}
		}
	}

	/**
	 * Reseta as variáveis auxiliares.
	 */
	private void reset() {
		changed = false;
		currRow = -1;
		columnMap.clear();
	}
	
	@Override
	public String toString() {
		if (file != null) {
			return String.format(TO_STRING_PATTERN_FILE, getClass().getSimpleName(), file.getAbsolutePath());
		}
		
		return String.format(TO_STRING_PATTERN_INPUT_STREAM, getClass().getSimpleName());
	}
}
