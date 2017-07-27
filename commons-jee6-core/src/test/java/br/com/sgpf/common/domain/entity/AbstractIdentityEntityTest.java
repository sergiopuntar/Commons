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
