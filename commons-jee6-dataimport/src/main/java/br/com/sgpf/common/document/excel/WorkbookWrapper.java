/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.excel;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import br.com.sgpf.common.document.exception.DocumentException;
import br.com.sgpf.common.document.exception.DocumentFileException;
import br.com.sgpf.common.document.exception.DocumentFormatException;
import br.com.sgpf.common.document.exception.DocumentIOException;

/**
 * Interface para manipulação de documentos Excel que provê métodos facilitadores de acesso aos
 * dados das planilhas.
 * 
 * @author Sergio Puntar
 */
public interface WorkbookWrapper extends Serializable {

	/**
	 * Verifica se o documento é gravável.
	 * 
	 * @return True se for gravável, False caso contrário
	 */
	boolean isWritable();

	/**
	 * Abre o documento para iniciar a leitura ou escrita.
	 * 
	 * @throws DocumentException Se ocorrer um erro na leitura do documento
	 */
	void open() throws DocumentException;

	/**
	 * Define un novo índice de planilha de trabalho.<br>
	 * Se o documento estiver aberto, vai tentar substituir a planilha de trabalho atual pela
	 * planilha com o novo índice. Se o documento estiver fechado, vai armazenar o índice para ser
	 * utilizado no momento da abertura. 
	 * 
	 * @param workingSheetIndex Índice da planilha de trabalho
	 * @throws DocumentException Caso não exista uma planilha com o índice definido
	 */
	void setWorkingSheetId(Integer workingSheetIndex) throws DocumentException;

	/**
	 * Fecha o documento salvando as alterações realizadas ou não.
	 * 
	 * @param save Flag indicando se as alterações devem ser salvas ou não
	 * @throws DocumentFileException Se o arquivo do documento não for encontrado
	 * @throws DocumentIOException Se hpuver um erro de escrita do documento
	 */
	void close(Boolean save) throws DocumentFileException, DocumentIOException;

	/**
	 * Recupera o Workbook.
	 * 
	 * @return Workbook
	 */
	Workbook getWorkbook();

	/**
	 * Recupera a planilha de trabalho.
	 * 
	 * @return Planilha de trabalho
	 */
	Sheet getWorkingSheet();

	/**
	 * Lê o conteúdo de uma celula do tipo String.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	String readStringCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo String.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeStringCell(Integer rowIndex, Integer cellIndex, String value);

	/**
	 * Lê o conteúdo de uma celula do tipo Character.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 * @throws DocumentFormatException Se a célula não possui conteúdo no formado Character
	 */
	Character readCharCell(Integer rowIndex, Integer cellIndex) throws DocumentFormatException;

	/**
	 * Escreve o conteúdo de uma celula do tipo Character.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeCharCell(Integer rowIndex, Integer cellIndex, Character value);

	/**
	 * Lê o conteúdo de uma celula do tipo Double.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Double readDoubleCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Double.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeDoubleCell(Integer rowIndex, Integer cellIndex, Double value);

	/**
	 * Lê o conteúdo de uma celula do tipo Float.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Float readFloatCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Float.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeFloatCell(Integer rowIndex, Integer cellIndex, Float value);

	/**
	 * Lê o conteúdo de uma celula do tipo Long.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Long readLongCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Long.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeLongCell(Integer rowIndex, Integer cellIndex, Long value);

	/**
	 * Lê o conteúdo de uma celula do tipo Integer.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Integer readIntegerCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Integer.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeIntegerCell(Integer rowIndex, Integer cellIndex, Integer value);

	/**
	 * Lê o conteúdo de uma celula do tipo Short.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Short readShortCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Short.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeShortCell(Integer rowIndex, Integer cellIndex, Short value);

	/**
	 * Lê o conteúdo de uma celula do tipo Byte.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Byte readByteCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Byte.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeByteCell(Integer rowIndex, Integer cellIndex, Byte value);

	/**
	 * Lê o conteúdo de uma celula do tipo Boolean.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Boolean readBooleanCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Boolean.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeBooleanCell(Integer rowIndex, Integer cellIndex, Boolean value);

	/**
	 * Lê o conteúdo de uma celula do tipo Date.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Date readDateCell(Integer rowIndex, Integer cellIndex);

	/**
	 * escreve o conteúdo de uma celula do tipo Date.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeDateCell(Integer rowIndex, Integer cellIndex, Date value);

	/**
	 * Lê o conteúdo de uma celula do tipo Calendar.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Conteúdo da célula, null se a célula estiver vazia
	 */
	Calendar readCalendarCell(Integer rowIndex, Integer cellIndex);

	/**
	 * Escreve o conteúdo de uma celula do tipo Calendar.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value Conteúdo da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeCalendarCell(Integer rowIndex, Integer cellIndex, Calendar value);

	/**
	 * Lê o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return True se o conteúdo for 'Y', False se o conteúdo for 'N' e null se for indefinido
	 * @throws DocumentFormatException Se a célula não possui conteúdo no formato Y/N
	 */
	Boolean readYesNoCell(Integer rowIndex, Integer cellIndex) throws DocumentFormatException;

	/**
	 * Escreve o conteúdo de uma celula do tipo Flag Y/N.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @param value True para o conteúdo 'Y', False para o conteúdo 'N'
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeYesNoCell(Integer rowIndex, Integer cellIndex, Boolean value);

	/**
	 * Escreve o conteúdo nulo de uma celula.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex Índice da célula
	 * @return Flag indicando se houve mudança real no conteúdo da célula.
	 */
	boolean writeNullValue(Integer rowIndex, Integer cellIndex);

	/**
	 * Recupera uma célula de uma linha a partir dos seus índices.
	 * 
	 * @param rowIndex Índice da linha
	 * @param cellIndex índice da célula
	 * @return Célula encontrada
	 */
	Cell getRowCell(Integer rowIndex, Integer cellIndex);

}