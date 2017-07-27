package br.com.sgpf.common.domain.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.Lists;

public class EntityUtilTest {

	@Test
	public void generateUUIDTest() {
		assertNotNull(UUID.fromString(EntityUtil.generateUUID()));
	}
	
	@Test
	public void recoverEntityTest() {
		AbstractEntityImpl entity1 = new AbstractEntityImpl();
		entity1.setId(1L);
		AbstractEntityImpl entity2 = new AbstractEntityImpl();
		entity2.setId(2L);
		AbstractEntityImpl entity3 = new AbstractEntityImpl();
		entity3.setId(3L);
		
		assertEquals(entity2, EntityUtil.recoverEntity(Lists.newArrayList(entity1, entity2, entity3), Long.valueOf(2L)));
		assertNull(EntityUtil.recoverEntity(Lists.newArrayList(entity1, entity3), Long.valueOf(2L)));
	}
}
