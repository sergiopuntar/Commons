/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.exception;

/**
 * Exceção erro de arquivo de Documentos.
 * 
 * @author Sergio Puntar
 */
public class DocumentFileException extends DocumentException {
	private static final long serialVersionUID = -1945606075191594866L;

	public DocumentFileException(String message) {
		super(message);
	}

	public DocumentFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
