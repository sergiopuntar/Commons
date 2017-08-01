/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.exception;

/**
 * Exceção erro de formato em Documentos.
 * 
 * @author Sergio Puntar
 */
public class DocumentFormatException extends DocumentException {
	private static final long serialVersionUID = -1870228793780696187L;

	public DocumentFormatException(String message) {
		super(message);
	}

	public DocumentFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
