package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.InvalidParameterException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceIOException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceInvalidStateException;
import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceNoMoreItensException;

/**
 * Implementação base da fonte de dados em planilha Excel simples.
 *
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public abstract class BaseSheetDataSource<ID extends Serializable, T extends Serializable> implements ImportDataSource<ID, T> {
	private static final long serialVersionUID = -7387063988593887736L;
	
	private static final String ERROR_NULL_FILE = "O arquivo não pode ser nulo.";
	private static final String ERROR_NULL_IS = "O input stream não pode ser nulo.";
	
	private static final String ERROR_UNDEFINED_TYPE = "O tipo da fonte de dados não foi definido.";
	private static final String ERROR_DOCUMENT_CLOSED = "O documento está fechado.";
	private static final String ERROR_DOCUMENT_OPEN = "O documento está aberto.";

	private static final String ERROR_READING_DOCUMENT = "Ocorreu um erro na leitura do documento.";
	private static final String ERROR_ENCRYPTED_DOCUMENT = "O documento está criptografado.";
	private static final String ERROR_INVALID_DOCUMENT_FORMAT = "O documento possui um formato inválido.";
	private static final String ERROR_NON_READABLE_FILE = "O arquivo [%s] não pode ser lido.";
	private static final String ERROR_NON_EXISTING_SHEET = "O documento não possui planilha com índice [%d].";
	private static final String ERROR_FILE_NOT_FOUND = "Não foi possível encontrar o arquivo [%s].";
	private static final String ERROR_WRITING_CHANGES = "Não foi possível gravar as alterações no documento.";
	private static final String ERROR_CLOSING_DOCUMENT = "Ocorreu um erro ao fechar o documento.";


	private static enum Type { FILE, INPUT_STREAM };
	
	private File file;
	private InputStream is;
	private Type type;
	private int sheetId;
	
	private Workbook workbook;
	private Sheet sheet;
	private int currRow;
	private boolean changed;
	
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
	 */
	public BaseSheetDataSource(File file, int sheetId) {
		this(Type.FILE, sheetId);
		
		if (file == null) {
			throw new InvalidParameterException(ERROR_NULL_FILE);
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
		
		if (file == null) {
			throw new InvalidParameterException(ERROR_NULL_IS);
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
		
		sheet = workbook.getSheetAt(sheetId);
		
		if (sheet == null) {
			throw new ImportDataSourceDocumentException(String.format(ERROR_NON_EXISTING_SHEET, sheetId));
		}
		
		changed = false;
		currRow = -1;
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
			return WorkbookFactory.create(file);
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
	public DataImportItem<ID, T> next() throws ImportDataSourceNoMoreItensException, ImportDataSourceException {
		validadeIsOpen();
		
		if (!hasNext()) {
			throw new ImportDataSourceNoMoreItensException();
		}
		
		currRow++;
		
		//TODO ler dados
		return null;
	}

	@Override
	public void sync(DataImportItem<ID, T> item) throws ImportDataSourceException {
		validadeIsOpen();
		//TODO escrever dados
	}

	@Override
	public void close() throws ImportDataSourceException {
		validadeIsOpen();
		
		try {
			if (changed && isWritable()) {
				flush();
			}
		} catch (FileNotFoundException e) {
			throw new ImportDataSourceDocumentException(String.format(ERROR_FILE_NOT_FOUND, file.getAbsolutePath()), e);
		} catch (IOException e) {
			throw new ImportDataSourceIOException(ERROR_WRITING_CHANGES, e);
		} finally {
			try {
				workbook.close();
				workbook = null;
				sheet = null;
				changed = false;
				currRow = -1;
			} catch (IOException e) {
				throw new ImportDataSourceIOException(ERROR_CLOSING_DOCUMENT, e);
			}
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
		
		workbook.write(new FileOutputStream(file));
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
