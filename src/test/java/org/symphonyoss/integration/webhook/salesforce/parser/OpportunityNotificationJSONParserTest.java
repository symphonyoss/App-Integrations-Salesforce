package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;

import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by crepache on 24/03/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OpportunityNotificationJSONParserTest extends BaseSalesforceTest {

  private static final String CONTENT_TYPE_HEADER_PARAM = "content-type";
  private static final String TYPE_JSON = "application/json";
  private static final String OPPORTUNITY_NOTIFICATION = "SFDCCallbackSampleOpportunity.json";
  private static final String OPPORTUNITY_NOTIFICATION_withoutNextStep = "SFDCCallbackSampleOpportunity_withoutNextStep.json";

  @Mock
  private UserService userService;

  @InjectMocks
  private SalesforceParser salesforceParser = new OpportunityNotificationJSONParser();

  @Before
  public void setup() {
    User returnedUser =
        createUser("amysak", "amysak@company.com", "Alexandra Mysak", 7627861918843L);
    when(userService.getUserByEmail(anyString(), anyString())).thenReturn(returnedUser);
  }

  @Test
  public void LINKED_FORMATTED() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, TYPE_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile("parser/opportunityNotificationJSON");
    assertEquals(expected, result);
  }

  @Test
  public void testOpportunityNotification_withoutNextStep() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_withoutNextStep);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, TYPE_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile("parser/opportunityNotificationJSON_withoutNextStep");
    assertEquals(expected, result);
  }

  protected JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();

    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }
}
