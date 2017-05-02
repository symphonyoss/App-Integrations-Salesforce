package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link V2ParserFactory}
 * Created by crepache on 25/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2ParserFactoryTest {

  private static final String MOCK_INTEGRATION_TYPE = "mockType";

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @InjectMocks
  private V2ParserFactory factory;

  @Mock
  private OpportunityNotificationJSONMetadataParser opportunityNotificationJSONMetadataParser;


  @Before
  public void init() {
    beans.add(opportunityNotificationJSONMetadataParser);

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

    verify(opportunityNotificationJSONMetadataParser, times(1)).setSalesforceUser(MOCK_INTEGRATION_TYPE);
  }
}