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

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.MessageML;
import org.symphonyoss.integration.entity.MessageMLParser;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * Created by rsanchez on 31/08/16.
 */
@Component
public class SalesforceWebHookIntegration extends WebHookIntegration {

  @Autowired
  private SalesforceResolver salesforceResolver;

  @Autowired
  private List<SalesforceFactory> factories;

  @Override
  public void onConfigChange(IntegrationSettings settings) {
    super.onConfigChange(settings);

    for (SalesforceFactory factory : factories) {
      factory.onConfigChange(settings);
    }
  }

  @Override
  public Message parse(WebHookPayload input) throws WebHookParseException {
    WebHookParser parser = salesforceResolver.getFactory().getParser(input);
    return parser.parse(input);
  }


//  private Map<String, SalesforceParser> parsers = new HashMap<>();

//  @Autowired
//  private List<SalesforceParser> salesforceParserBeans;
//
//  @PostConstruct
//  public void init() {
//    for (SalesforceParser parser : salesforceParserBeans) {
//      List<String> events = parser.getEvents();
//      for (String eventType : events) {
//        this.parsers.put(eventType, parser);
//      }
//    }
//  }

//  @Override
//  public Message parse(WebHookPayload input) throws WebHookParseException {
//    if (isContentTypeJSON(input)) {
//      return parseJSONPayload(input);
//    }
//    Entity mainEntity = parsePayloadToEntity(input);
//
//    String type = mainEntity.getType();
//
//    SalesforceParser parser = getParser(type);
//    if (parser == null) {
//      Message message = new Message();
//      message.setMessage(input.getBody());
//      message.setFormat(Message.FormatEnum.MESSAGEML);
//      message.setVersion(MessageMLVersion.V1);
//
//      return message;
//    }
//
//    String messageML = parser.parse(mainEntity);
//    return super.buildMessageML(messageML, type);
//  }

//  private SalesforceParser getParser(String type) {
//    return parsers.get(type);
//  }

  /**
   * Parses the webhook payload (received in JSON format)
   * @param input the webhook payload
   * @return parsed content formatted as MessageML
   */
//  private Message parseJSONPayload(WebHookPayload input) {
//    JsonNode rootNode = null;
//
//    try {
//      rootNode = JsonUtils.readTree(input.getBody());
//    } catch (IOException e) {
//      throw new SalesforceParseException(
//          "Something went wrong when trying parse the JSON payload received by the webhook.", e);
//    }
//
//    SalesforceParser parser = getParser(OPPORTUNITY_NOTIFICATION_JSON);
//
//    String messageML = parser.parse(input.getHeaders(), rootNode);
//
//    return super.buildMessageML(messageML, OPPORTUNITY_NOTIFICATION_JSON);
//  }

}