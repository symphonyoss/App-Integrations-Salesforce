package org.symphonyoss.integration.webhook.salesforce.parser;

import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;

import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBException;

/**
 * Created by crepache on 15/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class NullSalesforceParserTest extends BaseSalesforceTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private SalesforceParser salesforceParser = new NullSalesforceParser();

  @Test
  public void testNullEvent() {
    Assert.assertEquals(0, salesforceParser.getEvents().size());
  }

  @Test
  public void testNullParser() throws IOException, JAXBException {
    String xml = readFile("executiveReport.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);
    Message result = salesforceParser.parse(payload);
    assertNull(result);
  }

  @Test()
  public void testParserJson() throws IOException {
    assertNull(salesforceParser.parse(null, JsonNodeFactory.instance.objectNode()));
  }
}
