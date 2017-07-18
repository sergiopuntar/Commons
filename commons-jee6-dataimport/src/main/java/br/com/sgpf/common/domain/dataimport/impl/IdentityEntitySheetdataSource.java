package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractIdentityEntity;

/**
 * Implementação base para todas as entidades identidade de uma fonte de dados baseada em planilha.
 *
 * @param <E> Tipo da entidade
 */
public abstract class IdentityEntitySheetdataSource<E extends AbstractIdentityEntity> extends EntitySheetDataSource<Long, E> {
	private static final long serialVersionUID = 8096959271007933357L;
	
	/**
	 * {@inheritDoc}
	 */
	public IdentityEntitySheetdataSource(File file, int sheetId) throws ImportDataSourceFileException {
		super(file, sheetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public IdentityEntitySheetdataSource(InputStream is, int sheetId) {
		super(is, sheetId);
	}

	@Override
	protected Long readEntityId(String columnName) {
		return readLongCell(columnName);
	}

	@Override
	protected void writeEntityId(Integer rowIndex, String columnName, Long id) {
		writeLongCell(rowIndex, columnName, id);
	}
}
