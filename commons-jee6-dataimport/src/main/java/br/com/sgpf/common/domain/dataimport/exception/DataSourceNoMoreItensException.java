/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro de DataSource de importação sem mais itens.
 * 
 * @author Sergio Puntar
 */
public class DataSourceNoMoreItensException extends DataImportException {
	private static final long serialVersionUID = 8839531543825966534L;

	public DataSourceNoMoreItensException(String message) {
		super(message);
	}

	public DataSourceNoMoreItensException(String message, Throwable cause) {
		super(message, cause);
	}
}
