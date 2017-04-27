package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.symphonyoss.integration.webhook.salesforce.parser
    .v1.OpportunityNotificationJSONParserTest.PARSER_OPPORTUNITY_NOTIFICATION_JSON_CREATED;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * Created by crepache on 27/04/17.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class OpportunityNotificationMetadataParserTest extends BaseSalesforceTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";
  private static final String CONTENT_TYPE_HEADER_PARAM = "content-type";
  private static final String OPPORTUNITY_NOTIFICATION_CREATED = "SFDCCallbackSampleOpportunityCreated.json";

  @Mock
  private UserService userService;

  @InjectMocks
  private SalesforceParser salesforceParser = new OpportunityNotificationJSONMetadataParser(userService);

  private SalesforceMetadataParser parser;

  @Before
  public void init() {
    parser = new OpportunityNotificationJSONMetadataParser(userService);

    parser.init();
    parser.setSalesforceUser(MOCK_INTEGRATION_USER);
  }

  @Test
  public void testOpportunityNotificationCreated() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_CREATED);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    Message result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON_CREATED);
    assertEquals(expected, result.getMessage());
  }

}
