package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção geral de erro de DataSource de importação.
 */
public class ImportDataSourceException extends DataImportException {
	private static final long serialVersionUID = -6357098074559278302L;

	public ImportDataSourceException(String message) {
		super(message);
	}

	public ImportDataSourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
