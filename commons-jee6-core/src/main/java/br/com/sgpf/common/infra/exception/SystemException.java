package br.com.sgpf.common.infra.exception;

/**
 * Exceção básica dos sistemas.
 */
public class SystemException extends Exception {
	private static final long serialVersionUID = 1417205324573148696L;

	public SystemException(String message) {
		super(message);
	}

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}
}
