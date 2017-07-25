package br.com.sgpf.common.infra.exception;

/**
 * Exceção de infraestrutura do sistema.
 */
public class InfraestructureException extends Exception {
	private static final long serialVersionUID = 1417205324573148696L;

	public InfraestructureException(String message) {
		super(message);
	}

	public InfraestructureException(String message, Throwable cause) {
		super(message, cause);
	}
}
