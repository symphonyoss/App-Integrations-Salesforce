package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationBridge;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * Created by crepache on 27/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OpportunityNotificationMetadataParserTest extends BaseSalesforceTest {

  private static final Long MOCK_USER_ID = 123456L;
  private static final String MOCK_INTEGRATION_USER = "mockUser";
  private static final String MOCK_DISPLAY_NAME = "Mock user";
  private static final String MOCK_USERNAME = "username";
  private static final String MOCK_EMAIL_ADDRESS = "test@symphony.com";
  private static final String CONTENT_TYPE_HEADER_PARAM = "content-type";

  private static final String OPPORTUNITY_NOTIFICATION_CREATED = "SFDCCallbackSampleOpportunityCreated.json";
  private static final String OPPORTUNITY_NOTIFICATION_UPDATED = "SFDCCallbackSampleOpportunityUpdated.json";
  private static final String OPPORTUNITY_NOTIFICATION_WITH_ALL_FIELDS_NULL = "SFDCCallbackSampleOpportunity_WithAllFieldsNull.json";

  private static final String OPPORTUNITY_NOTIFICATION_METADATA_CREATED = "parser/v2/OpportunityNotificationMetadataCreated.json";
  private static final String OPPORTUNITY_NOTIFICATION_METADATA_UPDATED = "parser/v2/OpportunityNotificationMetadataUpdated.json";
  private static final String OPPORTUNITY_NOTIFICATION_METADATA_WITH_ALL_FIELDS_NULL = "parser/v2/OpportunityNotificationMetadataWithAllFieldsNull.json";

  private static final String OPPORTUNITY_NOTIFICATION_TEMPLATE_CREATED = "parser/v2/OpportunityNotificationTemplateCreated";

  private static final String INTEGRATION_NAME = "salesforce";

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  private SalesforceMetadataParser parser;

  @Before
  public void init() {
    parser = new OpportunityNotificationMetadataParser(userService, integrationProperties);

    parser.init();
    parser.setSalesforceUser(MOCK_INTEGRATION_USER);
  }

  @Test
  public void testOpportunityNotificationCreated() throws JAXBException, IOException {
    mockUserInfo();
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_CREATED);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    mockIntegrationProperties();
    Message result = parser.parse(headerParams, node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(OPPORTUNITY_NOTIFICATION_METADATA_CREATED);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());

    String expectedTemplate = readFile(OPPORTUNITY_NOTIFICATION_TEMPLATE_CREATED);
    assertEquals(expectedTemplate, result.getMessage().replace("\n", ""));
  }

  @Test
  public void testOpportunityNotificationUpdated() throws JAXBException, IOException {
    mockUserInfo();
    mockIntegrationProperties();
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_UPDATED);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    Message result = parser.parse(headerParams, node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(OPPORTUNITY_NOTIFICATION_METADATA_UPDATED);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
  }

  @Test
  public void testOpportunityNotificationWithAllFieldsNull() throws JAXBException, IOException {
    mockUserInfo();
    mockIntegrationProperties();
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_WITH_ALL_FIELDS_NULL);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    Message result = parser.parse(headerParams, node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(OPPORTUNITY_NOTIFICATION_METADATA_WITH_ALL_FIELDS_NULL);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
  }

  private void mockUserInfo() {
    User user = new User();
    user.setId(MOCK_USER_ID);
    user.setDisplayName(MOCK_DISPLAY_NAME);
    user.setUserName(MOCK_USERNAME);
    user.setEmailAddress(MOCK_EMAIL_ADDRESS);

    doReturn(user).when(userService).getUserByEmail(eq(MOCK_INTEGRATION_USER), anyString());
  }

  private void mockIntegrationProperties() {
    doReturn("symphony.com").when(integrationProperties).getApplicationUrl(INTEGRATION_NAME);
  }

  @Test()
  public void testParserJson() throws IOException {
    Assert.assertNull(parser.parse(new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), null)));
  }
}
