package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de DataSource de importação sem mais itens.
 */
public class DataSourceNoMoreItensException extends DataImportException {
	private static final long serialVersionUID = 8839531543825966534L;

	public DataSourceNoMoreItensException(String message) {
		super(message);
	}

	public DataSourceNoMoreItensException(String message, Throwable cause) {
		super(message, cause);
	}
}
