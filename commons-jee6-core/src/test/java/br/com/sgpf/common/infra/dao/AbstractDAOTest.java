/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import br.com.sgpf.common.domain.entity.AbstractEntityImpl;
import br.com.sgpf.common.domain.entity.Entity;
import br.com.sgpf.common.infra.exception.DAOException;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDAOTest {

	@Mock
	private EntityManager em;
	
	@InjectMocks
	private AbstractDAO<AbstractEntityImpl, Long> dao = new AbstractEntityImplDAO();
	
	@Before
	public void before() {
		dao.postConstruct();
		assertEquals(AbstractEntityImpl.class, dao.clazz);
	}
	
	@Test(expected = NullPointerException.class)
	public void findNullIdTest() {
		dao.find(null);
	}
	
	@Test
	public void findTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		when(em.find(AbstractEntityImpl.class, entity.getId())).thenReturn(entity);
		
		assertEquals(entity, dao.find(1L));
	}
	
	@Test
	public void findAllTest() {
		AbstractEntityImpl entity1 = new AbstractEntityImpl();
		entity1.setId(1L);
		AbstractEntityImpl entity2 = new AbstractEntityImpl();
		entity2.setId(2L);
		ArrayList<AbstractEntityImpl> entities = Lists.newArrayList(entity1, entity2);
		
		Query query = mock(Query.class);
		when(query.getResultList()).thenReturn(entities);
		
		when(em.createQuery("from " + AbstractEntityImpl.class.getName())).thenReturn(query);
		
		assertEquals(entities, dao.findAll());
	}
	
	@Test(expected = NullPointerException.class)
	public void persistNullEntityTest() {
		dao.persist(null);
	}
	
	@Test
	public void persistTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		dao.persist(entity);
		
		verify(em).persist(entity);
		verify(em).flush();
	}
	
	@Test(expected = NullPointerException.class)
	public void mergeNullEntityTest() {
		dao.merge(null);
	}
	
	@Test
	public void mergeTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		AbstractEntityImpl mergedEntity = new AbstractEntityImpl();
		mergedEntity.setId(1L);
		mergedEntity.setVersion(0L);
		
		when(em.merge(entity)).thenReturn(mergedEntity);
		
		assertEquals(mergedEntity, dao.merge(entity));
		
		verify(em).merge(entity);
		verify(em).flush();
	}
	
	@Test(expected = NullPointerException.class)
	public void refreshNullEntityTest() {
		dao.refresh(null);
	}
	
	@Test
	public void refreshTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		dao.refresh(entity);
		
		verify(em).refresh(entity);
	}
	
	@Test(expected = NullPointerException.class)
	public void removeNullEntityTest() {
		dao.remove(null);
	}
	
	@Test
	public void removeInContextEntityTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		when(em.contains(entity)).thenReturn(true);
		
		dao.remove(entity);
		
		verify(em).remove(entity);
		verify(em).flush();
	}
	
	@Test
	public void removeOutContextEntityTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		AbstractEntityImpl mergedEntity = new AbstractEntityImpl();
		mergedEntity.setId(1L);
		mergedEntity.setVersion(0L);
		
		when(em.contains(entity)).thenReturn(false);
		when(em.merge(entity)).thenReturn(mergedEntity);
		
		dao.remove(entity);
		
		verify(em).merge(entity);
		verify(em).remove(mergedEntity);
		verify(em).flush();
	}
	
	@Test(expected = NullPointerException.class)
	@SuppressWarnings("rawtypes")
	public void firstResultNullResultTypeTest() {
		List<Entity> entities = Lists.newArrayList();
		dao.firstResult(entities, null);
	}
	
	@Test(expected = DAOException.class)
	public void firstResultWrongTypeTest() {
		Object entity1 = new Object();
		Object entity2 = new Object();
		
		dao.firstResult(Lists.newArrayList(entity1, entity2), AbstractEntityImpl.class);
	}
	
	@Test
	public void firstResultEmptyListTest() {
		assertNull(dao.firstResult(null, AbstractEntityImpl.class));
		assertNull(dao.firstResult(Lists.newArrayList(), AbstractEntityImpl.class));
	}
	
	@Test
	public void firstResultTest() {
		AbstractEntityImpl entity1 = new AbstractEntityImpl();
		entity1.setId(1L);
		AbstractEntityImpl entity2 = new AbstractEntityImpl();
		entity2.setId(2L);
		
		assertEquals(entity1, dao.firstResult(Lists.newArrayList(entity1, entity2), AbstractEntityImpl.class));
	}
}
