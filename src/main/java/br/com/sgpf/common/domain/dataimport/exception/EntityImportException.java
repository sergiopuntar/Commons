package br.com.sgpf.common.domain.dataimport.exception;

import br.com.sgpf.common.infra.exception.SystemException;

/**
 * Exceção geral de erro de importação de entidade.
 */
public class EntityImportException extends SystemException {
	private static final long serialVersionUID = 875159910150397968L;

	public EntityImportException() {
		super();
	}

	public EntityImportException(String message) {
		super(message);
	}

	public EntityImportException(Throwable cause) {
		super(cause);
	}
	
	public EntityImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
