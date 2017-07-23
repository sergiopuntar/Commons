package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro no documento do DataSource de importação.
 */
public class ImportDataSourceDocumentException extends ImportDataSourceException {
	private static final long serialVersionUID = -7280431806544070875L;

	public ImportDataSourceDocumentException(String message) {
		super(message);
	}

	public ImportDataSourceDocumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
