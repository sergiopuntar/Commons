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
import br.com.sgpf.common.domain.dataimport.impl.entity.UUIDEntitySimpleSheetDataSource;
import br.com.sgpf.common.domain.entity.AbstractUUIDEntityImpl;

public class UUIDEntitySimpleSheetDataSourceImpl extends UUIDEntitySimpleSheetDataSource<AbstractUUIDEntityImpl> {
	private static final long serialVersionUID = 1L;

	public UUIDEntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceDocumentException {
		super(file, sheetId);
	}

	public UUIDEntitySimpleSheetDataSourceImpl(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected AbstractUUIDEntityImpl createEntityInstance() {
		return new AbstractUUIDEntityImpl();
	}

	@Override
	protected boolean writeItemData(Integer rowIndex, AbstractUUIDEntityImpl data) {
		return false;
	}
}