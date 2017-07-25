package br.com.sgpf.common.infra.exception;

/**
 * Exceção fatal de infraestrutura do sistema.
 */
public class InfraestructureFatalException extends RuntimeException {
	private static final long serialVersionUID = 1000896224430502360L;

	public InfraestructureFatalException(String message) {
		super(message);
	}

	public InfraestructureFatalException(String message, Throwable cause) {
		super(message, cause);
	}
}
