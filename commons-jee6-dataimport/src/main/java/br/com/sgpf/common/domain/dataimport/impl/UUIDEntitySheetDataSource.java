package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractUUIDEntity;

/**
 * Implementação base para todas as entidades UUID de uma fonte de dados baseada em planilha.
 *
 * @param <E> Tipo da entidade
 */
public abstract class UUIDEntitySheetDataSource<E extends AbstractUUIDEntity> extends EntitySheetDataSource<String, E> {
	private static final long serialVersionUID = 8096959271007933357L;
	
	/**
	 * {@inheritDoc}
	 */
	public UUIDEntitySheetDataSource(File file, int sheetId) throws ImportDataSourceFileException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public UUIDEntitySheetDataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected String readEntityId(String columnName) {
		return readStringCell(columnName);
	}

	@Override
	protected void writeEntityId(Integer rowIndex, String columnName, String id) {
		writeStringCell(rowIndex, columnName, id);
	}
}
