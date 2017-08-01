/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de leitura ou escrita em DataSource de importação.
 * 
 * @author Sergio Puntar
 */
public class DataSourceIOException extends DataImportException {
	private static final long serialVersionUID = 2390875987041086184L;

	public DataSourceIOException(String message) {
		super(message);
	}

	public DataSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
