package br.com.sgpf.common.test;

import java.util.List;

import com.google.common.collect.Lists;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.rule.impl.TestClassMustBeProperlyNamedRule;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.DefaultValuesNullTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

/**
 * Classe utilitária para teste de pojos.
 */
public class PojoTester {
	
	/**
	 * Array de regras de estrutura estritas.
	 * <ul>
	 *    <li>Devem existir Getter e Setter para todas as propriedades;</li>
	 *    <li>Nenhum campo deve possuir tipo primitivo;</li>
	 *    <li>Não devem existir classes aninhadas;</li>
	 *    <li>Campos <i>static</i> devem ser <i>final</i>;</li>
	 *    <li>Classes <i>Serializable</i> devem possuir <i>serialVersionUID</i> definido;</li>
	 *    <li>Propriedades de classes superiores não devem ser redefinidas (field shadowing);</li>
	 *    <li>Campos <i>public</i> devem ser <i>static final</i>;</li>
	 *    <li>Classes de teste devem ser nomeadas de acordo com as classes testadas.</li>
	 * </ul>
	 */
	public static final Rule[] STRICT_RULES = new Rule[] {
			new GetterMustExistRule(),
			new SetterMustExistRule(),
			new NoPrimitivesRule(),
			new NoNestedClassRule(),
			new NoStaticExceptFinalRule(),
			new SerializableMustHaveSerialVersionUIDRule(),
			new NoFieldShadowingRule(),
			new NoPublicFieldsExceptStaticFinalRule(),
			new TestClassMustBeProperlyNamedRule()
		};

	/**
	 * Array de testes de comportamento estritas.
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter;</li>
	 *    <li>Nenhum campo deve possuir valor padrão nulo, a não ser que seja primitivo ou <i>final</i>.</li>
	 * </ul>
	 */
	public static final Tester[] STRICT_TESTERS = new Tester[] {
			new SetterTester(),
			new GetterTester(),
			new DefaultValuesNullTester()
		};
	
	/**
	 * Array de regras de estrutura frouxas.
	 * <ul>
	 *    <li>Devem existir Getter e Setter para todas as propriedades;</li>
	 *    <li>Não devem existir classes aninhadas;</li>
	 *    <li>Campos <i>static</i> devem ser <i>final</i>;</li>
	 *    <li>Classes <i>Serializable</i> devem possuir <i>serialVersionUID</i> definido;</li>
	 *    <li>Propriedades de classes superiores não devem ser redefinidas (field shadowing);</li>
	 *    <li>Campos <i>public</i> devem ser <i>static final</i>.</li>
	 * </ul>
	 */
	public static final Rule[] LOOSE_RULES = new Rule[] {
			new GetterMustExistRule(),
			new SetterMustExistRule(),
			new NoNestedClassRule(),
			new NoStaticExceptFinalRule(),
			new SerializableMustHaveSerialVersionUIDRule(),
			new NoFieldShadowingRule(),
			new NoPublicFieldsExceptStaticFinalRule()
		};
	
	/**
	 * Array de testes de comportamento frouxas.
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter.</li>
	 * </ul>
	 */
	public static final Tester[] LOOSE_TESTERS = new Tester[] {
			new SetterTester(),
			new GetterTester()
		};
	
	/**
	 * Array de regras de estrutura para pojos com conteúdo imutável.
	 * <ul>
	 *    <li>Devem existir Getter para todas as propriedades;</li>
	 *    <li>Nenhum campo deve possuir tipo primitivo;</li>
	 *    <li>Não devem existir classes aninhadas;</li>
	 *    <li>Campos <i>static</i> devem ser <i>final</i>;</li>
	 *    <li>Classes <i>Serializable</i> devem possuir <i>serialVersionUID</i> definido;</li>
	 *    <li>Propriedades de classes superiores não devem ser redefinidas (field shadowing);</li>
	 *    <li>Campos <i>public</i> devem ser <i>static final</i>;</li>
	 *    <li>Classes de teste devem ser nomeadas de acordo com as classes testadas.</li>
	 * </ul>
	 */
	public static final Rule[] UNMUTABLE_RULES = new Rule[] {
			new GetterMustExistRule(),
			new NoPrimitivesRule(),
			new NoNestedClassRule(),
			new NoStaticExceptFinalRule(),
			new SerializableMustHaveSerialVersionUIDRule(),
			new NoFieldShadowingRule(),
			new NoPublicFieldsExceptStaticFinalRule(),
			new TestClassMustBeProperlyNamedRule()
		};
	
