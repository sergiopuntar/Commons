package br.com.sgpf.common.util;

/**
 * Interface para objetos que sobrescrevem o método equals usando a abordagem CanEqual.<br>
 * http://www.artima.com/lejava/articles/equality.html
 */
public interface CanEqual {
	
	/**
	 * Indica se um determinado objeto pode ser utilizado para comparação com este.
	 * 
	 * @param obj Objeto que se deseja comparar
	 * @return Flag indicando se o objeto pode ser comparado a este
	 */
	boolean canEqual(Object obj);
}
