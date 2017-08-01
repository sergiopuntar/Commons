/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de formato de dados em DataSource de importação.
 * 
 * @author Sergio Puntar
 */
public class DataSourceFormatException extends DataImportException {
	private static final long serialVersionUID = -8220075413533232533L;

	public DataSourceFormatException(String message) {
		super(message);
	}

	public DataSourceFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
