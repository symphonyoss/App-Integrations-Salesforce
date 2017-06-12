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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParserFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParserResolver;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.AccountStatusParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.NullSalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1ParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * Created by rsanchez on 25/08/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesforceWebHookIntegrationTest extends BaseSalesforceTest{

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @Spy
  private List<SalesforceParserFactory> factories = new ArrayList<>();

  @InjectMocks
  private SalesforceWebHookIntegration salesforceWebHookIntegration = new SalesforceWebHookIntegration();

  @Spy
  private AccountStatusParser accountStatusParser = new AccountStatusParser();

  @Spy
  private SalesforceParserResolver salesforceParserResolver;

  @InjectMocks
  private V1ParserFactory v1ParserFactory;

  @Spy
  private NullSalesforceParser defaultSalesforceParser;

  @Mock
  private SalesforceParserFactory factory;

  @Before
  public void setup() {

    beans.add(accountStatusParser);
    beans.add(defaultSalesforceParser);

    factories.add(factory);

    v1ParserFactory.init();

    doReturn(v1ParserFactory).when(salesforceParserResolver).getFactory();
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();

    salesforceWebHookIntegration.onConfigChange(settings);

    verify(factory, times(1)).onConfigChange(settings);
  }

  @Test(expected = SalesforceParseException.class)
  public void testInvalidPayload() throws IOException {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), "invalid_payload");

    v1ParserFactory.getParser(payload).getEvents();
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

    String expected = readFile("parser/v1/accountStatus_withMentionTags_expected.xml");
    Message message = new Message();
    message.setVersion(MessageMLVersion.V1);
    message.setMessage(expected);
    doReturn(message).when(accountStatusParser).parse(payload);

    Message result = salesforceWebHookIntegration.parse(payload);
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testSupportedContentTypes() {
    List<MediaType> supportedContentTypes = new ArrayList<>();
    supportedContentTypes.add(MediaType.WILDCARD_TYPE);

    Assert.assertEquals(salesforceWebHookIntegration.getSupportedContentTypes(), supportedContentTypes);
  }
}
