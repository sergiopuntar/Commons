/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção geral de erro de DataSource de importação.
 */
public class DataImportException extends Exception {
	private static final long serialVersionUID = -6357098074559278302L;

	public DataImportException(String message) {
		super(message);
	}

	public DataImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
