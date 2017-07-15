package br.com.sgpf.common.domain.dataimport.entity;

import java.io.Serializable;

import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.entity.Entity;

/**
 * Interface das origens de dados de entidades.
 * 
 * @param <ID> Identificador o item de importação
 * @param <E> Tipo da entidade
 */
public interface EntityDataSource<ID extends Serializable, E extends Entity<? extends Serializable>> extends ImportDataSource<ID, E> {

}
