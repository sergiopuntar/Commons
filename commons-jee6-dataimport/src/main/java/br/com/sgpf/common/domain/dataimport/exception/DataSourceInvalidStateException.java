/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de estado inválido em DataSource de importação.
 */
public class DataSourceInvalidStateException extends DataImportException {
	private static final long serialVersionUID = 5140491473385527084L;

	public DataSourceInvalidStateException(String message) {
		super(message);
	}

	public DataSourceInvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
