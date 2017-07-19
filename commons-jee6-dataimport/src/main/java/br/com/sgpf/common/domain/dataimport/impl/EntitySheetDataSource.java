package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Implementação base para todas as entidades de uma fonte de dados baseada em planilha.
 *
 * @param <E> Tipo da entidade
 */
public abstract class EntitySheetDataSource<ID extends Serializable, E extends Entity<ID>> extends BaseSheetDataSource<E> {
	private static final long serialVersionUID = 1711265271350804677L;
	
	private static enum EntityMetadataHeader {ID, CREATION_DATE, UPDATE_DATE, VERSION };

	/**
	 * {@inheritDoc}
	 */
	public EntitySheetDataSource(File file, int sheetId) throws ImportDataSourceFileException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EntitySheetDataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected E readCurrentItemData() {
		ID id = readEntityId(EntityMetadataHeader.ID.name());
		Date creationDate = readDateCell(EntityMetadataHeader.CREATION_DATE.name());
		Date updateDate = readDateCell(EntityMetadataHeader.UPDATE_DATE.name());
		Long version = readLongCell(EntityMetadataHeader.VERSION.name());
		
		return readCurrentItemData(id, creationDate, updateDate, version);
	}
	
	/**
	 * Lê o identificador da entidade na planilha.
	 * 
	 * @param columnName Nome da coluna onde está o identificador.
	 * @return Identificador da entidade
	 */
	protected abstract ID readEntityId(String columnName);
	
	/**
	 * Lê os dados restantes da entidade na panilha.<br>
	 * Os dados genéricos passados são incluídos na entidade.
	 * 
	 * @param id Identificador da entidade
	 * @param creationDate Data de criação da entidade
	 * @param updateDate Data de atualização da entidade
	 * @param version Versão da entidade
	 * @return Instância da entidade com os dados da planilha
	 */
	protected abstract E readCurrentItemData(ID id, Date creationDate, Date updateDate, Long version);

	@Override
	protected void syncRow(Integer rowIndex, E data) {
		writeEntityId(rowIndex, EntityMetadataHeader.ID.name(), data.getId());
		writeDateCell(rowIndex, EntityMetadataHeader.CREATION_DATE.name(), data.getDataCriacao());
		writeDateCell(rowIndex, EntityMetadataHeader.UPDATE_DATE.name(), data.getDataAtualizacao());
		writeLongCell(rowIndex, EntityMetadataHeader.VERSION.name(), data.getVersao());
		writeItemData(rowIndex, data);
	}
	
	/**
	 * Escreve o identificador da entidade em uma linha da planilha.
	 * 
	 * @param rowIndex Índice da linha
	 * @param columnName Nome da coluna onde está o identificador.
	 * @param id Identificador da entidade
	 */
	protected abstract void writeEntityId(Integer rowIndex, String columnName, ID id);
	
	/**
	 * Escreve os dados de uma entidade em uma linha da planilha.
	 * 
	 * @param rowIndex Índice da linha
	 * @param data Dados da entidade
	 */
	protected abstract void writeItemData(Integer rowIndex, E data);
}
