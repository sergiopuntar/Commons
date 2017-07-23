package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de DataSource de importação sem mais itens.
 */
public class ImportDataSourceNoMoreItensException extends ImportDataSourceException {
	private static final long serialVersionUID = 8839531543825966534L;

	public ImportDataSourceNoMoreItensException(String message) {
		super(message);
	}

	public ImportDataSourceNoMoreItensException(String message, Throwable cause) {
		super(message, cause);
	}
}
