package org.symphonyoss.integration.webhook.salesforce.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.event.MessageMLVersionUpdatedEventData;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1SalesforceParserFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.v2.V2SalesforceParserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link SalesforceParserResolver}
 * Created by crepache on 12/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesforceParserResolverTest {

  @Spy
  private List<SalesforceParserFactory> factories = new ArrayList<>();

  private V1SalesforceParserFactory v1SalesforceParserFactory = new V1SalesforceParserFactory();

  private V2SalesforceParserFactory v2SalesforceParserFactory = new V2SalesforceParserFactory();

  @InjectMocks
  private SalesforceParserResolver resolver;

  @Before
  public void setup() {
    factories.add(v1SalesforceParserFactory);
    factories.add(v2SalesforceParserFactory);
  }

  @Test
  public void testInit() {
    resolver.init();

    assertEquals(v1SalesforceParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV1() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V1);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v1SalesforceParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV2() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V2);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v2SalesforceParserFactory, resolver.getFactory());
  }

}
