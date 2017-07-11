package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;
import java.util.Collection;

import br.com.sgpf.common.domain.dataimport.exception.EntityImportException;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Interface do importador de entidades.
 * 
 * @param <T> Identificador o item de importação
 * @param <E> Tipo da entidade
 */
public interface EntityImporter<T extends Serializable, E extends Entity<? extends Serializable>> extends Serializable {
	
	/**
	 * Processa a importação de todos os item encontrados no DataSource. 
	 * 
	 * @param dataSource DataSource das entidades
	 * @param sync Flag que indica se os dados da origem devem ser sincronizados com os dados do
	 * destino. Os dados só serão sincronizados para os itens marcados no DataSource como
	 * sincronizáveis. Se a flag for setada como False, as marcações de sincronização no DataSource
	 * serão ignoradas
	 * @return Dados dos itens lidos do DataSource com seus respectivos
	 * resultados.
	 * @throws EntityImportException Se ocorrer um erro na importação das entidades
	 */
	public Collection<EntityImportItem<T, E>> importEntities(EntityDataSource<T, E> dataSource, boolean sync) throws EntityImportException;
}
