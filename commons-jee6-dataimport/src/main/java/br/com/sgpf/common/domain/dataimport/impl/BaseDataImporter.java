/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.DataImportResult.Status;
import br.com.sgpf.common.domain.dataimport.DataImporter;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;

/**
 * Implementação base para os importadores de dados.
 * 
 * @param <I> Identificador do item de importação
 * @param <T> Tipo do dado
 * 
 * @author Sergio Puntar
 */
public abstract class BaseDataImporter<I extends Serializable, T extends Serializable> implements DataImporter<I, T> {
	private static final long serialVersionUID = 5124248593928945081L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataImporter.class);
	
	private static final String ERROR_READING_ITEM = "Ocorreu um erro ao ler um item para importação da origem.";
	private static final String ERROR_SYNCHRONIZING_ITEM = "ocorreu um erro ao sincronizar um item importado com a origem.";

	private static final String ARG_NAME_DATA_SOURCE = "dataSource";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<DataImportItem<I, T>> importData(ImportDataSource<I, T> dataSource, boolean sync, boolean suppressExceptions) throws DataImportException {
		checkNotNull(dataSource, ERROR_NULL_ARGUMENT, ARG_NAME_DATA_SOURCE);
		
		LOGGER.info("Iniciando importação a partir da fonte de dados [{0}].", dataSource);
		List<DataImportItem<I, T>> itens = new ArrayList<>();
		
		dataSource.open();
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.warn("A sincronização com a origem foi ativada, porém os dados não serão sincronizados, pois a fonte de dados [{0}] não é gravável.", dataSource);
		}
		
		int synced = 0;
		
		while (dataSource.hasNext()) {
			DataImportItem<I, T> item;
			
			try {
				item = dataSource.next();
			} catch (DataImportException e) {
				if (!suppressExceptions) {
					throw e;
				}
				
				LOGGER.warn("Ocorreu um erro na leitura de um item para importação. Supressão de exceções ativada, importação dos itens restantes proseguirá.", e);
				itens.add(new DataImportItem<I, T>(ERROR_READING_ITEM, e));
				
				if (dataSource.hasNext()) {
					continue;
				} else {
					break;
				}
			}
			
			processItem(item, suppressExceptions);
			
			LOGGER.debug("Status final da importação do item '{0}': {1}.", item.getId(), item.getResult().getStatus());
			
			if (sync && item.dataChanged() && item.isSync() && dataSource.isWritable()) {
				synced = syncData(dataSource, item, suppressExceptions) ? synced + 1 : synced;
			} else if (sync && item.dataChanged() && item.isSync() && !dataSource.isWritable()) {
				LOGGER.trace("Não foi possível sincronizar o item '{0}' pois a fonte de dados não é gravável.", item.getId());
			}
			
			itens.add(item);
		}
		
		LOGGER.info("Importação a partir da fonte de dados [{0}] finalizada.", dataSource);
		LOGGER.info("Total de itens processados: {0}", itens.size());
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.info("Total de itens sincronizados com a origem: {0}", synced);
		}
		
		dataSource.close();
		
		return itens;
	}

	/**
	 * Realiza a importação dos dados de um item da origem no destino.
	 * 
	 * @param item Item a ser importado no destino
	 * @param suppressExceptions Flag que indica se as exceções devem ser suprimidas ou não
	 * @throws DataImportException Se ocorre um erro durante a gravação dos dados no destino
	 */
	protected abstract void processItem(DataImportItem<I, T> item, boolean suppressExceptions) throws DataImportException;

	/**
	 * Sincroniza um item importado no destino com a origem.
	 * 
	 * @param dataSource Data source de origem dos dados
	 * @param item Item a ser sincronizado
	 * @param suppressExceptions Flag que indica se as exceções devem ser suprimidas ou não
	 * @throws DataImportException Se ocorre um erro durante a gravação dos dados na origem
	 */
	private boolean syncData(ImportDataSource<I, T> dataSource, DataImportItem<I, T> item, boolean suppressExceptions) throws DataImportException {
		LOGGER.debug("Iniciando sincronização do item '{0}' com a origem.", item.getId());
		
		try {
			if (dataSource.sync(item)) {
				LOGGER.debug("Item '{0}' sincronizado com a origem.", item.getId());
			} else {
				LOGGER.debug("Origem já possui dados atualizados para o item '{0}.'", item.getId());					
			}
			
			item.getResult().setSynced(true);
		} catch (DataImportException e) {
			if (!suppressExceptions) {
				throw e;
			}
			
			LOGGER.warn("Ocorreu um erro ao sincronizar o item {0} com a origem.", item.getId(), e);
			item.getResult().setStatus(Status.ERROR);
			item.getResult().setMessage(ERROR_SYNCHRONIZING_ITEM);
			item.getResult().setException(e);
			
			return false;
		}
		
		return true;
	}
}
