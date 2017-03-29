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
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

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

  public static final String OPPORTUNITY_NOTIFICATION_JSON = "opportunityNotificationJSON";

  private Map<String, SalesforceParser> parsers = new HashMap<>();

  @Autowired
  private List<SalesforceParser> salesforceParserBeans;

  @PostConstruct
  public void init() {
    for (SalesforceParser parser : salesforceParserBeans) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  @Override
  public void onConfigChange(IntegrationSettings settings) {
    super.onConfigChange(settings);

    for (SalesforceParser parsers : salesforceParserBeans) {
      parsers.setSalesforceUser(settings.getType());
    }
  }

  @Override
  public String parse(WebHookPayload input) throws WebHookParseException {
    String messageML;

    if (isContentTypeJSON(input)) {
      return processIntegrationWithJSON(input);
    }

    Entity mainEntity = parsePayloadToEntity(input);

    String type = mainEntity.getType();

    SalesforceParser parser = getParser(type);

    if (parser == null) {
      return input.getBody();
    }

    messageML = parser.parse(mainEntity);
    messageML = super.buildMessageML(messageML, type);

    return messageML;
  }

  private SalesforceParser getParser(String type) {
    return parsers.get(type);
  }

  private Entity parsePayloadToEntity(WebHookPayload payload) {
    try {
      MessageML messageML = MessageMLParser.parse(payload.getBody());
      return messageML.getEntity();
    } catch (JAXBException e) {
      throw new SalesforceParseException(
          "Something went wrong when trying to validate the MessageML received to object.", e);
    }
  }

  /**
   * Return true when Type is JSON, is not tag ContentType when return false
   * @param payload type WebHookPayload
   * @return the true when Type is JSON
   */
  private boolean isContentTypeJSON(WebHookPayload payload) {
    if(!hasContentType(payload)) {
      return false;
    }

    return MediaType.APPLICATION_JSON.equals(getContentType(payload));
  }

  /**
   * Return the type of integration JSON/XML
   * @param payload type WebHookPayload
   * @return the Content-Type with Header payload
   */
  private String getContentType(WebHookPayload payload) {
    return payload.getHeaders().get("content-type");
  }

  /**
   * Return if exists header ContentType
   * @param payload type WebHookPayload
   * @return  if exists the Content-Type with Header payload
   */
  private boolean hasContentType(WebHookPayload payload) {
    return payload.getHeaders().get("content-type") != null;
  }

  /**
   * Return the MessageML parser with JSON integration about Opportunity Notification
   * @param input type WebHookPayload
   * @return MessageML formatted
   */
  private String processIntegrationWithJSON(WebHookPayload input) {
    JsonNode rootNode = null;

    try {
      rootNode = JsonUtils.readTree(input.getBody());
    } catch (IOException e) {
      throw new SalesforceParseException(
          "Something went wrong when trying to validate the MessageML received to object.", e);
    }

    SalesforceParser parser = getParser(OPPORTUNITY_NOTIFICATION_JSON);

    String messageML = parser.parse(input.getHeaders(), rootNode);
    messageML = super.buildMessageML(messageML, OPPORTUNITY_NOTIFICATION_JSON);

    return messageML;
  }

}