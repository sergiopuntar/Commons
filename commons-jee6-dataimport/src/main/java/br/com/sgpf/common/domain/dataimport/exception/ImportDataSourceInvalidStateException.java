package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de estado inválido em DataSource de importação.
 */
public class ImportDataSourceInvalidStateException extends ImportDataSourceException {
	private static final long serialVersionUID = 5140491473385527084L;

	public ImportDataSourceInvalidStateException(String message) {
		super(message);
	}

	public ImportDataSourceInvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
