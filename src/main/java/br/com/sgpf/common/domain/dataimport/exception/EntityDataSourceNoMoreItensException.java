package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de DataSource de entidade sem mais itens.
 */
public class EntityDataSourceNoMoreItensException extends EntityDataSourceException {
	private static final long serialVersionUID = 8839531543825966534L;

	public EntityDataSourceNoMoreItensException() {
		super();
	}

	public EntityDataSourceNoMoreItensException(String message) {
		super(message);
	}

	public EntityDataSourceNoMoreItensException(Throwable cause) {
		super(cause);
	}
	
	public EntityDataSourceNoMoreItensException(String message, Throwable cause) {
		super(message, cause);
	}
}
