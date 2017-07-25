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
 * @param <Id> Tipo do identificador da entidade
 */
@MappedSuperclass
public abstract class AbstractEntity<Id extends Serializable> implements Entity<Id>, CanEqual {
	private static final long serialVersionUID = 7899846729108918584L;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Version
	private Long version;
	
	@PrePersist
	public void prePersist() {
		setCreationDate(new Date());
	}
	
	@PreUpdate
	public void preUpdate() {
		setUpdateDate(new Date());
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public Long getVersion() {
		return version;
	}

	@Override
	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public boolean isPersisted() {
		return getCreationDate() != null;
	}

	@Override
	public String toString() {
		return getId() != null ? getId().toString() : null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 11)
				.append(this.getId())
				.append(this.getVersion())
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractEntity) {
			AbstractEntity<?> that = (AbstractEntity<?>) obj;
			return that.canEqual(this) &&
					new EqualsBuilder()
					.append(this.getId(), that.getId())
					.append(this.getVersion(), that.getVersion())
					.isEquals();
		}

		return false;
	}
}
