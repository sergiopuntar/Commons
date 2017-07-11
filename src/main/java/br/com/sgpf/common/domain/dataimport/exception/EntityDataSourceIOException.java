package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de leitura ou escrita em DataSource de entidade.
 */
public class EntityDataSourceIOException extends EntityDataSourceException {
	private static final long serialVersionUID = 2390875987041086184L;

	public EntityDataSourceIOException() {
		super();
	}

	public EntityDataSourceIOException(String message) {
		super(message);
	}

	public EntityDataSourceIOException(Throwable cause) {
		super(cause);
	}
	
	public EntityDataSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
