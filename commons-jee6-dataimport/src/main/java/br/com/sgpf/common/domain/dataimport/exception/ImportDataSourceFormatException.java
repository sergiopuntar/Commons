package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de formato de dados em DataSource de importação.
 */
public class ImportDataSourceFormatException extends ImportDataSourceException {
	private static final long serialVersionUID = -8220075413533232533L;

	public ImportDataSourceFormatException() {
		super();
	}

	public ImportDataSourceFormatException(String message) {
		super(message);
	}

	public ImportDataSourceFormatException(Throwable cause) {
		super(cause);
	}
	
	public ImportDataSourceFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
