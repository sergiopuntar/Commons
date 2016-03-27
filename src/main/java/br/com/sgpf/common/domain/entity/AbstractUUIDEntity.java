package br.com.sgpf.common.domain.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super classe abstrata para todas as entidades do sistema com identificador
 * próprio baseado em UUID.
 * 
 * @param <T> Tipo do identificador da entidade
 */
@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractEntity<String> {
	private static final long serialVersionUID = 8082865570195906837L;

	@Id
	private String id;
	
	/**
	 * Construtor padrão que gera o identificador da entidade.
	 * A abordagem de geração de id no construtor garante que o objeto sempre
	 * possui um id único e, dessa forma, pode-se utilizar os métodos hashcode
	 * e equals confiáveis baseados somente no identificador.
	 */
	public AbstractUUIDEntity() {
		super();
		this.id = EntityUtil.gerarUUID();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
