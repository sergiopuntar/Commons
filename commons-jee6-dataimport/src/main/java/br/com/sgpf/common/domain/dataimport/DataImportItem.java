/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.DELETED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.FORCE_UPDATED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.INSERTED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.OVERRIDDEN;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.UPDATED;
import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import br.com.sgpf.common.util.CanEqual;

/**
 * Classe Wrapper de dados utilizada no processo de importação.
 *
 * @param <I> Identificador o item de importação
 * @param <T> Tipo do dado
 * 
 * @author Sergio Puntar
 */
public class DataImportItem<I extends Serializable, T extends Serializable> implements Serializable, CanEqual {
	private static final long serialVersionUID = -4993453059086257679L;

	private static final String ARG_NAME_ID = "id";
	private static final String ARG_NAME_DATA = "data";
	private static final String ARG_NAME_INSTRUCTIONS = "instructions";
	
	private static final List<DataImportResult.Status> CHANGED_DATA_STATUS = Lists.newArrayList(INSERTED, UPDATED, FORCE_UPDATED, DELETED, OVERRIDDEN);
	
	private I id;
	private T data;
	private DataImportInstructions instructions;
	private DataImportResult result;
	
	/**
	 * Constrói o item de importação com os dados recuperados da origem.
	 * 
	 * @param id Identificador do item na origem
	 * @param data Dados do item na origem
	 * @param instructions Instruções de importação do item na origem
	 */
	public DataImportItem(I id, T data, DataImportInstructions instructions) {
		super();
		this.id = checkNotNull(id, ERROR_NULL_ARGUMENT, ARG_NAME_ID);
		this.data = checkNotNull(data, ERROR_NULL_ARGUMENT, ARG_NAME_DATA);
		this.instructions = checkNotNull(instructions, ERROR_NULL_ARGUMENT, ARG_NAME_INSTRUCTIONS);
		this.result = new DataImportResult();
	}
	
	/**
	 * Constrói um item de importação sem dados, contendo um resultado de erro de importação.
	 * 
	 * @param errorMessage Mensagem de erro de importação
	 * @param exception Exceção que gerou o erro
	 */
	public DataImportItem(String errorMessage, Exception exception) {
		super();
		this.result = new DataImportResult(errorMessage, exception);
	}

	public I getId() {
		return id;
	}

	public T getData() {
		return data;
	}

	public Boolean isInsert() {
		return instructions != null && instructions.isInsert();
	}

	public Boolean isUpdate() {
		return instructions != null && instructions.isUpdate();
	}

	public Boolean isMerge() {
		return instructions != null && instructions.isMerge();
	}

	public Boolean isRemove() {
		return instructions != null && instructions.isRemove();
	}

	public Boolean isForce() {
		return instructions != null && instructions.isForce();
	}

	public Boolean isSync() {
		return instructions != null && instructions.isSync();
	}

	public DataImportResult getResult() {
		return result;
	}
	
	/**
	 * Verifica se os dados contidos no item foram alterados (no destino ou na origem) após a
	 * importação.
	 * 
	 * @return True se houve alteração, False caso contrário 
	 */
	public Boolean dataChanged() {
		return CHANGED_DATA_STATUS.contains(getResult().getStatus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataImportItem) {
			DataImportItem<?, ?> that = (DataImportItem<?, ?>) obj;
			return that.canEqual(this) &&
					Objects.equals(this.getId(), that.getId());
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof DataImportItem;
	}
}
