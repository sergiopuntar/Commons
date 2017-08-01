/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.entity;

import java.util.UUID;

import org.junit.Test;

import br.com.sgpf.common.test.equals.EqualsTester;
import br.com.sgpf.common.test.pojo.PojoTester;

public class AbstractUUIDEntityTest {
	
	@Test
	public void constructorTest() {
		AbstractUUIDEntityImpl abstractUUIDEntity = new AbstractUUIDEntityImpl();
		// Somente verifica se o identificador é um UUID válido
		UUID.fromString(abstractUUIDEntity.getId());
	}

	@Test
	public void pojoTest() {
		PojoTester pojoTester = new PojoTester(AbstractUUIDEntityImpl.class);
		pojoTester.strictValidatePojoStructAndBehaviour();
	}
	
	@Test
	public void equalsTest() {
		EqualsTester<AbstractUUIDEntityImpl> equalsTester = new EqualsTester<>(AbstractUUIDEntityImpl.class, false, "id", "version");
		equalsTester.validate();
	}
}
