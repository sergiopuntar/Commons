package br.com.sgpf.common.infra.exception;

/**
 * Exceção de erro para regras de negócio
 */
public class BusinessException extends SystemException {
	private static final long serialVersionUID = 1269659412146724424L;

	public BusinessException() {
		super();
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
