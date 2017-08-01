/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.exception;

/**
 * Exceção de erro para regras de negócio.
 * 
 * @author Sergio Puntar
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
