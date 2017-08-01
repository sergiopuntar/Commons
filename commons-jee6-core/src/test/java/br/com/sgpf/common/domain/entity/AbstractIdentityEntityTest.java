/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.entity;

import org.junit.Test;

import br.com.sgpf.common.test.equals.EqualsTester;
import br.com.sgpf.common.test.pojo.PojoTester;

public class AbstractIdentityEntityTest {

	@Test
	public void pojoTest() {
		PojoTester pojoTester = new PojoTester(AbstractIdentityEntityImpl.class);
		pojoTester.strictValidatePojoStructAndBehaviour();
	}
	
	@Test
	public void equalsTest() {
		EqualsTester<AbstractIdentityEntityImpl> equalsTester = new EqualsTester<>(AbstractIdentityEntityImpl.class, false, "id", "version");
		equalsTester.validate();
	}
}
