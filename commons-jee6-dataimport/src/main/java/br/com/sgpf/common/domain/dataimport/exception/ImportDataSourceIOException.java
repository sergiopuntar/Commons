package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de leitura ou escrita em DataSource de importação.
 */
public class ImportDataSourceIOException extends ImportDataSourceException {
	private static final long serialVersionUID = 2390875987041086184L;

	public ImportDataSourceIOException(String message) {
		super(message);
	}

	public ImportDataSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
