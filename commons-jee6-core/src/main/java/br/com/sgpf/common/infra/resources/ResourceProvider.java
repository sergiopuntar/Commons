package br.com.sgpf.common.infra.resources;

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
	
	private static String beanManagerName = "java:comp/BeanManager";

	private ResourceProvider() {
		super();
	}

	/**
	 * Altera o nome usado para lookup do CDI BeanManager.<br>
	 * O nome padrão é: "java:comp/BeanManager".
	 * 
	 * @param beanManagerName Nome para lookup do BeanManager
	 */
	public static void setBeanManagerName(String beanManagerName) {
		ResourceProvider.beanManagerName = beanManagerName;
	}

	/**
	 * Recupera o CDI Bean Manager.
	 * 
	 * @return CDI Bean Manager
	 */
	public static BeanManager getBeanManager() {
		try {
			System.out.println(beanManagerName);
			return (BeanManager) InitialContext.doLookup(beanManagerName);
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
	public static <T> T getContextualReference(Class<T> clazz) {
		BeanManager beanManager = getBeanManager();
		Set<Bean<? extends Object>> beans = beanManager.getBeans(clazz);
		Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
		CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
		
		return (T) beanManager.getReference(bean, clazz, ctx);
	}
}
