package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.com.sgpf.common.util.CanEqual;

/**
 * Classe que representa o resultado da importação de dados.
 */
public class DataImportResult implements Serializable, CanEqual {
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
				.append(this.getStatus())
				.append(this.isSynced())
				.append(this.getMessage())
				.append(this.getException()).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataImportResult) {
			DataImportResult that = (DataImportResult) obj;
			return that.canEqual(this) && new EqualsBuilder()
					.append(this.getStatus(), that.getStatus())
					.append(this.isSynced(), that.isSynced())
					.append(this.getMessage(), that.getMessage())
					.append(this.getException(), that.getException()).isEquals();
		}

		return false;
	}
	
	/**
	 * Indica se um determinado objeto pode ser utilizado para comparação
	 * com este.
	 * 
	 * @param obj Objeto que se deseja comparar
	 * @return Flag indicando se o objeto pode ser comparado a este
	 */
	public boolean canEqual(Object obj) {
		return obj instanceof DataImportResult;
	}
}
