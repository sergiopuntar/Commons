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

	private List<PojoClass> pojoClasses;
	
	public PojoTester(String packageName) {
		pojoClasses = PojoClassFactory.getPojoClasses(packageName);
	}
	
	public PojoTester(Class<?> clazz) {
		pojoClasses = Lists.newArrayList(PojoClassFactory.getPojoClass(clazz));
	}
	
	public void pojoTest(Rule[] rules, Tester[] testers) {
		Validator validator = ValidatorBuilder.create().with(rules).with(testers).build();
		validator.validate(pojoClasses);
	}
	
	public void loosePojoStructAndBehaviourTest() {
		pojoTest(new Rule[] {
					// Make sure we have a getter and setter
					new GetterMustExistRule(),
					new SetterMustExistRule(),
					// Static fields must be final
					new NoStaticExceptFinalRule(),
					// Serializable must have serialVersionUID
					new SerializableMustHaveSerialVersionUIDRule(),
					// Don't shadow parent's field names.
					new NoFieldShadowingRule(),
					// What about public fields, use one of the following rules allow them only if they are static and final.
					new NoPublicFieldsExceptStaticFinalRule(),
					// Finally, what if you are testing your Testing code? Make sure your tests are properly named
					new TestClassMustBeProperlyNamedRule()
				},
				new Tester[] {
					// Make sure our setters and getters are behaving as expected.
					new SetterTester(),
					new GetterTester()
				}
		);
	}

	public void strictPojoStructAndBehaviourTest() {
		pojoTest(new Rule[] {
					// Make sure we have a getter and setter
					new GetterMustExistRule(),
					new SetterMustExistRule(),
					// We don't want any primitives in our Pojos.
					new NoPrimitivesRule(),
					// Pojo's should stay simple, don't allow nested classes, anonymous or declared.
					new NoNestedClassRule(),
					// Static fields must be final
					new NoStaticExceptFinalRule(),
					// Serializable must have serialVersionUID
					new SerializableMustHaveSerialVersionUIDRule(),
					// Don't shadow parent's field names.
					new NoFieldShadowingRule(),
					// What about public fields, use one of the following rules allow them only if they are static and final.
					new NoPublicFieldsExceptStaticFinalRule(),
					// Finally, what if you are testing your Testing code? Make sure your tests are properly named
					new TestClassMustBeProperlyNamedRule()
				},
				new Tester[] {
					// Make sure our setters and getters are behaving as expected.
					new SetterTester(),
					new GetterTester(),
					// We don't want any default values to any fields - unless they are declared final or are primitive.
					new DefaultValuesNullTester()
				}
		);
	}

	public void loosePojoBehaviourTest() {
		pojoTest(new Rule[] {},
				new Tester[] {
					// Make sure our setters and getters are behaving as expected.
					new SetterTester(),
					new GetterTester(),
					// We don't want any default values to any fields - unless they are declared final or are primitive.
					new DefaultValuesNullTester()
				}
		);
	}

	public void strictPojoBehaviourTest() {
		pojoTest(new Rule[] {},
				new Tester[] {
					// Make sure our setters and getters are behaving as expected.
					new SetterTester(),
					new GetterTester(),
					// We don't want any default values to any fields - unless they are declared final or are primitive.
					new DefaultValuesNullTester()
				}
		);
	}
}
