/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport;

import java.io.Serializable;

/**
 * Classe que contêm as instruções de importação de de dados para um item de importação. 
 */
public class DataImportInstructions implements Serializable {
	private static final long serialVersionUID = 5158010165679258326L;
	
	private Boolean insert;
	private Boolean update;
	private Boolean merge;
	private Boolean remove;
	private Boolean force;
	private Boolean sync;
	
	public DataImportInstructions(Boolean insert, Boolean update, Boolean merge, Boolean remove, Boolean force,
			Boolean sync) {
		super();
		this.insert = insert;
		this.update = update;
		this.merge = merge;
		this.remove = remove;
		this.force = force;
		this.sync = sync;
	}

	public Boolean isInsert() {
		return insert;
	}

	public void setInsert(Boolean insert) {
		this.insert = insert;
	}

	public Boolean isUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}

	public Boolean isMerge() {
		return merge;
	}

	public void setMerge(Boolean merge) {
		this.merge = merge;
	}

	public Boolean isRemove() {
		return remove;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public Boolean isForce() {
		return force;
	}

	public void setForce(Boolean force) {
		this.force = force;
	}

	public Boolean isSync() {
		return sync;
	}

	public void setSync(Boolean sync) {
		this.sync = sync;
	}
}
