package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for {@link V2SalesforceParserFactory}
 * Created by crepache on 25/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2SalesforceParserFactoryTest {

  private static final String MOCK_INTEGRATION_TYPE = "mockType";

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @InjectMocks
  private V2SalesforceParserFactory factory;

  @Mock
  private OpportunityNotificationMetadataParser opportunityNotificationMetadataParser;


  @Before
  public void init() {
    beans.add(opportunityNotificationMetadataParser);

    factory.init();
  }

  @Test
  public void testNotAcceptable() {
    assertFalse(factory.accept(MessageMLVersion.V1));
  }

  @Test
  public void testAcceptable() {
    assertTrue(factory.accept(MessageMLVersion.V2));
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();
    settings.setType(MOCK_INTEGRATION_TYPE);

    factory.onConfigChange(settings);

    verify(opportunityNotificationMetadataParser, times(1)).setSalesforceUser(MOCK_INTEGRATION_TYPE);
  }

  @Test(expected = SalesforceParseException.class)
  public void testInvalidPayload() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), "invalid_payload");

    factory.getParser(payload);
  }
}