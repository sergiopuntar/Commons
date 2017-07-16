package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro no arquivo do DataSource de importação.
 */
public class ImportDataSourceFileException extends ImportDataSourceException {
	private static final long serialVersionUID = -8969153256862227009L;

	public ImportDataSourceFileException() {
		super();
	}

	public ImportDataSourceFileException(String message) {
		super(message);
	}

	public ImportDataSourceFileException(Throwable cause) {
		super(cause);
	}
	
	public ImportDataSourceFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
