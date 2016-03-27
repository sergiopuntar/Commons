package br.com.sgpf.common.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface padrão para todas as entidades do sistema.
 * 
 * @param <ID> Tipo do identificador da entidade
 */
public interface Entity<ID extends Serializable> extends Serializable, Cloneable {

	ID getId();

	void setId(ID id);

	Date getDataCriacao();

	void setDataCriacao(Date dataCriacao);

	void setDataAtualizacao(Date dataAtualizacao);

	Date getDataAtualizacao();
	
	Long getVersao();

	void setVersao(Long versao);
	
	/**
	 * Verifica se a entidade foi persistida.
	 * 
	 * @return Flag indicando se a entidade foi persistida
	 */
	boolean isPersisted();
}