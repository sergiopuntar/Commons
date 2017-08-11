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
 * Interface do importador de dados.<br>
 * <br>
 * O processo de importação de dados possui as seguintes etapas:<br>
 * <ol>
 * <li>Leitura dos itens na origem</li>
 * <li>Conciliação dos dados da origem com o destino</li>
 * <li>Atualização dos dados no destino de acordo com o resultado da conciliação</li>
 * <li>Atualização dos dados na origem de acordo com o resultado da conciliação(opcional)</li>
 * </ol>
 * A conciliação leva em conta as instruções de importação para cada item contidas na origem:<br>
 * <br>
 * <ul>
 * <li><b>INSERT</b> - Insere o item como um novo registro</li>
 * <li><b>UPDATE</b> - Atualiza o registro preexistente do item</li>
 * <li><b>MERGE</b> - Insere o item como um novo registro caso ainda não exista ou atualiza o
 * registro preexistente</li>
 * <li><b>REMOVE</b> - Remove o registro do item</li>
 * <li><b>FORCE</b> - Força a execução da instrução mesmo que os dados do destino sejam mais
 * recentes que os da origem</li>
 * <li><b>SYNC</b> - Sincroniza os dados na origem após a importação no destino, atualizando os
 * possíveis metadados alterados (id, número de versão, etc.) ou mesmo o item como um todo,
 * caso os dados do destino sejam mais recentes que os da origem</li> 
 * </ul>
 * A princípio, os dados mais recentes são mantidos, sejam os da origem ou os do destino. Contudo,
 * caso a opção <b>FORCE</b> seja ativada, os dados mantidos sempre são os da origem, mesmo que
 * mais antigos que os do destino.<br><br>
 * 
 * @param <I> Identificador o item de importação
 * @param <T> Tipo do dado
 * 
 * @author Sergio Puntar
 */
public interface DataImporter<I extends Serializable, T extends Serializable> extends Serializable {
	
	/**
	 * Processa a importação de todos os itens encontrados na origem de dados.<br>
	 * <br>
	 * Apesar de ser configurável a nível de item, a sincronização pode ser desativada globalmente
	 * através do parametro {@code sync}. Se definido como {@code false}, nenhum item será
	 * singronizado com a origem. Se definido como {@code true}, os itens com a opção <b>SYNC</b>
	 * ativada na origem serão sincronizados.<br>
	 * <br>
	 * Durante o processo de importação, exceções podem ocorrer durante a leitura e gravação tanto
	 * na origem quanto no destino. Através do parâmetro {@code suppressExceptions}, é possível
	 * definir qual será o tratamento dessas exceções. Se definido como {@code true}, as exceções
	 * serão suprimidas e o status do resultado da importação será <b>ERROR</b>. Se definido como
	 * {@code false}, as exceções serão arremessadas interrompendo o processo de importação, e as
	 * camadas superiores da aplicação deverão tratá-las.<br>
	 * Obs.: Exceções durante a abertura e fechamento da origem de dados nunca são suprimidas. 
	 * 
	 * @param dataSource Origem dos dados
	 * @param sync Flag que indica se os dados da origem devem ser sincronizados com os dados do
	 * destino.
	 * @param suppressExceptions Flag que indica se as exceções devem ser suprimidas ou não
	 * @return Dados dos itens lidos do DataSource com seus respectivos resultados.
	 * @throws DataImportException Se ocorrer um erro na importação dos dados
	 */
	public Collection<DataImportItem<I, T>> importData(ImportDataSource<I, T> dataSource, boolean sync, boolean suppressExceptions) throws DataImportException;
}
