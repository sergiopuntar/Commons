package br.com.sgpf.common.domain.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super classe abstrata para todas as entidades do sistema com identificador
 * próprio baseado em UUID.<br>
 * <b>Essa abordagem é válida somente em ambientes sem redundância.</b><br>
 * Se a applicação roda de forma reduntante, duas instâncias tem uma baixíssima
 * porem existente possibilidade de gerar UUIDs iguais 
 */
@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractEntity<String> {
	private static final long serialVersionUID = 8082865570195906837L;
	
	@Id
	// O ideal seria que o id fosse do tipo UUID, mas o JPA 2.1 não suporta converter em IDs 
	private String id;
	
	/**
	 * Construtor padrão que gera o identificador da entidade.
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
