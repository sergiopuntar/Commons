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
import br.com.sgpf.common.domain.entity.AbstractUUIDEntity;

/**
 * Implementação base para todas as entidades UUID de uma fonte de dados baseada em planilha Excel simples.
 *
 * @param <E> Tipo da entidade
 * 
 * @author Sergio Puntar
 */
public abstract class UUIDEntitySimpleSheetDataSource<E extends AbstractUUIDEntity> extends EntitySimpleSheetDataSource<String, E> {
	private static final long serialVersionUID = 8096959271007933357L;
	
	/**
	 * {@inheritDoc}
	 */
	public UUIDEntitySimpleSheetDataSource(File file, int sheetId) throws DataSourceDocumentException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public UUIDEntitySimpleSheetDataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected String readEntityId(String columnName) {
		return readStringCell(columnName);
	}

	@Override
	protected boolean writeEntityId(Integer rowIndex, String columnName, String id) {
		return writeStringCell(rowIndex, columnName, id);
	}
}
