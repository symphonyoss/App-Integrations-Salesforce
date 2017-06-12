package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Unit test for {@link V1ParserFactory}
 * Created by crepache on 25/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V1ParserFactoryTest extends BaseSalesforceTest {

  private static final String MOCK_INTEGRATION_TYPE = "mockType";

  private static final String CONTENT_TYPE_HEADER_PARAM = "content-type";

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @Spy
  private OpportunityNotificationJSONParser opportunityNotificationJSONParser;

  @Spy
  private OpportunityNotificationParser opportunityNotificationParser;

  @Spy
  private AccountStatusParser accountStatusParser;

  @InjectMocks
  private V1ParserFactory factory;

  @Before
  public void init() {
    beans.add(opportunityNotificationJSONParser);
    beans.add(opportunityNotificationParser);
    beans.add(accountStatusParser);

    factory.init();
  }

  @Test
  public void testNotAcceptable() {
    assertFalse(factory.accept(MessageMLVersion.V2));
  }

  @Test
  public void testAcceptable() {
    assertTrue(factory.accept(MessageMLVersion.V1));
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();
    settings.setType(MOCK_INTEGRATION_TYPE);

    factory.onConfigChange(settings);

    verify(opportunityNotificationJSONParser, times(1)).setSalesforceUser(MOCK_INTEGRATION_TYPE);
    verify(opportunityNotificationParser, times(1)).setSalesforceUser(MOCK_INTEGRATION_TYPE);
    verify(accountStatusParser, times(1)).setSalesforceUser(MOCK_INTEGRATION_TYPE);
  }

  @Test
  public void testOpportunityNotificationJSONParser() {
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    WebHookPayload payload =
        new WebHookPayload(Collections.<String, String>emptyMap(), headerParams, null);

    assertEquals(opportunityNotificationJSONParser.getEvents(),
        factory.getParser(payload).getEvents());
  }

  @Test
  public void testOpportunityNotificationParser() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(),
        Collections.<String, String>emptyMap(), readFile("parser/v1/opportunityNotification.xml"));

    assertEquals(opportunityNotificationParser.getEvents(), factory.getParser(payload).getEvents());
  }

  @Test
  public void testAccountStatusParser() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(),
        Collections.<String, String>emptyMap(), readFile("accountStatus.xml"));

    assertEquals(accountStatusParser.getEvents(), factory.getParser(payload).getEvents());
  }

}
