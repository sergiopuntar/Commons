/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;
import java.util.Collection;

import br.com.sgpf.common.domain.dataimport.exception.DataImportException;

/**
 * Interface do importador de dados.
 * 
 * @param <I> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public interface DataImporter<I extends Serializable, T extends Serializable> extends Serializable {
	
	/**
	 * Processa a importação de todos os item encontrados no DataSource. 
	 * 
	 * @param dataSource DataSource dos dados
	 * @param sync Flag que indica se os dados da origem devem ser sincronizados com os dados do
	 * destino. Os dados só serão sincronizados para os itens marcados no DataSource como
	 * sincronizáveis. Se a flag for setada como False, as marcações de sincronização no DataSource
	 * serão ignoradas
	 * @return Dados dos itens lidos do DataSource com seus respectivos
	 * resultados.
	 * @throws DataImportException Se ocorrer um erro na importação dos dados
	 */
	public Collection<DataImportItem<I, T>> importData(ImportDataSource<I, T> dataSource, boolean sync) throws DataImportException;
}
