package br.com.sgpf.common.infra.resources;

import static br.com.sgpf.common.infra.resources.Constants.ERROR_NULL_ARGUMENT;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.sgpf.common.infra.exception.InfraestructureFatalException;

/**
 * Classe que provê recursos do sistema estaticamente.
 */
public class ResourceProvider {
	private static final String ERROR_LOOKUP_BEAN_MANAGER = "Não foi possível encontrar o registro do Bean Manager no JNDI.";
	
	public static final String DEFAULT_BEAN_MANAGER_NAME = "java:comp/BeanManager";
	
	private String beanManagerName;

	/**
	 * Constrói um Resource Provider que usa o nome de lookup padrão para o CDI BeanManager:
	 * {@link #DEFAULT_BEAN_MANAGER_NAME}.
	 * 
	 * @param beanManagerName Nome para lookup do BeanManager
	 */
	public ResourceProvider() {
		this(DEFAULT_BEAN_MANAGER_NAME);
	}

	/**
	 * Constrói um Resource Provider que usa um nome de lookup específico para o CDI BeanManager.
	 * 
	 * @param beanManagerName Nome de lookup do BeanManager
	 */
	public ResourceProvider(String beanManagerName) {
		super();
		this.beanManagerName = checkNotNull(beanManagerName, ERROR_NULL_ARGUMENT, "beanManagerName");
	}

	/**
	 * Recupera o CDI Bean Manager.
	 * 
	 * @return CDI Bean Manager
	 */
	public BeanManager getBeanManager() {
		try {
			return InitialContext.doLookup(beanManagerName);
		} catch (NamingException e) {
			throw new InfraestructureFatalException(ERROR_LOOKUP_BEAN_MANAGER, e);
		}
	}
	
	/**
	 * Recupera uma referência contextual de um CDI bean.
	 * 
	 * @param clazz Classe do CDI bean
	 * @return Referência contextual da classe
	 */
	@SuppressWarnings("unchecked")
	public <T> T getContextualReference(Class<T> clazz) {
		BeanManager beanManager = getBeanManager();
		Set<Bean<? extends Object>> beans = beanManager.getBeans(clazz);
		Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
		CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
		
		return (T) beanManager.getReference(bean, clazz, ctx);
	}
}
