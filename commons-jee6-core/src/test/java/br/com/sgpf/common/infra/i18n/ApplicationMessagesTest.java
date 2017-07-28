package br.com.sgpf.common.infra.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import br.com.sgpf.common.infra.resources.ResourceProvider;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationMessagesTest {

	@Mock
	private ResourceProvider resourceProvider;
	
	@InjectMocks
	private ApplicationMessages applicationMessages = new ApplicationMessages();
	
	@Test
	public void getMessage() {
		Mockito.when(resourceProvider.getContextualReference(Locale.class)).thenReturn(Locale.US);
		
		assertEquals("value", applicationMessages.getMessage(MessageKeyImpl.TEST));
		assertEquals("param: paramValue", applicationMessages.getMessage(MessageKeyImpl.TEST_PARAM, "paramValue"));
		
		assertEquals("value", applicationMessages.getMessage("test"));
		assertEquals("param: paramValue", applicationMessages.getMessage("test.param", "paramValue"));
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
