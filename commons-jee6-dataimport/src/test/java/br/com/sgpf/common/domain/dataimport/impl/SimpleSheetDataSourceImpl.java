package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.DataSourceFileException;
import br.com.sgpf.common.domain.vo.SimpleDataElement;

public class SimpleSheetDataSourceImpl extends SimpleSheetDataSource<SimpleDataElement> {
	private static final long serialVersionUID = 1L;
	
	private boolean changedRow = false;

	public SimpleSheetDataSourceImpl(File file, int sheetId) throws DataSourceFileException {
		super(file, sheetId);
	}

	public SimpleSheetDataSourceImpl(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	public void setChangedRow(boolean changedRow) {
		this.changedRow = changedRow;
	}

	@Override
	protected SimpleDataElement readCurrentItemData() {
		return new SimpleDataElement(readLongCell("ID"));
	}

	@Override
	protected boolean syncRow(Integer rowIndex, SimpleDataElement data) {
		return changedRow;
	}
}