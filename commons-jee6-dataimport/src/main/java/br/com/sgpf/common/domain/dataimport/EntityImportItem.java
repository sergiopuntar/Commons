package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.domain.dataimport.EntityImportResult.Status.DELETED;
import static br.com.sgpf.common.domain.dataimport.EntityImportResult.Status.FORCE_UPDATED;
import static br.com.sgpf.common.domain.dataimport.EntityImportResult.Status.INSERTED;
import static br.com.sgpf.common.domain.dataimport.EntityImportResult.Status.OVERRIDDEN;
import static br.com.sgpf.common.domain.dataimport.EntityImportResult.Status.UPDATED;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;

import br.com.sgpf.common.domain.entity.Entity;

/**
 * Classe Wrapper de entidades utilizada no processo de importação.
 *
 * @param <T> Identificador o item de importação
 * @param <E> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 */
public class EntityImportItem<T extends Serializable, E extends Entity<? extends Serializable>> implements Serializable {
	private static final long serialVersionUID = -4993453059086257679L;
	
	private static final List<EntityImportResult.Status> CHANGED_STATUS = Lists.newArrayList(INSERTED, UPDATED, FORCE_UPDATED, DELETED, OVERRIDDEN);
	
	private T id;
	private E entity;
	private boolean insert;
	private boolean update;
	private boolean merge;
	private boolean remove;
	private boolean force;
	private boolean sync;
	private EntityImportResult result;
	
	public EntityImportItem(T id, E entity, boolean insert, boolean update, boolean remove, boolean force, boolean sync) {
		super();
		this.id = id;
		this.entity = entity;
		this.insert = insert;
		this.update = update;
		this.remove = remove;
		this.sync = sync;
		this.result = new EntityImportResult();
	}

	public T getId() {
		return id;
	}

	public E getEntity() {
		return entity;
	}

	public boolean isInsert() {
		return insert;
	}

	public boolean isUpdate() {
		return update;
	}

	public boolean isMerge() {
		return merge;
	}

	public boolean isRemove() {
		return remove;
	}

	public boolean isForce() {
		return force;
	}

	public boolean isSync() {
		return sync;
	}

	public EntityImportResult getResult() {
		return result;
	}
	
	/**
	 * Verifica se os dados da entidade continda no item foram alterados após a importação,
	 * incluído informações de versionamento.<br>
	 * A entidade pode ter sido sobrescrita por completo com dados da entidade do destino, somente
	 * as informações de versionamento podem ter sido atualizadas, ou a entidade pode ter sido
	 * removida.
	 * 
	 * @return True se houve alteração, False caso contrário 
	 */
	public boolean entityChanged() {
		return CHANGED_STATUS.contains(getResult().getStatus());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 11)
				.append(getId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityImportResult) {
			EntityImportItem<?, ?> eii = (EntityImportItem<?, ?>) obj;
			return new EqualsBuilder()
					.append(getId(), eii.getId()).isEquals();
		}

		return false;
	}
}
