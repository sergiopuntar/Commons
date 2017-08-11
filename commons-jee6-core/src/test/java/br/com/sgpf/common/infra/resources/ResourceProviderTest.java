/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.com.sgpf.common.infra.exception.InfraestructureFatalException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InitialContext.class)
public class ResourceProviderTest {
	
	private static final String TEST_BEAN_MANAGER_NAME = "java:test/BeanManager";

	@Test(expected = InfraestructureFatalException.class)
	public void getBeanManagerErrorTest() throws NamingException {
		ResourceProvider resourceProvider = new ResourceProvider();
		resourceProvider.getBeanManager();
	}

	@Test
	public void getBeanManagerTest() throws NamingException {
		BeanManager beanManager = mock(BeanManager.class);
		mockStatic(InitialContext.class);
		when(InitialContext.doLookup(ResourceProvider.DEFAULT_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider();
		assertEquals(beanManager, resourceProvider.getBeanManager());
	}
	
	@Test(expected = NullPointerException.class)
	public void setBeanManagerNullNameConstructorTest() throws NamingException {
		new ResourceProvider(null);
	}
	
	@Test
	public void setBeanManagerNameConstructorTest() throws NamingException {
		BeanManager beanManager = mock(BeanManager.class);
		mockStatic(InitialContext.class);
		when(InitialContext.doLookup(TEST_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider(TEST_BEAN_MANAGER_NAME);
		assertEquals(beanManager, resourceProvider.getBeanManager());
	}
	
	@Test
	public void getContextualReferenceTest() throws NamingException {
		Object object = new Object();
		Set<Bean<? extends Object>> beans = new HashSet<>();
		Bean<?> bean = mock(Bean.class);
		beans.add(bean);
		CreationalContext<?> ctx = mock(CreationalContext.class);
		
		BeanManager beanManager = mock(BeanManager.class);
		when(beanManager.getBeans(Object.class)).thenReturn(beans);
		doReturn(bean).when(beanManager).resolve(beans);
		doReturn(ctx).when(beanManager).createCreationalContext(bean);
		doReturn(object).when(beanManager).getReference(bean, Object.class, ctx);
		
		mockStatic(InitialContext.class);
		when(InitialContext.doLookup(ResourceProvider.DEFAULT_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider();
		assertEquals(object, resourceProvider.getContextualReference(Object.class));
	}
}
