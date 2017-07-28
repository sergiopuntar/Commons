package br.com.sgpf.common.domain.entity;

import java.util.Date;

import br.com.sgpf.common.domain.entity.AbstractUUIDEntity;

public class AbstractUUIDEntityImpl extends AbstractUUIDEntity {
	private static final long serialVersionUID = 1L;
	
	public AbstractUUIDEntityImpl() {
		super();
	}
	
	public AbstractUUIDEntityImpl(String id, Date creationDate, Date updateDate, Long versao) {
		this();
		setId(id);
		setCreationDate(creationDate);
		setUpdateDate(updateDate);
		setVersion(versao);
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof AbstractUUIDEntityImpl;
	}
}