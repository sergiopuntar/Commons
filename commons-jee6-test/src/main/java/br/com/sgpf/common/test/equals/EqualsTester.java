/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.test.equals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * Classe utilitária para teste dos métodos equals e hashcode.
 * 
 * @param<T> Tipo da classe testada
 */
public class EqualsTester<T> {

	private EqualsVerifier<T> equalsVerifier;

	/**
	 * Cria um testador de igualdade para uma classe.
	 * 
	 * @param clazz Classe a ser testada
	 * @param hasSuperClazz Flag indicando se a classe possui uma superclasse
	 */
	public EqualsTester(Class<T> clazz, boolean hasSuperClazz) {
		super();
		equalsVerifier = EqualsVerifier.forClass(clazz).
				suppress(Warning.NONFINAL_FIELDS);
		
		if (hasSuperClazz) {
			equalsVerifier.withRedefinedSuperclass();
		}
	}
	
	/**
	 * Cria um testador de igualdade para uma classe definindo os campos que devem ser considerados
	 * na verificação igualdade.
	 * 
	 * @param clazz Classe a ser testada
	 * @param hasSuperClazz Flag indicando se a classe possui uma superclasse
	 * @param withFields Nomes dos campos que devem ser considerados na verificação igualdade.
	 */
	public EqualsTester(Class<T> clazz, boolean hasSuperClazz, String... withFields) {
		this(clazz, hasSuperClazz);
		equalsVerifier.withOnlyTheseFields(withFields);
	}	
	
	/**
	 * Cria um testador de igualdade para uma classe que possui uma subclasse.
	 * 
	 * @param clazz Classe a ser testada
	 * @param hasSuperClazz Flag indicando se a classe possui uma superclasse
	 * @param subClazz Subclasse da classe a ser testada
	 */
	public EqualsTester(Class<T> clazz, boolean hasSuperClazz, Class<? extends T> subClazz) {
		this(clazz, hasSuperClazz);
		equalsVerifier.withRedefinedSubclass(subClazz);
	}
	
	/**
	 * Cria um testador de igualdade para uma classe que possui uma subclasse definindo os campos
	 * que devem ser considerados na verificação igualdade.
	 * 
	 * @param clazz Classe a ser testada
	 * @param hasSuperClazz Flag indicando se a classe possui uma superclasse
	 * @param subClazz Subclasse da classe a ser testada
	 * @param withFields Nomes dos campos que devem ser considerados na verificação igualdade.
	 */
	public EqualsTester(Class<T> clazz, boolean hasSuperClazz, Class<? extends T> subClazz, String... withFields) {
		this(clazz, hasSuperClazz, subClazz);
		equalsVerifier.withOnlyTheseFields(withFields);
	}	
	
	/**
	 * Executa a validação de igqualdade.
	 */
	public void validate() {
		equalsVerifier.verify();
	}
}
