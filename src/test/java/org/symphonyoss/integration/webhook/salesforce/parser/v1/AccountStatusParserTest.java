/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.salesforce.BaseSalesforceTest;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBException;

/**
 * Unit tests for {@link AccountStatusParser}
 * Created by cmarcondes on 11/3/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountStatusParserTest extends BaseSalesforceTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private SalesforceParser salesforceParser = new AccountStatusParser();

  @Before
  public void setup() {
    User returnedUser =
        createUser("amysak", "amysak@company.com", "Alexandra Mysak", 7627861918843L);
    when(userService.getUserByEmail(anyString(), anyString())).thenReturn(returnedUser);
  }

  @Test
  public void testAddingMentionTag()
      throws WebHookParseException, IOException,
      JAXBException {
    String messageML = readFile("accountStatus.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), messageML);
    Message result = salesforceParser.parse(payload);

    String expected = readFile("parser/v1/accountStatus_withMentionTags_expected.xml");

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testUserNotFound()
      throws WebHookParseException, IOException,
      JAXBException {
    User returnedUser =
        createUser(null, "amysak@company.com", null, null);
    when(userService.getUserByEmail(anyString(), anyString())).thenReturn(returnedUser);
    String messageML = readFile("accountStatus.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), messageML);
    Message result = salesforceParser.parse(payload);

    String expected = readFile("parser/v1/accountStatus_withoutMentionTags_expected.xml");

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testWithoutAccountOwner()
      throws WebHookParseException, IOException,
      JAXBException {
    String messageML = readFile("parser/v1/accountStatus_without_AccountOwner.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), messageML);
    Message result = salesforceParser.parse(payload);

    String expected = readFile("parser/v1/accountStatus_without_AccountOwner_expected.xml");

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testWithoutOpportunityOwner()
      throws WebHookParseException, IOException,
      JAXBException {
    String messageML = readFile("parser/v1/accountStatus_without_OpportunityOwner.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), messageML);
    Message result = salesforceParser.parse(payload);

    String expected = readFile("parser/v1/accountStatus_without_OpportunityOwner_expected.xml");

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testWithoutOwnerEmail()
      throws WebHookParseException, IOException,
      JAXBException {
    String messageML = readFile("parser/v1/accountStatus_without_ownerEmail.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), messageML);
    Message result = salesforceParser.parse(payload);

    String expected = readFile("parser/v1/accountStatus_without_ownerEmail_expected.xml");

    assertEquals(expected, result.getMessage());
  }

  @Test(expected = SalesforceParseException.class)
  public void testParserJson() throws IOException {
    salesforceParser.parse(null, JsonNodeFactory.instance.objectNode());
  }
}