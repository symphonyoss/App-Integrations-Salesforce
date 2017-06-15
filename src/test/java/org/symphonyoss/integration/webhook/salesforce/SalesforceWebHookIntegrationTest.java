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

import static org.mockito.Matchers.any;
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
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
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

/**
 * Created by rsanchez on 25/08/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesforceWebHookIntegrationTest extends BaseSalesforceTest {

  @Spy
  private List<SalesforceParser> beans = new ArrayList<>();

  @Spy
  private List<SalesforceParserFactory> factories = new ArrayList<>();

  @InjectMocks
  private SalesforceWebHookIntegration salesforceWebHookIntegration = new SalesforceWebHookIntegration();

  @Spy
  private AccountStatusParser accountStatusParser = new AccountStatusParser();

  @Mock
  private SalesforceParserResolver salesforceParserResolver;

  @Mock
  private V1ParserFactory v1ParserFactory;

  @Spy
  private NullSalesforceParser defaultSalesforceParser;

  @Mock
  private SalesforceParserFactory factory;

  @Mock
  private WebHookParser webHookParser;

  @Before
  public void setup() {

    beans.add(accountStatusParser);
    beans.add(defaultSalesforceParser);

    factories.add(factory);

    v1ParserFactory.init();
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();

    salesforceWebHookIntegration.onConfigChange(settings);

    verify(factory, times(1)).onConfigChange(settings);
  }

  @Test
  public void testSupportedContentTypes() {
    List<MediaType> supportedContentTypes = new ArrayList<>();
    supportedContentTypes.add(MediaType.WILDCARD_TYPE);

    Assert.assertEquals(supportedContentTypes, salesforceWebHookIntegration.getSupportedContentTypes());
  }

  @Test
  public void testParser() throws IOException {
    String xml = readFile("accountStatus.xml");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), xml);

    doReturn(v1ParserFactory).when(salesforceParserResolver).getFactory();
    doReturn(webHookParser).when(v1ParserFactory).getParser(any(WebHookPayload.class));
    Message message = new Message();
    doReturn(message).when(webHookParser).parse(any(WebHookPayload.class));
    Message parse = salesforceWebHookIntegration.parse(payload);
    verify(webHookParser, times(1)).parse(payload);
  }
}
