package br.com.sgpf.common.infra.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import br.com.sgpf.common.infra.i18n.MessageBundleProvider;
import br.com.sgpf.common.infra.resources.ResourceProvider;

/**
 * Classe de recuperação das mensages da aplicação.
 */
public class ApplicationMessages {

	/**
	 * Recupera uma mensagem a partir da uma chave pré-definida, aplicando os
	 * parâmetros definidos.
	 * 
	 * @param key Chave pré-definida da mensagem.
	 * @param params Parâmetros da mensagem
	 * @return Mensagem recuperada
	 */
	public static String getMessage(MessageKey key, Object... params) {
		return getMessage(key.getName(), params);
	}
	
	/**
	 * Recupera uma mensagem a partir da sua chave, aplicando os parâmetros
	 * definidos.
	 * 
	 * @param key Chave da mensagem.
	 * @param params Parâmetros da mensagem
	 * @return Mensagem recuperada
	 */
	public static String getMessage(String key, Object... params) {
		return MessageFormat.format(getPlainMessage(key), params);
	}

	/**
	 * Recupera a mensagem plana, na forma que foi definida no arquivo de
	 * propriedades, sem definição de nenhum parâmetro.
	 * 
	 * @param key Chave da mensagem.
	 * @return Mensagem recuperada
	 */
	private static String getPlainMessage(String key) {
		return MessageBundleProvider.getMessageBundle(ResourceProvider.getContextualReference(Locale.class)).getString(key);
	}
}
