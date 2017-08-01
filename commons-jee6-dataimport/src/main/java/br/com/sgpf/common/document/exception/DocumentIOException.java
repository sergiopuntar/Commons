/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.exception;

/**
 * Exceção erro de leitura e escrita em Documentos.
 * 
 * @author Sergio Puntar
 */
public class DocumentIOException extends DocumentException {
	private static final long serialVersionUID = -8307638187655651994L;

	public DocumentIOException(String message) {
		super(message);
	}

	public DocumentIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
