/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.exception;

/**
 * Exceção fatal de infraestrutura do sistema.
 * 
 * @author Sergio Puntar
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
