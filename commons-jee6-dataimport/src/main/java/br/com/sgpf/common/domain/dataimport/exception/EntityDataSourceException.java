package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção geral de erro de DataSource de entidade.
 */
public class EntityDataSourceException extends EntityImportException {
	private static final long serialVersionUID = -6357098074559278302L;

	public EntityDataSourceException() {
		super();
	}

	public EntityDataSourceException(String message) {
		super(message);
	}

	public EntityDataSourceException(Throwable cause) {
		super(cause);
	}
	
	public EntityDataSourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
