package br.com.sgpf.common.test.pojo.rules;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import com.openpojo.log.utils.MessageFormatter;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.Rule;

/**
 * Implementação customizada da regra
 * {@link com.openpojo.validation.rule.impl.NoFieldShadowingRule}.
 * <br>
 * Na implementação original, o método {@link #isSerializable(PojoField, PojoClass)} considera que
 * a classe é serializável somente se ela implementa diretamente a interface Serializable.<br>
 * Essa implementação considera que a classe pode herdar a interface de uma super classe.
 */
public class CustomNoFieldShadowingRule implements Rule {
	private static final String SERIAL_VERSION_UID_FIELD_NAME = "serialVersionUID";
	private static final Class<?> SERIAL_VERSION_UID_FIELD_TYPE = long.class;
	private List<String> fieldNamesToSkip;

	public CustomNoFieldShadowingRule(String... fieldNamesToSkip) {
		this.fieldNamesToSkip = Arrays.asList(fieldNamesToSkip);
	}

	public void evaluate(final PojoClass pojoClass) {
		final List<PojoField> parentPojoFields = new LinkedList<>();
		PojoClass parentPojoClass = pojoClass.getSuperClass();
		while (parentPojoClass != null) {
			parentPojoFields.addAll(parentPojoClass.getPojoFields());
			parentPojoClass = parentPojoClass.getSuperClass();
		}

		final List<PojoField> childPojoFields = pojoClass.getPojoFields();
		for (final PojoField childPojoField : childPojoFields) {
			if (!childPojoField.isSynthetic() && !isSerializable(childPojoField, pojoClass) && !inSkipList(childPojoField) && contains(childPojoField.getName(), parentPojoFields)) {
				Affirm.fail(MessageFormatter.format("Field=[{0}] shadows field with the same name in parent class=[{1}]", childPojoField, parentPojoFields));
			}
		}
	}

	private boolean inSkipList(PojoField field) {
		for (String skipEntry : fieldNamesToSkip) {
			if (skipEntry.equals(field.getName())) {
				return true;
			}
		}

		return false;
	}

	private boolean isSerializable(PojoField field, PojoClass pojoClass) {
		return field.getName().equals(SERIAL_VERSION_UID_FIELD_NAME)
				&& field.getType().equals(SERIAL_VERSION_UID_FIELD_TYPE)
				&& ClassUtils.getAllInterfaces(pojoClass.getClazz()).contains(Serializable.class);
	}

	private boolean contains(final String fieldName, final List<PojoField> pojoFields) {
		for (final PojoField pojoField : pojoFields) {
			if (pojoField.getName().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}
}
