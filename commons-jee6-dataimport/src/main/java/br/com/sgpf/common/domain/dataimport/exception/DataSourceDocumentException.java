/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.exception;

/**
 * Exceção de erro no documento do DataSource de importação.
 */
public class DataSourceDocumentException extends DataImportException {
	private static final long serialVersionUID = -7280431806544070875L;

	public DataSourceDocumentException(String message) {
		super(message);
	}

	public DataSourceDocumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
