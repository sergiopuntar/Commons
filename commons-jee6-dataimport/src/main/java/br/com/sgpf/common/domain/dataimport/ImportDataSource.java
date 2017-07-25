package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.exception.ImportDataSourceException;

/**
 * Interface das fontes de dados de importação.
 * 
 * @param <Id> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public interface ImportDataSource<Id extends Serializable, T extends Serializable> extends Serializable {

	/**
	 * Abre a fonte de dados para iniciar a leitura ou escrita.
	 * 
	 * @throws ImportDataSourceException Se ocorrer um erro na abertura da fonte de dados.
	 */
	public void open() throws ImportDataSourceException;
	
	/**
	 * Verifica se a fonte de dados é gravável.
	 * 
	 * @return True se for gravável, False caso contrário
	 */
	public boolean isWritable();
	
	/**
	 * Verifica se existe um próximo item na sequência da fonte de dados.
	 * 
	 * @return True se existe um próximo item, False caso contrário.
	 * @throws ImportDataSourceException Se ocorrer um erro na leitura da fonte de dados.
	 */
	public boolean hasNext() throws ImportDataSourceException;
	
	/**
	 * Lê o próximo item na sequência da fonte de dados.
	 * 
	 * @return Item lido da fonte de dados
	 * @throws ImportDataSourceException Se ocorrer um erro na leitura da fonte de dados.
	 */
	public DataImportItem<Id, T> next() throws ImportDataSourceException;
	
	/**
	 * Sincroniza os dados de um item na fonte de dados.<br>
	 * Os dados existentes para o item na fonte de dados serão substituídos.
	 * 
	 * @param item Item a ser sincronizado na fonte de dados.
	 * @throws ImportDataSourceException Se ocorrer um erro na escrita da fonte de dados.
	 */
	public void sync(DataImportItem<Id, T> item) throws ImportDataSourceException;
	
	/**
	 * Fecha a fonte de dados e libera os recursos ocupados.
	 * 
	 * @throws ImportDataSourceException Se ocorrer um erro no fechamento da fonte de dados.
	 */
	public void close() throws ImportDataSourceException;
}
