package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;

import java.io.IOException;
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
  private static final String OPPORTUNITY_NOTIFICATION_JSON_METADATA_CREATED = "parser/v2/OpportunityNotificationJSONMetadataCreated.json";
  private static final String OPPORTUNITY_NOTIFICATION_JSON_TEMPLATE_CREATED = "parser/v2/OpportunityNotificationJSONTemplateCreated";

  @Mock
  private UserService userService;

  private SalesforceMetadataParser parser;

  @Before
  public void init() {
    parser = new OpportunityNotificationJSONMetadataParser(userService);

    parser.init();
    parser.setSalesforceUser(MOCK_INTEGRATION_USER);
  }

  @Test
  public void testOpportunityNotificationCreated() throws JAXBException, IOException {
    mockUserInfo();
    JsonNode node = readJsonFromFile(OPPORTUNITY_NOTIFICATION_CREATED);
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(CONTENT_TYPE_HEADER_PARAM, MediaType.APPLICATION_JSON);

    Message result = parser.parse(headerParams, node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(OPPORTUNITY_NOTIFICATION_JSON_METADATA_CREATED);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());

    String expectedTemplate = readFile(OPPORTUNITY_NOTIFICATION_JSON_TEMPLATE_CREATED);
    assertEquals(expectedTemplate, result.getMessage().replace("\n", ""));
  }

  private void mockUserInfo() {
    User user = new User();
    user.setId(MOCK_USER_ID);
    user.setDisplayName(MOCK_DISPLAY_NAME);
    user.setUserName(MOCK_USERNAME);
    user.setEmailAddress(MOCK_EMAIL_ADDRESS);

    doReturn(user).when(userService).getUserByEmail(eq(MOCK_INTEGRATION_USER), anyString());
  }

}
