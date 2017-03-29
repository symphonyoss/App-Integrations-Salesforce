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

import javax.ws.rs.core.MediaType;
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
  private static final String OPPORTUNITY_NOTIFICATION = "SFDCCallbackSampleOpportunity.json";
  private static final String OPPORTUNITY_NOTIFICATION_WITHOUT_NEXT_STEPS = "SFDCCallbackSampleOpportunity_WithoutNextStep.json";
  private static final String OPPORTUNITY_NOTIFICATION_WITHOUT_AMOUNT = "SFDCCallbackSampleOpportunity_WhitoutAmount.json";
  private static final String OPPORTUNITY_NOTIFICATION_WITH_ALL_FIELDS_NULL = "SFDCCallbackSampleOpportunity_WithAllFieldsNull.json";
  private static final String OPPORTUNITY_NOTIFICATION_WITHOUT_TAGS_ACCOUNT_AND_OWNER = "SFDCCallbackSampleOpportunity_WithoutTagsAccountAndOwner.json";

  public static final String PARSER_OPPORTUNITY_NOTIFICATION_JSON = "parser/opportunityNotificationJSON";
  public static final String PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_NEXT_STEP = "parser/opportunityNotificationJSON_WithoutNextStep";
  public static final String PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_AMOUNT = "parser/opportunityNotificationJSON_WithoutAmount";
  public static final String PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_ALL_FIELDS_NULL = "parser/opportunityNotificationJSON_WithoutAllFieldsNull";
  public static final String PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_TAGS_ACCOUNT_AND_OWNER = "parser/opportunityNotificationJSON_WithoutTagsAccountAndOwner";

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
  public void testOpportunityNotification() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON);
    assertEquals(expected, result);
  }

  @Test
  public void testOpportunityNotificationWithoutNextStep() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_WITHOUT_NEXT_STEPS);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_NEXT_STEP);
    assertEquals(expected, result);
  }

  @Test
  public void testOpportunityNotificationWithAllFieldsNull() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_WITH_ALL_FIELDS_NULL);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_ALL_FIELDS_NULL);
    assertEquals(expected, result);
  }

  @Test
  public void testOpportunityNotificationWithoutAmount() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_WITHOUT_AMOUNT);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_AMOUNT);
    assertEquals(expected, result);
  }

  @Test
  public void testOpportunityNotificationWithoutTagsAccountAndOwner() throws JAXBException, IOException {
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_WITHOUT_TAGS_ACCOUNT_AND_OWNER);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    String result = salesforceParser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);
    String expected = readFile(PARSER_OPPORTUNITY_NOTIFICATION_JSON_WITHOUT_TAGS_ACCOUNT_AND_OWNER);
    assertEquals(expected, result);
  }

  protected JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();

    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }
}
