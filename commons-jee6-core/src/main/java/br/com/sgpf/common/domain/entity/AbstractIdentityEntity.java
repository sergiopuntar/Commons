/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.entity;

import javax.persistence.MappedSuperclass;

/**
 * Super classe abstrata para todas as entidades do sistema com identificador baseado em uma coluna
 * de identidade auto incrementada na base de dados.
 */
@MappedSuperclass
public abstract class AbstractIdentityEntity extends AbstractEntity<Long> {
	private static final long serialVersionUID = -1470236638046051374L;
	
	/**
	 * Construtor padrão.
	 */
	public AbstractIdentityEntity() {
		super();
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof AbstractIdentityEntity;
	}
}
