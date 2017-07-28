package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractIdentityEntityImpl;

public class IdentityEntitySimpleSheetDataSourceImpl extends IdentityEntitySimpleSheetDataSource<AbstractIdentityEntityImpl> {
	private static final long serialVersionUID = 1L;

	public IdentityEntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
		super(file, sheetId);
	}

	public IdentityEntitySimpleSheetDataSourceImpl(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected AbstractIdentityEntityImpl createEntityInstance() {
		return new AbstractIdentityEntityImpl();
	}

	@Override
	protected boolean writeItemData(Integer rowIndex, AbstractIdentityEntityImpl data) {
		return false;
	}
}