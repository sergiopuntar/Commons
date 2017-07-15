package br.com.sgpf.common.domain.dataimport.exception;

import br.com.sgpf.common.infra.exception.SystemException;

/**
 * Exceção geral de erro de importação de dados.
 */
public class DataImportException extends SystemException {
	private static final long serialVersionUID = 875159910150397968L;

	public DataImportException() {
		super();
	}

	public DataImportException(String message) {
		super(message);
	}

	public DataImportException(Throwable cause) {
		super(cause);
	}
	
	public DataImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
