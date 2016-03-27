package br.com.sgpf.common.domain.entity;

import java.util.Collection;
import java.util.UUID;

/**
 * Classe de utilitários para entidades.
 */
public class EntityUtil {

	/**
	 * Gera um UUID para ser usado como identificador de uma entidade.
	 * 
	 * @return UUID gerado
	 */
	public static String gerarUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	/**
	 * Recupera uma entidade de uma lista a partir do seu identificador.
	 * 
	 * @param entidades Lista de entidades
	 * @param id Identificador da entidade
	 * @return Entidade recuperada, null se ela não estiver presente na lista
	 */
	public static <T extends Entity<?>> T recuperarEntidade(Collection<T> entidades, String id) {
		for (T entidade : entidades) {
			if(entidade.getId()!=null && entidade.getId().equals(id)) {
				return entidade;
			}
		}
		
		return null;
	}
}
