package br.com.sgpf.common.domain.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super classe abstrata para todas as entidades do sistema com identificador
 * próprio baseado em UUID.
 * Essa implementação segue a abordagem de geração de id no construtor, o que
 * garante que o objeto possui sempre um id único e, dessa forma, os métodos
 * hashcode e equals serão confiáveis baseados somente no identificador.
 * <b>Essa abordagem é válida somente em ambientes sem concorrência.</b>
 */
@MappedSuperclass
public abstract class AbstractUUIDEntity extends AbstractEntity<String> {
	private static final long serialVersionUID = 8082865570195906837L;
	
	private static final String ERROR_EQUALS_HASHCODE_UNSUPPORTED = "Os métodos unpersistedEquals e unpersistedHashCode não são suportados pela classe AbstractUUIDEntity, pois objetos desse tipo devem sempre possuir um id baseeado em UUID.";

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

	@Override
	public int unpersistedHashCode() {
		throw new UnsupportedOperationException(ERROR_EQUALS_HASHCODE_UNSUPPORTED);
	}

	@Override
	public boolean unpersistedEquals(Object obj) {
		throw new UnsupportedOperationException(ERROR_EQUALS_HASHCODE_UNSUPPORTED);
	}
}
