package br.com.sgpf.common.domain.vo;

import java.io.Serializable;

public class SimpleDataElement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	public SimpleDataElement(Long id) {
		super();
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((SimpleDataElement)obj).getId());
	}
}