package br.com.sgpf.common.domain.entity;

import java.util.UUID;

public class EntityUtilTest {

	public void generateUUIDTest() {
		// Verifica se gerou um UUID válido
		UUID.fromString(EntityUtil.generateUUID());
	}
	
}
