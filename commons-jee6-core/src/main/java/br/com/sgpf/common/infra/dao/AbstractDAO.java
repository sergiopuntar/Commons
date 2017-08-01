/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.dao;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkNotNull;

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
 * @param <I> Tipo do identificador da entidade
 * 
 * @author Sergio Puntar
 */
public abstract class AbstractDAO<E extends Entity<I>, I extends Serializable> implements Repository<E, I> {
	private static final long serialVersionUID = -6837735901842866300L;
	
	private static final String ERROR_RESULT_TYPE = "O objeto [%s] não é do tipo esperado [%s].";
	
	private static final String ARG_NAME_ID = "id";
	private static final String ARG_NAME_ENTITY = "entity";
	private static final String ARG_NAME_RESULT_TYPE = "resultType";

	@PersistenceContext
	protected transient EntityManager em;

	protected Class<E> clazz;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void postConstruct() {
		clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public E find(I id) {
		checkNotNull(id, ERROR_NULL_ARGUMENT, ARG_NAME_ID);
		
		return em.find(clazz, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		return em.createQuery("from " + clazz.getName()).getResultList();
	}

	@Override
	public void persist(E entity) {
		checkNotNull(entity, ERROR_NULL_ARGUMENT, ARG_NAME_ENTITY);
		
		em.persist(entity);
		em.flush();
	}

	@Override
	public E merge(E entity) {
		checkNotNull(entity, ERROR_NULL_ARGUMENT, ARG_NAME_ENTITY);
		
		E persistedEntity = em.merge(entity);
		em.flush();

		return persistedEntity;
	}

	@Override
	public void refresh(E entity) {
		checkNotNull(entity, ERROR_NULL_ARGUMENT, ARG_NAME_ENTITY);
		
		em.refresh(entity);
	}

	@Override
	public void remove(E entity) {
		checkNotNull(entity, ERROR_NULL_ARGUMENT, ARG_NAME_ENTITY);
		
		if (!em.contains(entity)) {
			em.remove(em.merge(entity));
		} else {
			em.remove(entity);
		}
		
		em.flush();
	}

	/**
	 * Recupera o primeiro resultado de uma lista de resultados de consulta.
	 * 
	 * @param resultList Lista de resultados de consulta
	 * @param resultType Tipo dos itens da lista
	 * @return Primeiro resultado da lista
	 */
	@SuppressWarnings("unchecked")
	protected <T> T firstResult(List<?> resultList, Class<T> resultType) {
		checkNotNull(resultType, ERROR_NULL_ARGUMENT, ARG_NAME_RESULT_TYPE);
		
		T firstResult = null;
		Object object = (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;

		if (resultType.isInstance(object)) {
			firstResult = (T) object;
		} else if (object != null) {
			throw new DAOException(String.format(ERROR_RESULT_TYPE, object, resultType.getName()));
		}

		return firstResult;
	}
}
