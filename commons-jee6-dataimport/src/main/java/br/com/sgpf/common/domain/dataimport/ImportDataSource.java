/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.exception.DataImportException;

/**
 * Interface das fontes de dados de importação.
 * 
 * @param <I> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public interface ImportDataSource<I extends Serializable, T extends Serializable> extends Serializable {

	/**
	 * Abre a fonte de dados para iniciar a leitura ou escrita.
	 * 
	 * @throws DataImportException Se ocorrer um erro na abertura da fonte de dados.
	 */
	public void open() throws DataImportException;
	
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
	 * @throws DataImportException Se ocorrer um erro na leitura da fonte de dados.
	 */
	public boolean hasNext() throws DataImportException;
	
	/**
	 * Lê o próximo item na sequência da fonte de dados.
	 * 
	 * @return Item lido da fonte de dados
	 * @throws DataImportException Se ocorrer um erro na leitura da fonte de dados.
	 */
	public DataImportItem<I, T> next() throws DataImportException;
	
	/**
	 * Lê o item corrente na sequência da fonte de dados.<br>
	 * Deve ser chamado somente após o método {@link #next()} haver sido chamado ao menos uma vez
	 * desde que o Data Source foi aberto, caso contrário não existe um item corrente para a lido.
	 * 
	 * @return Item lido da fonte de dados
	 * @throws DataImportException Se ocorrer um erro na leitura da fonte de dados.
	 */
	public DataImportItem<I, T> current() throws DataImportException;
	
	/**
	 * Sincroniza os dados de um item na fonte de dados.<br>
	 * Os dados existentes para o item na fonte de dados serão substituídos.
	 * 
	 * @param item Item a ser sincronizado na fonte de dados.
	 * @return Flag indicando se houve mudança real na fonte de dados
	 * @throws DataImportException Se ocorrer um erro na escrita da fonte de dados.
	 */
	public boolean sync(DataImportItem<I, T> item) throws DataImportException;
	
	/**
	 * Fecha a fonte de dados e libera os recursos ocupados.
	 * 
	 * @throws DataImportException Se ocorrer um erro no fechamento da fonte de dados.
	 */
	public void close() throws DataImportException;
}
