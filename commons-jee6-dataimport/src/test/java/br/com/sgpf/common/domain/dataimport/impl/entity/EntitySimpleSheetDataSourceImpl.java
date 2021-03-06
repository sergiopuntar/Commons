/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl.entity;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceDocumentException;
import br.com.sgpf.common.domain.dataimport.impl.entity.EntitySimpleSheetDataSource;
import br.com.sgpf.common.domain.entity.AbstractEntityImpl;

public class EntitySimpleSheetDataSourceImpl extends EntitySimpleSheetDataSource<Long, AbstractEntityImpl> {
	private static final long serialVersionUID = 1L;

	public EntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceDocumentException {
		super(file, sheetId);
	}

	public EntitySimpleSheetDataSourceImpl(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected AbstractEntityImpl createEntityInstance() {
		return new AbstractEntityImpl();
	}
	
	@Override
	protected Long readEntityId(String columnName) {
		return readLongCell(columnName);
	}

	@Override
	protected boolean writeEntityId(Integer rowIndex, String columnName, Long id) {
		return writeLongCell(rowIndex, columnName, id);
	}

	@Override
	protected boolean writeItemData(Integer rowIndex, AbstractEntityImpl data) {
		return false;
	}

}