package br.com.sgpf.common.domain.entity;

import java.util.Date;

import br.com.sgpf.common.domain.entity.AbstractEntity;

public class AbstractEntityImpl extends AbstractEntity<Long> {
	private static final long serialVersionUID = 1L;
	
	public AbstractEntityImpl() {
		super();
	}
	
	public AbstractEntityImpl(Long id, Date creationDate, Date updateDate, Long versao) {
		this();
		setId(id);
		setCreationDate(creationDate);
		setUpdateDate(updateDate);
		setVersion(versao);
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof AbstractEntityImpl;
	}
}