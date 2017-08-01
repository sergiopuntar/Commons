/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

/**
 * Classe que representa o resultado da importação de dados.
 * 
 * @author Sergio Puntar
 */
public class DataImportResult implements Serializable {
	private static final long serialVersionUID = 6106162065670933764L;

	public enum Status { 
		/**
		 * Os dados da origem foram importados no destino como um novo registro.
		 */
		INSERTED,
		/**
		 * Os dados da origem foram importados em um registro pré-existente no destino, pois os
		 * dados da origem eram mais recentes.
		 */		
		UPDATED,
		/**
		 * Os dados da origem foram importados em um registro pré-existente no destino, mesmo o
		 * detino contendo dados mais recentes.
		 */
		FORCE_UPDATED, 
		/**
		 * Os dados foram removidos do destino.
		 */
		DELETED,
		/**
		 * Os dados do item de importação foram substituídos pelos dados do destino, pois eram mais
		 * recentes. 
		 */
		OVERRIDDEN,
		/**
		 * Ocorreu um erro durante a importação dos dados.
		 */
		ERROR,
		/**
		 * O item foi ignorado pois não haviam instruções sobre o que fazer. 
		 */
		IGNORED
	}
	
	private Status status;
	private boolean synced = false;
	private String message;
	private Exception exception;
	
	public DataImportResult() {
		super();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isSynced() {
		return synced;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
