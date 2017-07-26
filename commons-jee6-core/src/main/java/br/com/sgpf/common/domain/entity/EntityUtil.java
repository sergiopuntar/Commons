package br.com.sgpf.common.domain.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Classe de utilitários para entidades.
 */
public class EntityUtil {

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
		for (T entity : entities) {
			if(entity.getId() != null && entity.getId().equals(id)) {
				return entity;
			}
		}
		
		return null;
	}
}