	/**
	 * Array de testes de comportamento para pojos com conteúdo imutável.
	 * <ul>
	 *    <li>Getter;</li>
	 * </ul>
	 */
	public static final Tester[] UNMUTABLE_TESTERS = new Tester[] {
			new GetterTester()
		};
	
	private List<PojoClass> pojoClasses;
	
	/**
	 * Cria um Pojo tester para todas as classes em um pacote.
	 * 
	 * @param packageName Nome do pacote
	 */
	public PojoTester(String packageName) {
		super();
		pojoClasses = PojoClassFactory.getPojoClasses(packageName);
	}
	
	/**
	 * Cria um Pojo tester para uma classe.
	 * 
	 * @param clazz Classe a ser testada
	 */
	public PojoTester(Class<?> clazz) {
		super();
		pojoClasses = Lists.newArrayList(PojoClassFactory.getPojoClass(clazz));
	}
	
	/**
	 * Valida um Pojo utilizando determinadas regras de estrutura e testes de comportamento. 
	 * 
	 * @param rules Regras de estrutura a serem aplicadas
	 * @param testers Testes de comportamento a serem executados
	 */
	public void validatePojo(Rule[] rules, Tester[] testers) {
		Validator validator = ValidatorBuilder.create().with(rules).with(testers).build();
		validator.validate(pojoClasses);
	}
	
	/**
	 * Valida um Pojo utilizando determinados testes de comportamento. 
	 * 
	 * @param testers Testes de comportamento a serem executados
	 */
	public void validatePojo(Tester[] testers) {
		validatePojo(new Rule[] {},	testers);
	}

	/**
	 * Valida a estrutura e comportamento de um Pojo de modo estrito.<br>
	 * <b>Regras de estrutura aplicadas:</b>
	 * <ul>
	 *    <li>Devem existir Getter e Setter para todas as propriedades;</li>
	 *    <li>Nenhum campo deve possuir tipo primitivo;</li>
	 *    <li>Não devem existir classes aninhadas;</li>
	 *    <li>Campos <i>static</i> devem ser <i>final</i>;</li>
	 *    <li>Classes <i>Serializable</i> devem possuir <i>serialVersionUID</i> definido;</li>
	 *    <li>Propriedades de classes superiores não devem ser redefinidas (field shadowing);</li>
	 *    <li>Campos <i>public</i> devem ser <i>static final</i>;</li>
	 *    <li>Classes de teste devem ser nomeadas de acordo com as classes testadas.</li>
	 * </ul>
	 * <b>Testes de comportamento executados:</b>
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter;</li>
	 *    <li>Nenhum campo deve possuir valor padrão nulo, a não ser que seja primitivo ou <i>final</i>.</li>
	 * </ul>
	 */
	public void strictValidatePojoStructAndBehaviour() {
		validatePojo(STRICT_RULES, STRICT_TESTERS);
	}
	
	/**
	 * Valida a estrutura e comportamento de um Pojo de modo frouxo.<br>
	 * <b>Regras de estrutura aplicadas:</b>
	 * <ul>
	 *    <li>Devem existir Getter e Setter para todas as propriedades;</li>
	 *    <li>Campos <i>static</i> devem ser <i>final</i>;</li>
	 *    <li>Classes <i>Serializable</i> devem possuir <i>serialVersionUID</i> definido;</li>
	 *    <li>Propriedades de classes superiores não devem ser redefinidas (field shadowing);</li>
	 *    <li>Campos <i>public</i> devem ser <i>static final</i>.</li>
	 * </ul>
	 * <b>Testes de comportamento executados:</b>
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter.</li>
	 * </ul>
	 */
	public void looseValidatePojoStructAndBehaviour() {
		validatePojo(LOOSE_RULES, LOOSE_TESTERS);
	}

	/**
	 * Valida o comportamento de um Pojo de modo estrito.<br>
	 * <b>Testes de comportamento executados:</b>
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter;</li>
	 *    <li>Nenhum campo deve possuir valor padrão nulo, a não ser que seja primitivo oi <i>final</i>.</li>
	 * </ul>
	 */
	public void strictValidatePojoBehaviour() {
		validatePojo(STRICT_TESTERS);
	}

	/**
	 * Valida o comportamento de um Pojo de modo frouxo.<br>
	 * <b>Testes de comportamento executados:</b>
	 * <ul>
	 *    <li>Getter;</li>
	 *    <li>Setter.</li>
	 * </ul>
	 */
	public void looseValidatePojoBehaviour() {
		validatePojo(LOOSE_TESTERS);
	}
}
