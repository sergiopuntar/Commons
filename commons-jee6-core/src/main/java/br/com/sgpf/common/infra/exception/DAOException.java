package br.com.sgpf.common.infra.exception;

/**
 * Exceptions relacionadas a camada de acesso ao banco de dados.
 */
public class DAOException extends SystemFatalException {
	private static final long serialVersionUID = -1555104124339514398L;

	public DAOException() {
		super();
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(Throwable cause) {
		super(cause);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}
}
