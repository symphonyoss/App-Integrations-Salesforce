package org.symphonyoss.integration.webhook.salesforce.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.event.MessageMLVersionUpdatedEventData;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1ParserFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.v2.V2ParserFactory;

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

  private V1ParserFactory v1ParserFactory = new V1ParserFactory();

  private V2ParserFactory v2ParserFactory = new V2ParserFactory();

  @InjectMocks
  private SalesforceParserResolver resolver;

  @Before
  public void setup() {
    factories.add(v1ParserFactory);
    factories.add(v2ParserFactory);
  }

  @Test
  public void testInit() {
    resolver.init();

    assertEquals(v1ParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV1() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V1);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v1ParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV2() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V2);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v2ParserFactory, resolver.getFactory());
  }

}
