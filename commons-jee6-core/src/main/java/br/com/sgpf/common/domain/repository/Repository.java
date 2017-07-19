package br.com.sgpf.common.domain.repository;

import java.io.Serializable;
import java.util.List;

import br.com.sgpf.common.domain.entity.Entity;

/**
 * Interface genérica de repositórios de entidades.
 *
 * @param <E> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public interface Repository<E extends Entity<ID>, ID extends Serializable> extends Serializable {

	/**
	 * Recupera uma entidade a partir do seu identificador.
	 *
	 * @param id Identificador da entidade
	 * @return Entidade recuperada
	 */
	E find(ID id);

	/**
	 * Recupera todas as entidades.
	 *
	 * @return List<E> Entidades recuperadas
	 */
	List<E> findAll();

	/**
	 * Persiste uma nova entidade no repositório.
	 *
	 * @param entidade Entidade a ser persistida
	 */
	void persist(E entidade);

	/**
	 * Atualiza os dados de uma entidade no repositório.
	 *
	 * @param entidade Entidade a ser atualizada no repositório
	 * @return Entidade atualizada no repositório
	 */
	E merge(E entidade);
	
	/**
	 * Atualiza os dados de uma entidade a partir do repositório.
	 * 
	 * @param entidade Entidade a ser atualizada a partir do repositório
	 */
	void refresh(E entidade);

	/**
	 * Remove a entidade do repositório.
	 *
	 * @param entidade Entidade a ser removida
	 */
	void remove(E entidade);
}
