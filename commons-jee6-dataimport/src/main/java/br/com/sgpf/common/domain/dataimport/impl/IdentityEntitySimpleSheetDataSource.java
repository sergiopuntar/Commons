/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.entity.AbstractIdentityEntity;

/**
 * Implementação base para todas as entidades identidade de uma fonte de dados baseada em planilha Excel simples.
 *
 * @param <E> Tipo da entidade
 * 
 * @author Sergio Puntar
 */
public abstract class IdentityEntitySimpleSheetDataSource<E extends AbstractIdentityEntity> extends EntitySimpleSheetDataSource<Long, E> {
	private static final long serialVersionUID = 8096959271007933357L;
	
	/**
	 * {@inheritDoc}
	 */
	public IdentityEntitySimpleSheetDataSource(File file, int sheetId) throws DataSourceDocumentException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public IdentityEntitySimpleSheetDataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected Long readEntityId(String columnName) {
		return readLongCell(columnName);
	}

	@Override
	protected boolean writeEntityId(Integer rowIndex, String columnName, Long id) {
		return writeLongCell(rowIndex, columnName, id);
	}
}
