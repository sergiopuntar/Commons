package br.com.sgpf.common.domain.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super classe abstrata para todas as entidades do sistema com identificador
 * baseado em uma coluna de identidade auto incrementada na base de dados.
 */
@MappedSuperclass
public abstract class AbstractIdentityEntity extends AbstractEntity<Long> {
	private static final long serialVersionUID = -1470236638046051374L;
	
	@Id
	private Long id;
	
	/**
	 * Construtor padrão.
	 */
	public AbstractIdentityEntity() {
		super();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}
