package br.com.sgpf.common.infra.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import javax.inject.Inject;

import br.com.sgpf.common.infra.resources.ResourceProvider;

/**
 * Classe de recuperação das mensages da aplicação.
 */
public class ApplicationMessages {
	
	@Inject
	private ResourceProvider resourceProvider;
	
	@Inject
	private MessageBundleProvider messageBundleProvider;

	public ApplicationMessages() {
		super();
	}

	/**
	 * Recupera uma mensagem a partir da uma chave pré-definida, aplicando os parâmetros definidos.
	 * 
	 * @param key Chave pré-definida da mensagem.
	 * @param params Parâmetros da mensagem
	 * @return Mensagem recuperada
	 */
	public String getMessage(MessageKey key, Object... params) {
		return getMessage(key.getName(), params);
	}
	
	/**
	 * Recupera uma mensagem a partir da sua chave, aplicando os parâmetros definidos.
	 * 
	 * @param key Chave da mensagem.
	 * @param params Parâmetros da mensagem
	 * @return Mensagem recuperada
	 */
	public String getMessage(String key, Object... params) {
		return MessageFormat.format(getPlainMessage(key), params);
	}

	/**
	 * Recupera a mensagem plana, na forma que foi definida no arquivo de propriedades, sem
	 * definição de nenhum parâmetro.
	 * 
	 * @param key Chave da mensagem.
	 * @return Mensagem recuperada
	 */
	private String getPlainMessage(String key) {
		return messageBundleProvider.getMessageBundle(resourceProvider.getContextualReference(Locale.class)).getString(key);
	}
}
