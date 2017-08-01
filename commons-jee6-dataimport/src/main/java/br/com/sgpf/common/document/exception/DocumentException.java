/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.document.exception;

/**
 * Exceção geral de erro de Documentos.
 * 
 * @author Sergio Puntar
 */
public class DocumentException extends Exception {
	private static final long serialVersionUID = -1333434388409237352L;
	
	public DocumentException(String message) {
		super(message);
	}

	public DocumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
