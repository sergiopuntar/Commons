package br.com.sgpf.common.infra.exception;

/**
 * Exceção fatal básica dos sistemas.
 */
public class SystemFatalException extends RuntimeException {
	private static final long serialVersionUID = 1000896224430502360L;

	public SystemFatalException() {
		super();
	}

	public SystemFatalException(String message) {
		super(message);
	}

	public SystemFatalException(Throwable cause) {
		super(cause);
	}

	public SystemFatalException(String message, Throwable cause) {
		super(message, cause);
	}
}
