package br.com.sgpf.common.domain.dataimport.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.DataImporter;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;

/**
 * Implementação base para os importadores de dados.
 * 
 * @param <I> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public abstract class BaseDataImporter<I extends Serializable, T extends Serializable> implements DataImporter<I, T> {
	private static final long serialVersionUID = 5124248593928945081L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataImporter.class);
	
	private static final String ERROR_NULL_DATASOURCE = "A fonte de dados não pode ser nula.";
	
	@Override
	public Collection<DataImportItem<I, T>> importData(ImportDataSource<I, T> dataSource, boolean sync) throws DataImportException {
		if (dataSource == null) {
			throw new IllegalArgumentException(ERROR_NULL_DATASOURCE);
		}
		
		LOGGER.info("Iniciando importação a partir da fonte de dados [{0}].", dataSource);
		List<DataImportItem<I, T>> itens = new ArrayList<>();
		
		dataSource.open();
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.warn("A sincronização com a origem foi ativada, porém os dados não serão sincronizados, pois a fonte de dados [{0}] não é gravável.", dataSource);
		}
		
		int inserted = 0, updated = 0, deleted = 0, skipped = 0, errors = 0, ignored = 0, synced = 0;
		
		while (dataSource.hasNext()) {
			DataImportItem<I, T> item = dataSource.next();
			
			importData(item);
			
			LOGGER.debug("Importação do item '{0}' processada, status final: {1}.", item.getId(), item.getResult().getStatus().name());
			
			switch (item.getResult().getStatus()) {
				case INSERTED:
					inserted++;
					break;
				case UPDATED:
					updated++;
					break;
				case FORCE_UPDATED:
					updated++;
					break;
				case DELETED:
					deleted++;
					break;
				case OVERRIDDEN:
					skipped++;
					break;
				case ERROR:
					errors++;
					LOGGER.warn(String.format("Ocorreu um erro ao importar o item '%s'.", item.getId()), item.getResult().getException());
					break;
				case IGNORED:
					ignored++;
					break;
			}
			
			if (sync && item.dataChanged() && item.isSync() && dataSource.isWritable()) {
				LOGGER.debug("Iniciando sincronização do item '{0}' com a origem.", item.getId());
				if (dataSource.sync(item)) {
					LOGGER.debug("Item '{0}' sincronizado com a origem.", item.getId());
				} else {
					LOGGER.debug("Origem já possui dados atualizados para o item '{0}.'", item.getId());					
				}
				item.getResult().setSynced(true);
				synced++;
			} else if (sync && item.dataChanged() && item.isSync() && !dataSource.isWritable()) {
				LOGGER.trace("Não foi possível sincronizar o item '{0}' pois a fonte de dados não é gravável.", item.getId());
			}
			
			itens.add(item);
		}
		
		LOGGER.info("Importação a partir da fonte de dados [{0}] finalizada.", dataSource);
		LOGGER.info("Total de itens processados: {0}", itens.size());
		LOGGER.info("Total de itens inseridos no destino: {0}", inserted);
		LOGGER.info("Total de itens atualizados no destino: {0}", updated);
		LOGGER.info("Total de itens removidos do destino: {0}", deleted);
		LOGGER.info("Total de itens pulados: {0}", skipped);
		LOGGER.info("Total de itens ignorados (sem instrução de importação): {0}", ignored);
		LOGGER.info("Total de erros de importação: ", errors);
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.info("Total de itens sincronizados com a origem: ", synced);
		}
		
		dataSource.close();
		
		return itens;
	}

	/**
	 * Realiza a importação dos dados de um item da origem no destino.
	 * 
	 * @param item Item a ser importado no destino
	 */
	protected abstract void importData(DataImportItem<I, T> item);
}
