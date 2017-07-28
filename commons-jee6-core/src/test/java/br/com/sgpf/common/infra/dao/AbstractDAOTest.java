package br.com.sgpf.common.infra.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import br.com.sgpf.common.domain.entity.AbstractEntityImpl;
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
	
	@Test
	public void findTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		Mockito.when(em.find(AbstractEntityImpl.class, entity.getId())).thenReturn(entity);
		
		assertEquals(entity, dao.find(1L));
	}
	
	@Test
	public void findAllTest() {
		AbstractEntityImpl entity1 = new AbstractEntityImpl();
		entity1.setId(1L);
		AbstractEntityImpl entity2 = new AbstractEntityImpl();
		entity2.setId(2L);
		ArrayList<AbstractEntityImpl> entities = Lists.newArrayList(entity1, entity2);
		
		Query query = Mockito.mock(Query.class);
		Mockito.when(query.getResultList()).thenReturn(entities);
		
		Mockito.when(em.createQuery("from " + AbstractEntityImpl.class.getName())).thenReturn(query);
		
		assertEquals(entities, dao.findAll());
	}
	
	@Test
	public void persistTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		dao.persist(entity);
		
		Mockito.verify(em).persist(entity);
		Mockito.verify(em).flush();
	}
	
	@Test
	public void mergeTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		AbstractEntityImpl mergedEntity = new AbstractEntityImpl();
		mergedEntity.setId(1L);
		mergedEntity.setVersion(0L);
		
		Mockito.when(em.merge(entity)).thenReturn(mergedEntity);
		
		assertEquals(mergedEntity, dao.merge(entity));
		
		Mockito.verify(em).merge(entity);
		Mockito.verify(em).flush();
	}
	
	@Test
	public void refreshTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		dao.refresh(entity);
		
		Mockito.verify(em).refresh(entity);
	}
	
	@Test
	public void removeInContextEntityTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		Mockito.when(em.contains(entity)).thenReturn(true);
		
		dao.remove(entity);
		
		Mockito.verify(em).remove(entity);
		Mockito.verify(em).flush();
	}
	
	@Test
	public void removeOutContextEntityTest() {
		AbstractEntityImpl entity = new AbstractEntityImpl();
		entity.setId(1L);
		
		AbstractEntityImpl mergedEntity = new AbstractEntityImpl();
		mergedEntity.setId(1L);
		mergedEntity.setVersion(0L);
		
		Mockito.when(em.contains(entity)).thenReturn(false);
		Mockito.when(em.merge(entity)).thenReturn(mergedEntity);
		
		dao.remove(entity);
		
		Mockito.verify(em).merge(entity);
		Mockito.verify(em).remove(mergedEntity);
		Mockito.verify(em).flush();
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
