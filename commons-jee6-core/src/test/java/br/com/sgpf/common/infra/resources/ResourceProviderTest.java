/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.resources;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
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
		BeanManager beanManager = Mockito.mock(BeanManager.class);
		PowerMockito.mockStatic(InitialContext.class);
		Mockito.when(InitialContext.doLookup(ResourceProvider.DEFAULT_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider();
		assertEquals(beanManager, resourceProvider.getBeanManager());
	}
	
	@Test(expected = NullPointerException.class)
	public void setBeanManagerNullNameConstructorTest() throws NamingException {
		new ResourceProvider(null);
	}
	
	@Test
	public void setBeanManagerNameConstructorTest() throws NamingException {
		BeanManager beanManager = Mockito.mock(BeanManager.class);
		PowerMockito.mockStatic(InitialContext.class);
		Mockito.when(InitialContext.doLookup(TEST_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider(TEST_BEAN_MANAGER_NAME);
		assertEquals(beanManager, resourceProvider.getBeanManager());
	}
	
	@Test
	public void getContextualReferenceTest() throws NamingException {
		Object object = new Object();
		Set<Bean<? extends Object>> beans = new HashSet<>();
		Bean<?> bean = Mockito.mock(Bean.class);
		beans.add(bean);
		CreationalContext<?> ctx = Mockito.mock(CreationalContext.class);
		
		BeanManager beanManager = Mockito.mock(BeanManager.class);
		Mockito.when(beanManager.getBeans(Object.class)).thenReturn(beans);
		Mockito.doReturn(bean).when(beanManager).resolve(beans);
		Mockito.doReturn(ctx).when(beanManager).createCreationalContext(bean);
		Mockito.doReturn(object).when(beanManager).getReference(bean, Object.class, ctx);
		
		PowerMockito.mockStatic(InitialContext.class);
		Mockito.when(InitialContext.doLookup(ResourceProvider.DEFAULT_BEAN_MANAGER_NAME)).thenReturn(beanManager);
		
		ResourceProvider resourceProvider = new ResourceProvider();
		assertEquals(object, resourceProvider.getContextualReference(Object.class));
	}
}
