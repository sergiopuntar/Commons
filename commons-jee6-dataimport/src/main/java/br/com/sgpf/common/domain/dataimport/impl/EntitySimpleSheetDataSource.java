/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Implementação base para todas as entidades de uma fonte de dados baseada em planilha Excel simples.
 *
 * @param <E> Tipo da entidade
 */
public abstract class EntitySimpleSheetDataSource<I extends Serializable, E extends Entity<I>> extends SimpleSheetDataSource<E> {
	private static final long serialVersionUID = 1711265271350804677L;
	
	private enum EntityMetadataHeader { ID, CREATION_DATE, UPDATE_DATE, VERSION }

	/**
	 * {@inheritDoc}
	 */
	public EntitySimpleSheetDataSource(File file, int sheetId) throws DataSourceFileException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EntitySimpleSheetDataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected E readCurrentItemData() {
		E entity = createEntityInstance();
		entity.setId(readEntityId(EntityMetadataHeader.ID.name()));
		entity.setCreationDate(readDateCell(EntityMetadataHeader.CREATION_DATE.name()));
		entity.setUpdateDate(readDateCell(EntityMetadataHeader.UPDATE_DATE.name()));
		entity.setVersion(readLongCell(EntityMetadataHeader.VERSION.name()));
		
		return entity;
	}
	
	/**
	 * Cria uma nova instância vazia da entidade. 
	 */
	protected abstract E createEntityInstance();
	
	/**
	 * Lê o identificador da entidade na planilha.
	 * 
	 * @param columnName Nome da coluna onde está o identificador.
	 * @return Identificador da entidade
	 */
	protected abstract I readEntityId(String columnName);

	@Override
	protected boolean syncRow(Integer rowIndex, E data) {
		boolean idChanged = writeEntityId(rowIndex, EntityMetadataHeader.ID.name(), data.getId());
		boolean creationDateChanged = writeDateCell(rowIndex, EntityMetadataHeader.CREATION_DATE.name(), data.getCreationDate());
		boolean updateDateChanged = writeDateCell(rowIndex, EntityMetadataHeader.UPDATE_DATE.name(), data.getUpdateDate());
		boolean versionChanged = writeLongCell(rowIndex, EntityMetadataHeader.VERSION.name(), data.getVersion());
		boolean itemChanged = writeItemData(rowIndex, data);
		
		return idChanged || creationDateChanged || updateDateChanged || versionChanged || itemChanged;
	}
	
	/**
	 * Escreve o identificador da entidade em uma linha da planilha.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna onde está o identificador.
	 * @param id Identificador da entidade
	 * @return Flag indicando se houve mudança real no conteúdo da planilha
	 */
	protected abstract boolean writeEntityId(Integer rowIndex, String columnName, I id);
	
	/**
	 * Escreve os dados de uma entidade em uma linha da planilha.
	 * 
	 * @param rowIndex Índice da linha
	 * @param data Dados da entidade
	 * @return Flag indicando se houve mudança real no conteúdo da planilha
	 */
	protected abstract boolean writeItemData(Integer rowIndex, E data);
}
