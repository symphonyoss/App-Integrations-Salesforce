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

package org.symphonyoss.integration.webhook.salesforce.parser;

import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.OPPORTUNITY;
import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.OWNER;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.exception.EntityXMLGeneratorException;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class responsable to handle the Opportunity Notification event of Salesforce
 *
 * Created by cmarcondes on 11/2/16.
 */
@Component
public class OpportunityNotificationParser extends BaseSalesforceParser {

  @Override
  public List<String> getEvents() {
    return Arrays.asList("com.symphony.integration.sfdc.event.opportunityNotification");
  }

  @Override
  public String parse(Entity entity) throws SalesforceParseException {
    createMentionTagFor(entity.getEntityByType(OPPORTUNITY), OWNER);

    try {
      return EntityBuilder.forEntity(entity).generateXML();
    } catch (EntityXMLGeneratorException e) {
      throw new SalesforceParseException("Something went wrong while building the message for Salesforce Opportunity Notification event.", e);
    }
  }

  @Override
  public String parse(Map<String, String> parameters, JsonNode node) throws SalesforceParseException {
    throw new SalesforceParseException("Parser used for JSON format but not used for XML.");
  }
}
