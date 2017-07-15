package br.com.sgpf.common.domain.dataimport.entity;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.DataImporter;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Interface do importador de entidades.
 * 
 * @param <ID> Identificador o item de importação
 * @param <E> Tipo da entidade
 */
public interface EntityImporter<ID extends Serializable, E extends Entity<? extends Serializable>> extends DataImporter<ID, E> {
	
}
