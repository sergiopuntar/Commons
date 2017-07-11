package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sgpf.common.domain.dataimport.exception.EntityImportException;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Implementação base para os importadores de entidade.
 * 
 * @param <T> Identificador o item de importação
 * @param <E> Tipo da entidade
 */
public abstract class BaseEntityImporterImpl<T extends Serializable, E extends Entity<? extends Serializable>> implements EntityImporter<T, E> {
	private static final long serialVersionUID = 5124248593928945081L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseEntityImporterImpl.class);
	
	private static final String ERROR_NON_READEBLE_DATASOURCE = "As entidades não podem ser importadas pois os DataSource %s não é legível.";

	@Override
	public Collection<EntityImportItem<T, E>> importEntities(EntityDataSource<T, E> dataSource, boolean sync) throws EntityImportException {
		List<EntityImportItem<T, E>> itens = new ArrayList<EntityImportItem<T, E>>();
		
		dataSource.open();
		
		if (dataSource.isReadable()) {
			throw new EntityImportException(String.format(ERROR_NON_READEBLE_DATASOURCE, dataSource));
		}
		
		while (dataSource.hasNext()) {
			EntityImportItem<T, E> item = importEntity(dataSource.getNext());
			
			if (sync && item.entityChanged() && item.isSync() && dataSource.isWritable()) {
				dataSource.sync(item);
				item.getResult().setSynced(true);
			} else if (sync && item.entityChanged() && item.isSync() && !dataSource.isWritable()) {
				LOGGER.info("Não foi possível syncronizar a entidade '{0}' pois a fonte de dados não é gravável.");
			}
			
			itens.add(item);
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
	 * @throws EntityImportException
	 */
	protected abstract EntityImportItem<T, E> importEntity(EntityImportItem<T, E> item) throws EntityImportException;
}
