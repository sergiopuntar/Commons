package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de formato de dados em DataSource de importação.
 */
public class DataSourceFormatException extends DataImportException {
	private static final long serialVersionUID = -8220075413533232533L;

	public DataSourceFormatException(String message) {
		super(message);
	}

	public DataSourceFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
