package br.com.sgpf.common.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.com.sgpf.common.util.CanEqual;

/**
 * Super classe abstrata para todas as entidades do sistema.
 * 
 * @param <ID> Tipo do identificador da entidade
 */
@MappedSuperclass
public abstract class AbstractEntity<ID extends Serializable> implements Entity<ID>, CanEqual {
	private static final long serialVersionUID = 7899846729108918584L;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAtualizacao;
	
	@Version
	private Long versao;
	
	@PrePersist
	public void prePersist() {
		setDataCriacao(new Date());
	}
	
	@PreUpdate
	public void preUpdate() {
		setDataAtualizacao(new Date());
	}
	
	@Override
	public Date getDataCriacao() {
		return dataCriacao;
	}

	@Override
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Override
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	@Override
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Override
	public Long getVersao() {
		return versao;
	}

	@Override
	public void setVersao(Long versao) {
		this.versao = versao;
	}

	@Override
	public boolean isPersisted() {
		return getDataCriacao() != null;
	}

	@Override
	public String toString() {
		return getId() != null ? getId().toString() : null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 11)
				.append(this.getId())
				.append(this.getVersao())
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractEntity) {
			AbstractEntity<?> that = (AbstractEntity<?>) obj;
			return that.canEqual(this) &&
					new EqualsBuilder()
					.append(this.getId(), that.getId())
					.append(this.getVersao(), that.getVersao())
					.isEquals();
		}

		return false;
	}
}
