package br.com.sgpf.common.domain.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import br.com.sgpf.common.test.equals.EqualsTester;
import br.com.sgpf.common.test.pojo.PojoTester;

public class AbstractEntityTest {
	
	@Test
	public void pojoTest() {
		PojoTester pojoTester = new PojoTester(AbstractEntityImpl.class);
		pojoTester.strictValidatePojoStructAndBehaviour();
	}
	
	@Test
	public void equalsTest() {
		EqualsTester<AbstractEntityImpl> equalsTester = new EqualsTester<>(AbstractEntityImpl.class, false, "id", "version");
		equalsTester.validate();
	}
	
	@Test
	public void prePersistTest() throws InterruptedException {
		AbstractEntityImpl abstractEntity = new AbstractEntityImpl();
		
		Date before = new Date();
		TimeUnit.SECONDS.sleep(1);
		abstractEntity.prePersist();
		TimeUnit.SECONDS.sleep(1);
		Date after = new Date();
		
		Date creationDate = abstractEntity.getCreationDate();
		Date updateDate = abstractEntity.getUpdateDate();
		
		assertEquals(creationDate, updateDate);
		assertTrue(before.before(creationDate));
		assertTrue(after.after(creationDate));
	}
	
	@Test
	public void preUpdateTest() throws InterruptedException {
		AbstractEntityImpl abstractEntity = new AbstractEntityImpl();
		
		Date before = new Date();
		TimeUnit.SECONDS.sleep(1);
		abstractEntity.preUpdate();
		TimeUnit.SECONDS.sleep(1);
		Date after = new Date();
		
		Date updateDate = abstractEntity.getUpdateDate();
		
		assertTrue(before.before(updateDate));
		assertTrue(after.after(updateDate));
	}
	
	@Test
	public void isPersistedTest() {
		AbstractEntityImpl abstractEntity = new AbstractEntityImpl();
		
		assertFalse(abstractEntity.isPersisted());
		abstractEntity.setCreationDate(new Date());
		assertTrue(abstractEntity.isPersisted());
	}
	
	@Test
	public void toStringTest() {
		AbstractEntityImpl abstractEntity = new AbstractEntityImpl();
		
		assertNull(abstractEntity.toString());
		abstractEntity.setId(1L);
		assertEquals("1", abstractEntity.toString());
	}
}
