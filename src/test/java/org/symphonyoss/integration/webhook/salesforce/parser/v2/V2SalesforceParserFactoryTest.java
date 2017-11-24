package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.NullSalesforceParser;
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
public class V2SalesforceParserFactoryTest extends BaseSalesforceTest {

  private static final String MOCK_INTEGRATION_TYPE = "mockType";
  private static final String INVALID_PAYLOAD = "invalid_payload";

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @InjectMocks
  private V2SalesforceParserFactory factory;

  @Mock
  private OpportunityNotificationMetadataParser opportunityNotificationMetadataParser;

  @Spy
  private NullSalesforceParser defaultSalesforceParser;

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

    verify(opportunityNotificationMetadataParser, times(1)).setSalesforceUser(
        MOCK_INTEGRATION_TYPE);
  }

  @Test
  public void testValidPayload() throws IOException {
    String validBody = readFile("accountStatus.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(),
        Collections.<String, String>emptyMap(), validBody);

    WebHookParser parser = factory.getParser(payload);
    assertNull(parser.parse(payload));
  }

  @Test(expected = SalesforceParseException.class)
  public void testInvalidPayload() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(),
        Collections.<String, String>emptyMap(), INVALID_PAYLOAD);

    factory.getParser(payload);
  }
}