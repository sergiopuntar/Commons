package br.com.sgpf.common.infra.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.com.sgpf.common.infra.resources.ResourceProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceProvider.class)
public class ApplicationMessagesTest {

	@Test
	public void getMessage() {
		PowerMockito.mockStatic(ResourceProvider.class);
		Mockito.when(ResourceProvider.getContextualReference(Locale.class)).thenReturn(Locale.US);
		
		assertEquals("value", ApplicationMessages.getMessage(MessageKeyImpl.TEST));
		assertEquals("param: paramValue", ApplicationMessages.getMessage(MessageKeyImpl.TEST_PARAM, "paramValue"));
		
		assertEquals("value", ApplicationMessages.getMessage("test"));
		assertEquals("param: paramValue", ApplicationMessages.getMessage("test.param", "paramValue"));
	}
	
	enum MessageKeyImpl implements MessageKey {
		TEST,
		TEST_PARAM;

		@Override
		public String getName() {
			return name().replaceAll("_", ".").toLowerCase();
		}
	}
}
