package br.com.sgpf.common.domain.dataimport.impl;

import java.io.File;
import java.io.InputStream;

import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceFileException;
import br.com.sgpf.common.domain.entity.AbstractIdentityEntity;

/**
 * Implementação base para todas as entidades identidade de uma fonte de dados baseada em planilha Excel simples.
 *
 * @param <E> Tipo da entidade
 */
public abstract class IdentityEntitySimpleSheetDataSource<E extends AbstractIdentityEntity> extends EntitySimpleSheetDataSource<Long, E> {
	private static final long serialVersionUID = 8096959271007933357L;
	
	/**
	 * {@inheritDoc}
	 */
	public IdentityEntitySimpleSheetDataSource(File file, int sheetId) throws ImportDataSourceFileException {
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
	protected void writeEntityId(Integer rowIndex, String columnName, Long id) {
		writeLongCell(rowIndex, columnName, id);
	}
}
