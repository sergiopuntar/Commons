/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.entity;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Classe de utilitários para entidades.
 * 
 * @author Sergio Puntar
 */
public class EntityUtil {

	private static final String ARG_NAME_ID = "id";
	private static final String ARG_NAME_ENTITIES = "entities";
	
	private EntityUtil() {
		super();
	}

	/**
	 * Gera um UUID para ser usado como identificador de uma entidade.
	 * 
	 * @return UUID gerado
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	/**
	 * Recupera uma entidade de uma lista a partir do seu identificador.
	 * 
	 * @param entities Lista de entidades
	 * @param id Identificador da entidade
	 * @return Entidade recuperada, null se ela não estiver presente na lista
	 */
	public static <T extends Entity<I>, I extends Serializable> T recoverEntity(Collection<T> entities, I id) {
		checkNotNull(entities, ERROR_NULL_ARGUMENT, ARG_NAME_ENTITIES);
		checkNotNull(id, ERROR_NULL_ARGUMENT, ARG_NAME_ID);
		
		for (T entity : entities) {
			if(entity.getId() != null && entity.getId().equals(id)) {
				return entity;
			}
		}
		
		return null;
	}
}
