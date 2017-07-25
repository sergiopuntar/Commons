package br.com.sgpf.common.infra.exception;

/**
 * Exceção de erro para regras de negócio
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = 1269659412146724424L;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
