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

package org.symphonyoss.integration.webhook.salesforce;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceResolver;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.AccountStatusParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.CommonSalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.NullSalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1ParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * Created by rsanchez on 25/08/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesforceWebHookIntegrationTest extends BaseSalesforceTest{

  private static final String MOCK_INTEGRATION_USER = "mockUser";
  private static final String MOCK_DISPLAY_NAME = "Mock user";
  private static final String MOCK_USERNAME = "username";
  private static final String MOCK_EMAIL_ADDRESS = "test@symphony.com";
  private static final Long MOCK_USER_ID = 123456L;

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @InjectMocks
  private SalesforceWebHookIntegration salesforceWebHookIntegration = new SalesforceWebHookIntegration();

  @Spy
  private AccountStatusParser accountStatusParser = new AccountStatusParser();

  @Spy
  private SalesforceResolver salesforceResolver;

  @InjectMocks
  private V1ParserFactory factory;

  @Spy
  private NullSalesforceParser defaultJiraParser;

  @Mock
  private UserService userService;

  @Before
  public void setup() {

    beans.add(accountStatusParser);
    beans.add(defaultJiraParser);

    factory.init();

    doReturn(factory).when(salesforceResolver).getFactory();
  }

  @Test(expected = SalesforceParseException.class)
  public void testInvalidPayload() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), "invalid_payload");

    factory.getParser(payload).getEvents();
  }

  @Test
  public void testUnregistredParser() throws IOException{
    String xml = readFile("executiveReport.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);
    Message result = salesforceWebHookIntegration.parse(payload);
    Assert.assertEquals(xml, result.getMessage());
  }

//  @Test
//  public void testRegistredParser() throws IOException, JAXBException {
//    String xml = readFile("accountStatus.xml");
//    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);
//
//    String expected = readFile("parser/accountStatus_withMentionTags_expected.xml");
//
//    User user = new User();
//    user.setEmailAddress("amysak@company.com");
//    user.setId(123L);
//    user.setUserName("amysak");
//    user.setDisplayName("Alexandra Mysak");
//    when(userService.getUserByEmail(anyString(), anyString())).thenReturn(user);
//
//    Message result = salesforceWebHookIntegration.parse(payload);
//    assertEquals("<messageML>" + expected + "</messageML>", result.getMessage());
//  }

}
