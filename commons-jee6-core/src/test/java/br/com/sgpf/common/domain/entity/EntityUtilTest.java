package br.com.sgpf.common.domain.entity;

import java.util.UUID;

import org.junit.Test;

public class EntityUtilTest {

	@Test
	public void generateUUIDTest() {
		// Verifica se gerou um UUID válido
		UUID.fromString(EntityUtil.generateUUID());
	}
	
}
