package br.com.sgpf.common.infra.exception;

/**
 * Exceção de erro fatal para regras de negócio
 */
public class BusinessFatalException extends RuntimeException {
	private static final long serialVersionUID = 4113122929766666683L;

	public BusinessFatalException(String message) {
		super(message);
	}

	public BusinessFatalException(String message, Throwable cause) {
		super(message, cause);
	}
}
