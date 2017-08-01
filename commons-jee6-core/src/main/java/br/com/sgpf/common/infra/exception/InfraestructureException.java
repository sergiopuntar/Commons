/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.exception;

/**
 * Exceção de infraestrutura do sistema.
 * 
 * @author Sergio Puntar
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
