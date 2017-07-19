package br.com.sgpf.common.infra.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe que provê Message Bundles a partir de um locale.
 */
public class MessageBundleProvider {

	/**
	 * Nome basse do arquivo de mensagens
	 */
	private static final String MESSAGE_BUNDLE_BASE_NAME = "i18n.messages";
	
	/**
	 * Recupera um Message Bundle básico, sem especificar um local.
	 * 
	 * @return Message Bundle básico
	 */
	public static ResourceBundle getMessageBundle() {
		return getMessageBundle(null);
	}
	
	/**
	 * Recupera um Message Bundle para um local específico.
	 * 
	 * @param locale Local do Message Bundle.
	 * @return Message Bundle do local.
	 */
	public static ResourceBundle getMessageBundle(Locale locale) {
		return locale == null ? ResourceBundle.getBundle(MESSAGE_BUNDLE_BASE_NAME) : ResourceBundle.getBundle(MESSAGE_BUNDLE_BASE_NAME, locale);
	}
}
