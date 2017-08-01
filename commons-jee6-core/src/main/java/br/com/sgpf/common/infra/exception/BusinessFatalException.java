/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
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
