package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
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

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceIOException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceNoMoreItensException;

/**
 * Implementação base da fonte de dados em planilha Excel simples.
 *
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public abstract class BaseSheetDataSource<T extends Serializable> implements ImportDataSource<Integer, T> {
	private static final long serialVersionUID = -7387063988593887736L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSheetDataSource.class);
	
	private static final String ERROR_NULL_FILE = "O arquivo não pode ser nulo.";
	private static final String ERROR_NULL_IS = "O input stream não pode ser nulo.";
	private static final String ERROR_NULL_COLUMN_NAME = "O nome da coluna não pode ser nulo.";
	private static final String ERROR_FILE_NOT_FOUND = "Não foi possível encontrar o arquivo [%s].";
	private static final String ERROR_UNDEFINED_TYPE = "O tipo da fonte de dados não foi definido.";
	private static final String ERROR_READING_DOCUMENT = "Ocorreu um erro na leitura do documento.";
	private static final String ERROR_ENCRYPTED_DOCUMENT = "O documento está criptografado.";
	private static final String ERROR_INVALID_DOCUMENT_FORMAT = "O documento possui um formato inválido.";
	private static final String ERROR_NON_READABLE_FILE = "O arquivo [%s] não pode ser lido.";
	private static final String ERROR_NON_EXISTING_SHEET = "O documento não possui planilha com índice [%d].";
	private static final String ERROR_NO_HEADER = "A planilha não possui um cabeçalho.";
	private static final String ERROR_NO_MORE_ITENS = "A Planilha não possui mais itens.";
	private static final String ERROR_NON_EXISTING_COLUMN = "A planilha não possui uma coluna com o nome [%s].";
	private static final String ERROR_NON_EXISTING_ROW = "A planilha não possui uma linha com o índice[%d].";
	private static final String ERROR_CHAR_FORMART = "A coluna [%s] não possui conteúdo no formato Character.";
	private static final String ERROR_YES_NO_FORMAT = "A coluna [%s] não possui conteúdo no formato Y/N.";
	
	private static final String ERROR_WRITING_CHANGES = "Não foi possível gravar as alterações no documento.";
	private static final String ERROR_CLOSING_DOCUMENT = "Ocorreu um erro ao fechar o documento.";
	
	private static final String ERROR_DOCUMENT_CLOSED = "O documento está fechado.";
	private static final String ERROR_DOCUMENT_OPEN = "O documento está aberto.";
	
	private static final String VALUE_STRING_Y = "Y";
	private static final String VALUE_STRING_N = "N";

	private static enum Type { FILE, INPUT_STREAM };
	
	private static enum ImportActionHeader { INSERT, UPDATE, MERGE, REMOVE, FORCE, SYNC };
	
	private File file;
	private transient InputStream is;
	private Type type;
	private int sheetId;
	
	private transient Workbook workbook;
	private transient Sheet sheet;
	private int currRow;
	private boolean changed;
	private Map<String, Integer> columnMap = new HashMap<String, Integer>();
	
	private BaseSheetDataSource(Type type, int sheetId) {
		super();
		this.type = type;
		this.sheetId = sheetId;
	}
	
	/**
	 * Cria uma fonte de dados a partir do arquivo da planilha.
	 * 
	 * @param file Arquivo da planilha
	 * @param sheetId Índice da planilha
	 * @throws ImportDataSourceFileException Se o arquivo não for encontrado
	 */
	public BaseSheetDataSource(File file, int sheetId) throws ImportDataSourceFileException {
		this(Type.FILE, sheetId);
		
		if (file == null) {
			throw new IllegalArgumentException(ERROR_NULL_FILE);
		} else if (!file.exists()) {
			throw new ImportDataSourceFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()));
		}
		
		this.file = file;
	}
	
	/**
	 * Cria uma fonte de dados a partir de um input stream.
	 * 
	 * @param is Input stream com os dados da planilha
	 * @param sheetId Índice da planilha
	 */
	public BaseSheetDataSource(InputStream is, int sheetId) {
		this(Type.INPUT_STREAM, sheetId);
		
		if (is == null) {
			throw new IllegalArgumentException(ERROR_NULL_IS);
		}
		
		this.is = is;
	}

	@Override
	public void open() throws ImportDataSourceException {
		validadeIsClosed();
		
		switch (type) {
		case FILE:
			workbook = openFromFile();
			break;
		case INPUT_STREAM:
			workbook = openFromInputStream();
			break;
		default:
			throw new ImportDataSourceException(ERROR_UNDEFINED_TYPE);
		}
		
		try {
			sheet = workbook.getSheetAt(sheetId);
		} catch (IllegalArgumentException e) {
			throw new ImportDataSourceDocumentException(String.format(ERROR_NON_EXISTING_SHEET, sheetId), e);
		}
		
		reset();
		mapColumns();
	}

	/**
	 * Abre o workbook a partir de um arquivo.
	 * 
	 * @return Workbook aberto
	 * @throws ImportDataSourceException Se ocorrer um erro na abertura do workbook
	 */
	private Workbook openFromFile() throws ImportDataSourceException {
		if (!file.canRead()) {
			throw new ImportDataSourceIOException(String.format(ERROR_NON_READABLE_FILE, file.getAbsolutePath()));
		}
		
		try {
			return WorkbookFactory.create(file, null, !file.canWrite());
		} catch (EncryptedDocumentException e) {
			throw new ImportDataSourceDocumentException(ERROR_ENCRYPTED_DOCUMENT, e);
		} catch (InvalidFormatException e) {
			throw new ImportDataSourceDocumentException(ERROR_INVALID_DOCUMENT_FORMAT, e);
		} catch (IOException e) {
			throw new ImportDataSourceIOException(ERROR_READING_DOCUMENT, e);
		}
	}

	/**
	 * Abre o workbook a partir de um input stream.
	 * 
	 * @return Workbook aberto
	 * @throws ImportDataSourceException Se ocorrer um erro na abertura do workbook
	 */
	private Workbook openFromInputStream() throws ImportDataSourceException {
		try {
			return WorkbookFactory.create(is);
		} catch (EncryptedDocumentException e) {
			throw new ImportDataSourceDocumentException(ERROR_ENCRYPTED_DOCUMENT, e);
		} catch (InvalidFormatException e) {
			throw new ImportDataSourceDocumentException(ERROR_INVALID_DOCUMENT_FORMAT, e);
		} catch (IOException e) {
			throw new ImportDataSourceIOException(ERROR_READING_DOCUMENT, e);
		}
	}

	/**
	 * Mapeia as colunas da planilha a partir do seu cabeçalho.
	 * 
	 * @throws ImportDataSourceException Se a planilha não possui um cabeçalho 
	 */
	private void mapColumns() throws ImportDataSourceException {
		Row row = sheet.getRow(++currRow);
		
		if (row == null) {
			throw new ImportDataSourceException(ERROR_NO_HEADER);
		}
		
		Iterator<Cell> it = row.iterator();
		
		while (it.hasNext()) {
			Cell cell = it.next();
			columnMap.put(cell.getStringCellValue().toUpperCase(), cell.getColumnIndex());
		}
		
		LOGGER.debug("Colunas mapeadas: " + columnMap);
	}

	@Override
	public boolean isWritable() {
		if (type == Type.FILE && file != null) {
			return file.canWrite();
		}
		
		return false;
	}

	@Override
	public boolean hasNext() throws ImportDataSourceException {
		validadeIsOpen();
		return sheet.getRow(currRow + 1) != null;
	}

	@Override
	public DataImportItem<Integer, T> next() throws ImportDataSourceNoMoreItensException, ImportDataSourceException {
		validadeIsOpen();
		
		if (!hasNext()) {
			throw new ImportDataSourceNoMoreItensException(ERROR_NO_MORE_ITENS);
		}
		
		currRow++;
		
		Boolean insert = readYesNoCell(ImportActionHeader.INSERT.name());
		Boolean update = readYesNoCell(ImportActionHeader.UPDATE.name());
		Boolean merge = readYesNoCell(ImportActionHeader.MERGE.name());
		Boolean remove = readYesNoCell(ImportActionHeader.REMOVE.name());
		Boolean force = readYesNoCell(ImportActionHeader.FORCE.name());
		Boolean sync = readYesNoCell(ImportActionHeader.SYNC.name());
		
		return new DataImportItem<Integer, T>(currRow, readCurrentItemData(), insert, update, merge, remove, force, sync);
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
	 */
	protected void writeStringCell(Integer rowIndex, String columnName, String value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Character.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 * @throws ImportDataSourceFormatException Se a célula não possui conteúdo no formado Character
	 */
	protected Character readCharCell(String columnName) throws ImportDataSourceFormatException {
		String stringValue = readStringCell(columnName);
		
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		} else if (stringValue.length() > 1) {
			throw new ImportDataSourceFormatException(String.format(ERROR_CHAR_FORMART, columnName));
		}
		
		return stringValue.charAt(0);
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Character.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @return value Conteúdo da célula
	 */
	protected void writeCharCell(Integer rowIndex, String columnName, Character value) {
		writeStringCell(rowIndex, columnName, value == null ? null : String.valueOf(value));
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
	 */
	protected void writeDoubleCell(Integer rowIndex, String columnName, Double value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
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
	 */
	protected void writeFloatCell(Integer rowIndex, String columnName, Float value) {
		writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
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
	 */
	protected void writeLongCell(Integer rowIndex, String columnName, Long value) {
		writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
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
	 */
	protected void writeIntegerCell(Integer rowIndex, String columnName, Integer value) {
		writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
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
	 */
	protected void writeShortCell(Integer rowIndex, String columnName, Short value) {
		writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
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
	 */
	protected void writeByteCell(Integer rowIndex, String columnName, Byte value) {
		writeDoubleCell(rowIndex, columnName, value == null ? null : Double.valueOf(value));
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
	 */
	protected void writeBooleanCell(Integer rowIndex, String columnName,Boolean value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
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
	 */
	protected void writeDateCell(Integer rowIndex, String columnName, Date value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
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
	 */
	protected void writeCalendarCell(Integer rowIndex, String columnName, Calendar value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value);
		}
	}

	/**
	 * Lê o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param columnName Nome da coluna
	 * @return True se o conteúdo for 'Y', False se o conteúdo for 'N' e null se for indefinido
	 * @throws ImportDataSourceFormatException Se a célula não possui conteúdo no formato Y/N
	 */
	protected Boolean readYesNoCell(String columnName) throws ImportDataSourceFormatException {
		String value = getCurrentRowCell(columnName).getStringCellValue();
		
		if (VALUE_STRING_Y.equalsIgnoreCase(value)) {
			return true;
		} else if (VALUE_STRING_N.equalsIgnoreCase(value)) {
			return false;
		} else if (value != null && !value.isEmpty()) {
			throw new ImportDataSourceFormatException(String.format(ERROR_YES_NO_FORMAT, columnName));
		}
		
		return null;
	}
	
	/**
	 * Escreve o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @param value True para o conteúdo 'Y', False para o conteúdo 'N'
	 */
	protected void writeYesNoCell(Integer rowIndex, String columnName, Boolean value) {
		if (value == null) {
			writeNullValue(rowIndex, columnName);
		} else {
			getRowCell(rowIndex, columnName).setCellValue(value ? VALUE_STRING_Y : VALUE_STRING_N);
		}
	}
	
	/**
	 * Escreve o conteúdo nulo de uma celula.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 */
	private void writeNullValue(Integer rowIndex, String columnName) {
		String value = null;
		Cell cell = getRowCell(rowIndex, columnName);
		cell.setCellValue(value);
		cell.setCellType(CellType.BLANK);
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
		if (name == null) {
			throw new IllegalArgumentException(ERROR_NULL_COLUMN_NAME);
		}else if (!columnMap.containsKey(name)) {
			throw new IllegalArgumentException(String.format(ERROR_NON_EXISTING_COLUMN, name));
		}
		
		Row row = sheet.getRow(rowIndex);
		
		if (row == null) {
			throw new IllegalArgumentException(String.format(ERROR_NON_EXISTING_ROW, rowIndex));
		}
		
		return row.getCell(columnMap.get(name));
	}
	
	@Override
	public void sync(DataImportItem<Integer, T> item) throws ImportDataSourceException {
		validadeIsOpen();
		changed = changed || syncRow(item.getId(), item.getData());
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
	public void close() throws ImportDataSourceException {
		validadeIsOpen();
		
		try {
			if (changed && isWritable()) {
				flush();
			}
		} catch (FileNotFoundException e) {
			throw new ImportDataSourceFileException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()), e);
		} catch (IOException e) {
			throw new ImportDataSourceIOException(ERROR_WRITING_CHANGES, e);
		}
		
		try {
			workbook.close();
			workbook = null;
			sheet = null;
			reset();
		} catch (IOException e) {
			throw new ImportDataSourceIOException(ERROR_CLOSING_DOCUMENT, e);
		}
	}
	
	/**
	 * Salva o workbook no arquivo de onde foi lido.
	 * 
	 * @throws FileNotFoundException Se o arquivo não for encontrado
	 * @throws IOException Se ocorrer um erro na escrita do arquivo
	 */
	private void flush() throws FileNotFoundException, IOException {
		if (file == null || !file.canWrite()) {
			return;
		}
		// TODO: https://stackoverflow.com/questions/35078399/is-it-possible-to-first-read-and-then-write-into-same-spreadsheet-file
		workbook.write(new FileOutputStream(file));
	}

	/**
	 * Reseta as variáveis auxiliares.
	 */
	private void reset() {
		changed = false;
		currRow = -1;
		columnMap.clear();
	}

	/**
	 * Valida que o documento está aberto.
	 * 
	 * @throws ImportDataSourceInvalidStateException Se o documento estiver fechado
	 */
	private void validadeIsOpen() throws ImportDataSourceInvalidStateException {
		if (workbook == null) {
			throw new ImportDataSourceInvalidStateException(ERROR_DOCUMENT_CLOSED);
		}
	}
	
	/**
	 * Valida que o documento está fechado.
	 * 
	 * @throws ImportDataSourceInvalidStateException Se o documento estiver aberto
	 */
	private void validadeIsClosed() throws ImportDataSourceInvalidStateException {
		if (workbook != null) {
			throw new ImportDataSourceInvalidStateException(ERROR_DOCUMENT_OPEN);
		}
	}
}
