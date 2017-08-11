/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl.entity;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.dataimport.impl.BaseDataImporter;
import br.com.sgpf.common.domain.entity.Entity;
import br.com.sgpf.common.domain.repository.Repository;

/**
 * 
 * @author Sergio Puntar
 *
 * @param <I> Identificador do item de importação
 * @param <E> Tipo de entidade importada
 * @param <T> Identificador da entidade importada
 */
public class EntityDataImporter<I extends Serializable, E extends Entity<T>, T extends Serializable> extends BaseDataImporter<I, E> {
	private static final long serialVersionUID = 5448744052089563944L;
	
	private Repository<E, T> entityRepository;

	public EntityDataImporter(Repository<E, T> entityRepository) {
		super();
		this.entityRepository = entityRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processItem(DataImportItem<I, E> item, boolean suppressExceptions) throws DataImportException {
		
	}
}
