package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractUUIDEntityImpl;

public class UUIDEntitySimpleSheetDataSourceImpl extends UUIDEntitySimpleSheetDataSource<AbstractUUIDEntityImpl> {
	private static final long serialVersionUID = 1L;

	public UUIDEntitySimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
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