package br.com.sgpf.common.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface padrão para todas as entidades do sistema.
 * 
 * @param <I> Tipo do identificador da entidade
 */
public interface Entity<I extends Serializable> extends Serializable, Cloneable {

	I getId();

	void setId(I id);

	Date getCreationDate();

	void setCreationDate(Date creationDate);

	void setUpdateDate(Date updateDate);

	Date getUpdateDate();
	
	Long getVersion();

	void setVersion(Long version);
	
	/**
	 * Verifica se a entidade foi persistida.
	 * 
	 * @return Flag indicando se a entidade foi persistida
	 */
	boolean isPersisted();
}