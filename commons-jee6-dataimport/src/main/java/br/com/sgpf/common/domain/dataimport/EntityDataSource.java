package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.exception.EntityDataSourceException;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Interface das origens de dados de entidades.
 * 
 * @param <T> Identificador o item de importação
 * @param <E> Tipo da entidade
 */
public interface EntityDataSource<T extends Serializable, E extends Entity<? extends Serializable>> extends Serializable {

	/**
	 * Abre o DataSource para iniciar a leitura ou escrita.
	 * 
	 * @throws EntityDataSourceException Se ocorrer um erro na abertura do DataSource.
	 */
	public void open() throws EntityDataSourceException;
	
	/**
	 * Verifica se o DataSource é legível.
	 * 
	 * @return True se for legível, False caso contrário
	 */
	public boolean isReadable();
	
	/**
	 * Verifica se o DataSource é gravável.
	 * 
	 * @return True se for gravável, False caso contrário
	 */
	public boolean isWritable();
	
	/**
	 * Verifica se existe um póximo item na sequeência do DataSource.
	 * 
	 * @return True se existe um próximo item, False caso contrário.
	 * @throws EntityDataSourceException Se ocorrer um erro na leitura do DataSource.
	 */
	public boolean hasNext() throws EntityDataSourceException;
	
	/**
	 * Lê o próximo item na sequência do DataSource.
	 * 
	 * @return Item lido do datasource
	 * @throws EntityDataSourceException Se ocorrer um erro na leitura do DataSource.
	 */
	public EntityImportItem<T, E> getNext() throws EntityDataSourceException;
	
	/**
	 * Sincroniza os dados de um item no DataSource.<br>
	 * Os dados existentes para o item no DataSource serão substituídos.
	 * 
	 * @param item Item a ser sincronizado no DataSource.
	 * @throws EntityDataSourceException Se ocorrer um erro na escrita no DataSource.
	 */
	public void sync(EntityImportItem<T, E> item) throws EntityDataSourceException;
	
	/**
	 * Fecha o DataSource e libera os recursos ocupados.
	 * 
	 * @throws EntityDataSourceException Se ocorrer um erro no fechamento do DataSource.
	 */
	public void close() throws EntityDataSourceException;
}
