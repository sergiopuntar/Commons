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
 * @param <ID> Identificador o item de importação
 * @param <T> Tipo do dado
 */
public class DataImportItem<ID extends Serializable, T extends Serializable> implements Serializable, CanEqual {
	private static final long serialVersionUID = -4993453059086257679L;
	
	private static final List<DataImportResult.Status> CHANGED_STATUS = Lists.newArrayList(INSERTED, UPDATED, FORCE_UPDATED, DELETED, OVERRIDDEN);
	
	private ID id;
	private T data;
	private boolean insert;
	private boolean update;
	private boolean merge;
	private boolean remove;
	private boolean force;
	private boolean sync;
	private DataImportResult result;
	
	public DataImportItem(ID id, T data, boolean insert, boolean update, boolean remove, boolean force, boolean sync) {
		super();
		this.id = id;
		this.data = data;
		this.insert = insert;
		this.update = update;
		this.remove = remove;
		this.sync = sync;
		this.result = new DataImportResult();
	}

	public ID getId() {
		return id;
	}

	public T getData() {
		return data;
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

	public DataImportResult getResult() {
		return result;
	}
	
	/**
	 * Verifica se os dados contidos no item foram alterados após a importação.
	 * 
	 * @return True se houve alteração, False caso contrário 
	 */
	public boolean dataChanged() {
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
			return new EqualsBuilder()
					.append(this.getId(), that.getId()).isEquals();
		}

		return false;
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof DataImportItem;
	}
}
