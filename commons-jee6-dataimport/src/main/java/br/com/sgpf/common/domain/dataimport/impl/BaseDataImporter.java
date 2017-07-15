package br.com.sgpf.common.domain.dataimport.impl;

import java.io.Serializable;
import java.security.InvalidParameterException;
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
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public abstract class BaseDataImporter<ID extends Serializable, T extends Serializable> implements DataImporter<ID, T> {
	private static final long serialVersionUID = 5124248593928945081L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataImporter.class);
	
	private static final String ERROR_NULL_DATASOURCE = "A fonte de dados não pode ser nula.";
	
	@Override
	public Collection<DataImportItem<ID, T>> importData(ImportDataSource<ID, T> dataSource, boolean sync) throws DataImportException {
		if (dataSource == null) {
			throw new InvalidParameterException(ERROR_NULL_DATASOURCE);
		}
		
		LOGGER.info("Iniciando importação a partir da fonte de dados [{0}].", dataSource);
		List<DataImportItem<ID, T>> itens = new ArrayList<DataImportItem<ID, T>>();
		
		dataSource.open();
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.warn("A sincronização com a origem foi ativada, porém os dados não serão sincronizados, pois a fonte de dados [{0}] não é gravável.", dataSource);
		}
		
		int synced = 0;
		
		while (dataSource.hasNext()) {
			DataImportItem<ID, T> item = importEntity(dataSource.next());
			LOGGER.debug("Dados com identificador '{0}' importados.", item.getId());
			
			if (sync && item.dataChanged() && item.isSync() && dataSource.isWritable()) {
				dataSource.sync(item);
				item.getResult().setSynced(true);
				synced++;
				LOGGER.debug("Dados com identificador '{0}' sincronizados com a origem.", item.getId());
			} else if (sync && item.dataChanged() && item.isSync() && !dataSource.isWritable()) {
				LOGGER.debug("Não foi possível sincronizar os dados com identificador '{0}' pois a fonte de dados não é gravável.", item.getId());
			}
			
			itens.add(item);
		}
		
		LOGGER.info("Importação a partir da fonte de dados [{0}] finalizada.", dataSource);
		LOGGER.info("Total de itens importados: ", itens.size());
		
		if (sync && !dataSource.isWritable()) {
			LOGGER.info("Total de itens sincronizados com a origem: ", synced);
		}
		
		dataSource.close();
		
		return itens;
	}
	
	/**
	 * Importa um único item no destino, retornado o item atualizado e com o resultado da
	 * importação.
	 * 
	 * @param item Item a ser importado
	 * @return Item com os dados atualizados após importação
	 * @throws DataImportException Se ocorrer um erro durante a importação dos dados
	 */
	protected abstract DataImportItem<ID, T> importEntity(DataImportItem<ID, T> item) throws DataImportException;
}
