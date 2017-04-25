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
import org.symphonyoss.integration.entity.MessageMLParser;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.AccountStatusParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * Created by rsanchez on 25/08/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesforceWebHookIntegrationTest extends BaseSalesforceTest{

  private static final String CONTENT_TYPE_HEADER_PARAM = "content-type";
  private static final String OPPORTUNITY_NOTIFICATION =
      "SFDCCallbackSampleOpportunityCreated.json";

  @Spy
  private List<SalesforceParser> salesforceParserBeans = new ArrayList<>();

  @Mock
  private AccountStatusParser accountStatusParser = new AccountStatusParser();

  @InjectMocks
  private SalesforceWebHookIntegration salesforceWebHookIntegration = new SalesforceWebHookIntegration();

  private MessageMLParser messageMLParser = new MessageMLParser();

  @Before
  public void setup() {
    when(accountStatusParser.getEvents()).thenReturn(Arrays.asList("com.symphony.integration.sfdc.event.accountStatus"));

    salesforceParserBeans.add(accountStatusParser);
  }

  @Test(expected = SalesforceParseException.class)
  public void testInvalidPayload(){
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), "invalid_payload");
    salesforceWebHookIntegration.parse(payload);
  }

  @Test
  public void testUnregistredParser() throws IOException{
    String xml = readFile("executiveReport.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);
    Message result = salesforceWebHookIntegration.parse(payload);
    Assert.assertEquals(xml, result.getMessage());
  }

  @Test
  public void testRegistredParser() throws IOException, JAXBException {
    String xml = readFile("accountStatus.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);

    String expected = readFile("parser/accountStatus_withMentionTags_expected.xml");
    when(accountStatusParser.parse(any(Entity.class))).thenReturn(expected);

    Message result = salesforceWebHookIntegration.parse(payload);
    assertEquals("<messageML>" + expected + "</messageML>", result.getMessage());
  }

}
