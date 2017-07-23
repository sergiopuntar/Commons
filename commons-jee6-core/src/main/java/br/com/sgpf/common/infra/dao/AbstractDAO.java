package br.com.sgpf.common.infra.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.sgpf.common.domain.entity.Entity;
import br.com.sgpf.common.domain.repository.Repository;
import br.com.sgpf.common.infra.exception.DAOException;

/**
 * Classe abstrata para acesso a dados de entidades.
 *
 * @param <E> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public abstract class AbstractDAO<E extends Entity<ID>, ID extends Serializable> implements Repository<E, ID> {
	private static final long serialVersionUID = -6837735901842866300L;
	
	private static final String ERRO_TIPO_RESULTADO = "O objeto %s não é do tipo esperado %s.";

	@PersistenceContext
	protected transient EntityManager em;

	protected Class<E> clazz;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void postConstruct() {
		clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public E find(ID id) {
		return em.find(clazz, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		return em.createQuery("from " + clazz.getName()).getResultList();
	}

	@Override
	public void persist(E entidade) {
		em.persist(entidade);
		em.flush();
	}

	@Override
	public E merge(E entidade) {
		E entidadePersistida = em.merge(entidade);
		em.flush();

		return entidadePersistida;
	}

	@Override
	public void refresh(E entidade) {
		em.refresh(entidade);
	}

	@Override
	public void remove(E entidade) {
		if (!em.contains(entidade)) {
			em.remove(merge(entidade));
		} else {
			em.remove(entidade);
		}
		
		em.flush();
	}

	/**
	 * Recupera o primeiro resultado de uma lista de resultados de consulta.
	 * 
	 * @param resultList Lista de resultados de consulta
	 * @param tipoResultado Tipo dos itens da lista
	 * @return Primeiro resultado da lista
	 */
	@SuppressWarnings("unchecked")
	protected <T> T recuperarPrimeiroResultado(List<?> resultList, Class<T> tipoResultado) {
		T primeiroResultado = null;

		Object object = (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;

		if (tipoResultado.isInstance(object)) {
			primeiroResultado = (T) object;
		} else if (object != null) {
			throw new DAOException(String.format(ERRO_TIPO_RESULTADO, object, tipoResultado.getName()));
		}

		return primeiroResultado;
	}
}
