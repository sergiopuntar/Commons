package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro no arquivo do DataSource de importação.
 */
public class DataSourceFileException extends DataImportException {
	private static final long serialVersionUID = -8969153256862227009L;

	public DataSourceFileException(String message) {
		super(message);
	}

	public DataSourceFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
