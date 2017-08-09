/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sgpf.common.document.excel.WorkbookWrapper;
import br.com.sgpf.common.document.excel.impl.WorkbookWrapperImpl;
import br.com.sgpf.common.document.exception.DocumentException;
import br.com.sgpf.common.document.exception.DocumentFileException;
import br.com.sgpf.common.document.exception.DocumentFormatException;
import br.com.sgpf.common.document.exception.DocumentIOException;
import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceFormatException;
import br.com.sgpf.common.domain.dataimport.exception.DataSourceNoMoreItensException;

/**
 * Implementação fonte de dados em planilha Excel simples.
 *
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 * 
 * @author Sergio Puntar
 */
public abstract class SimpleSheetDataSource<T extends Serializable> implements ImportDataSource<Integer, T> {
	private static final long serialVersionUID = -7387063988593887736L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSheetDataSource.class);
	
	private static final String ERROR_NO_HEADER = "A planilha não possui um cabeçalho.";
	private static final String ERROR_NO_MORE_ITENS = "A planilha não possui mais itens.";
	private static final String ERROR_NEXT_NEVER_CALLED = "Não existe elemento corrente porque o método next() não foi chamado desde que o Data Source foi aberto.";
	private static final String ERROR_NON_EXISTING_COLUMN = "A planilha não possui uma coluna com o nome [%s].";
	
	private static final String ARG_NAME_FILE = "file";
	private static final String ARG_NAME_IS = "is";
	private static final String ARG_NAME_SHEET_ID = "is";
	private static final String ARG_NAME_COLUMN_NAME = "columnName";
	
	private static final String TO_STRING_PATTERN = "%s based on Document [%s]";


	private enum ImportActionHeader { INSERT, UPDATE, MERGE, REMOVE, FORCE, SYNC }
	
	private WorkbookWrapper workbook;
	private int currRow;
	private boolean changed;
	private Map<String, Integer> columnMap = new HashMap<>();
	
	/**
	 * Cria uma fonte de dados a partir do arquivo da planilha.
	 * 
	 * @param file Arquivo da planilha
	 * @param sheetId Índice da planilha
	 * @throws DataSourceException Se o arquivo não for encontrado
	 */
	public SimpleSheetDataSource(File file, Integer sheetId) throws DataSourceDocumentException {
		super();
		checkNotNull(file, ERROR_NULL_ARGUMENT, ARG_NAME_FILE);
		checkNotNull(sheetId, ERROR_NULL_ARGUMENT, ARG_NAME_SHEET_ID);
		
		try {
			workbook = new WorkbookWrapperImpl(file, sheetId);
		} catch (DocumentFileException e) {
			throw new DataSourceDocumentException(e.getMessage(), e);
		}
	}
	
	/**
	 * Cria uma fonte de dados a partir de um input stream.
	 * 
	 * @param is Input Stream com os dados da planilha
	 * @param sheetId Índice da planilha
	 */
	public SimpleSheetDataSource(InputStream is, Integer sheetId) {
		super();
		checkNotNull(is, ERROR_NULL_ARGUMENT, ARG_NAME_IS);
		checkNotNull(sheetId, ERROR_NULL_ARGUMENT, ARG_NAME_SHEET_ID);
		
		workbook = new WorkbookWrapperImpl(is, sheetId);
	}

	@Override
	public boolean isWritable() {
		return workbook.isWritable();
	}
	
	@Override
	public void open() throws DataSourceDocumentException, DataSourceFormatException {
		try {
			workbook.open();
		} catch (DocumentException e) {
			throw new DataSourceDocumentException(e.getMessage(), e);
		}
		
		reset();
		mapColumns();
	}

	/**
	 * Mapeia as colunas da planilha a partir do seu cabeçalho.
	 * 
	 * @throws DataSourceFormatException Se a planilha não possui um cabeçalho 
	 */
	private void mapColumns() throws DataSourceFormatException {
		Row row = workbook.getWorkingSheet().getRow(++currRow);
		
		if (row == null) {
			throw new DataSourceFormatException(ERROR_NO_HEADER);
		}
		
		Iterator<Cell> it = row.iterator();
		
		while (it.hasNext()) {
			Cell cell = it.next();
			columnMap.put(cell.getStringCellValue().toUpperCase(), cell.getColumnIndex());
		}
		
		LOGGER.debug("Colunas mapeadas: {0}", columnMap);
	}
	
	/**
	 * Recupera o índice de uma coluna da planilha dado seu nome.
	 * 
	 * @param columnName Nome da coluna
	 * @return Índice da coluna
	 */
	private Integer getColumnIndex(String columnName) {
		checkNotNull(columnName, ERROR_NULL_ARGUMENT, ARG_NAME_COLUMN_NAME);
		checkArgument(columnMap.containsKey(columnName), ERROR_NON_EXISTING_COLUMN, columnName);
		return columnMap.get(columnName);
	}

	@Override
	public boolean hasNext() {
		return workbook.getWorkingSheet().getRow(currRow + 1) != null;
	}

	@Override
	public DataImportItem<Integer, T> next() throws DataSourceNoMoreItensException, DataSourceFormatException {
		if (!hasNext()) {
			throw new DataSourceNoMoreItensException(ERROR_NO_MORE_ITENS);
		}
		
		currRow++;
		
		return current();
	}

	@Override
	public DataImportItem<Integer, T> current() throws DataSourceFormatException {
		checkState(currRow >= 1, ERROR_NEXT_NEVER_CALLED);
		
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
	
	@Override
	public boolean sync(DataImportItem<Integer, T> item) throws DataImportException {
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
		try {
			workbook.close(changed && workbook.isWritable());
		} catch (DocumentFileException | DocumentIOException e) {
			throw new DataSourceDocumentException(e.getMessage(), e);
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
	
	/**
	 * Lê o conteúdo de uma celula do tipo String.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected String readStringCell(String columnName) {
		return workbook.readStringCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeStringCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Character.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 * @throws DataSourceFormatException Se a célula não possui conteúdo no formado Character
	 */
	protected Character readCharCell(String columnName) throws DataSourceFormatException {
		try {
			return workbook.readCharCell(currRow, getColumnIndex(columnName));
		} catch (DocumentFormatException e) {
			throw new DataSourceFormatException(e.getMessage(), e);
		}
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
		return workbook.writeCharCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Double.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Double readDoubleCell(String columnName) {
		return workbook.readDoubleCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeDoubleCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Float.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Float readFloatCell(String columnName) {
		return workbook.readFloatCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeFloatCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Long.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Long readLongCell(String columnName) {
		return workbook.readLongCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeLongCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Integer.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Integer readIntegerCell(String columnName) {
		return workbook.readIntegerCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeIntegerCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Short.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Short readShortCell(String columnName) {
		return workbook.readShortCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeShortCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Byte.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Byte readByteCell(String columnName) {
		return workbook.readByteCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeByteCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Boolean.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Boolean readBooleanCell(String columnName) {
		return workbook.readBooleanCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeBooleanCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Date.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Date readDateCell(String columnName) {
		return workbook.readDateCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeDateCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Lê o conteúdo de uma celula do tipo Calendar.
	 * 
	 * @param columnName Nome da coluna
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	protected Calendar readCalendarCell(String columnName) {
		return workbook.readCalendarCell(currRow, getColumnIndex(columnName));
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
		return workbook.writeCalendarCell(rowIndex, getColumnIndex(columnName), value);
	}

	/**
	 * Lê o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param columnName Nome da coluna
	 * @return True se o conteúdo for 'Y', False se o conteúdo for 'N' e null se for indefinido
	 * @throws DataSourceFormatException Se a célula não possui conteúdo no formato Y/N
	 */
	protected Boolean readYesNoCell(String columnName) throws DataSourceFormatException {
		try {
			return workbook.readYesNoCell(currRow, getColumnIndex(columnName));
		} catch (DocumentFormatException e) {
			throw new DataSourceFormatException(e.getMessage(), e);
		}
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
		return workbook.writeYesNoCell(rowIndex, getColumnIndex(columnName), value);
	}
	
	/**
	 * Escreve o conteúdo nulo de uma celula.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	protected boolean writeNullValue(Integer rowIndex, String columnName) {
		return workbook.writeNullValue(rowIndex, getColumnIndex(columnName));
	}

	@Override
	public String toString() {
		return String.format(TO_STRING_PATTERN, getClass().getSimpleName(), workbook.toString());
	}
}
