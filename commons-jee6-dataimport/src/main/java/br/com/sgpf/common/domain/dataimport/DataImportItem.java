package br.com.sgpf.common.domain.dataimport;

import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.DELETED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.FORCE_UPDATED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.INSERTED;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.OVERRIDDEN;
import static br.com.sgpf.common.domain.dataimport.DataImportResult.Status.UPDATED;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;

import br.com.sgpf.common.util.CanEqual;

/**
 * Classe Wrapper de dados utilizada no processo de importação.
 *
 * @param <Id> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public class DataImportItem<Id extends Serializable, T extends Serializable> implements Serializable, CanEqual {
	private static final long serialVersionUID = -4993453059086257679L;
	
	private static final List<DataImportResult.Status> CHANGED_STATUS = Lists.newArrayList(INSERTED, UPDATED, FORCE_UPDATED, DELETED, OVERRIDDEN);
	
	private Id id;
	private T data;
	private Boolean insert;
	private Boolean update;
	private Boolean merge;
	private Boolean remove;
	private Boolean force;
	private Boolean sync;
	private DataImportResult result;
	
	public DataImportItem(Id id, T data, Boolean insert, Boolean update, Boolean merge, Boolean remove, Boolean force, Boolean sync) {
		super();
		this.id = id;
		this.data = data;
		this.insert = insert;
		this.update = update;
		this.merge = merge;
		this.remove = remove;
		this.force = force;
		this.sync = sync;
		this.result = new DataImportResult();
	}

	public Id getId() {
		return id;
	}

	public T getData() {
		return data;
	}

	public Boolean isInsert() {
		return insert;
	}

	public Boolean isUpdate() {
		return update;
	}

	public Boolean isMerge() {
		return merge;
	}

	public Boolean isRemove() {
		return remove;
	}

	public Boolean isForce() {
		return force;
	}

	public Boolean isSync() {
		return sync;
	}

	public DataImportResult getResult() {
		return result;
	}
	
	/**
	 * Verifica se os dados contidos no item foram alterados após a importação.
	 * 
	 * @return True se houve alteração, False caso contrário 
	 */
	public Boolean dataChanged() {
		return CHANGED_STATUS.contains(getResult().getStatus());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 11)
				.append(this.getId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataImportItem) {
			DataImportItem<?, ?> that = (DataImportItem<?, ?>) obj;
			return that.canEqual(this) &&
					new EqualsBuilder()
					.append(this.getId(), that.getId()).isEquals();
		}

		return false;
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof DataImportItem;
	}
}
