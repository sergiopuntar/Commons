package br.com.sgpf.common.infra.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;
import org.mockito.Mock;

public class MessageBundleProviderTest {
	
	@Mock
	private ResourceBundle defaultResourceBundle;
	
	@Mock
	private ResourceBundle usResourceBundle;
	
	@Test
	public void getMessageBundle() {
		assertEquals("value", MessageBundleProvider.getMessageBundle().getString("test"));
		assertEquals("value", MessageBundleProvider.getMessageBundle(null).getString("test"));
		assertEquals("valor", MessageBundleProvider.getMessageBundle(new Locale("pt", "BR")).getString("test"));
	}
}
