package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de leitura ou escrita em DataSource de importação.
 */
public class DataSourceIOException extends DataImportException {
	private static final long serialVersionUID = 2390875987041086184L;

	public DataSourceIOException(String message) {
		super(message);
	}

	public DataSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
