package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Classe que representa o resultado da importação de dados.
 */
public class DataImportResult implements Serializable {
	private static final long serialVersionUID = 6106162065670933764L;

	public static enum Status { 
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
		 * Os dados da origem foram importados em um registro pré-existente no destino, mesmo que o
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
	};
	
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 11)
				.append(getStatus())
				.append(isSynced())
				.append(getMessage())
				.append(getException()).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataImportResult) {
			DataImportResult eir = (DataImportResult) obj;
			return new EqualsBuilder()
					.append(getStatus(), eir.getStatus())
					.append(isSynced(), eir.isSynced())
					.append(getMessage(), eir.getMessage())
					.append(getException(), eir.getException()).isEquals();
		}

		return false;
	}
}
