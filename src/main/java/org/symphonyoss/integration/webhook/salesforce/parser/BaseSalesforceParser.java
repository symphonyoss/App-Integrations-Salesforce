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

import static org.symphonyoss.integration.parser.ParserUtils.presentationFormat;
import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.EMAIL_ADDRESS;
import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.INTEGRATION_NAME;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;

/**
 * Utility methods for Salesforce Parsers
 * Created by cmarcondes on 11/3/16.
 */
@Component
public abstract class BaseSalesforceParser implements SalesforceParser{

  @Autowired
  private UserService userService;

  private String salesforceUser;

  @Override
  public void setSalesforceUser(String user) {
    this.salesforceUser = user;
  }

  /**
   * Search the user at Symphony API.
   * @param email email to be found
   * @return User
   */
  private User getUser(String email) {
    return userService.getUserByEmail(salesforceUser, email);
  }

  protected void createMentionTagFor(Entity mainEntity, String userEntityName) {
    Entity userEntity = mainEntity.getEntityByName(userEntityName);
    if(userEntity == null){
      return;
    }
    String email = getEmail(userEntity);
    if (!StringUtils.isEmpty(email)) {
      User user = getUser(email);
      EntityBuilder.forEntity(userEntity).nestedEntity(user.getMentionEntity(INTEGRATION_NAME));
    }
  }

  /**
   * Creates the mention tag for the entity received.
   * @param mainEntity Main entity to find the nested entity
   * @param nestedEntityName Entity to find in the main entity
   * @param userEntityName Entity name for set the mention tag
   */
  protected void createListOfMentionsFor(Entity mainEntity, String nestedEntityName, String userEntityName){
    Entity entity = mainEntity.getEntityByType(nestedEntityName);
    if (entity != null) {
      for (Entity nestedEntity : entity.getEntities()) {
        createMentionTagFor(nestedEntity, userEntityName);
      }
    }
  }

  private String getEmail(Entity entity) {
    return entity.getAttributeValue(EMAIL_ADDRESS);
  }

  /**
   * Receives an array of object to format for the presentationML.
   * It's going to set an <br/> after each object.
   * @param args Array of object to be showed on presentationML
   * @return SafeString to be showed on presentationML
   */
  protected SafeString getPresentationMLBody(Object... args) {
    StrBuilder format = new StrBuilder("%s");
    SafeString body = null;

    for (Object obj : args) {
      if (obj != null) {
        body = formatPresentationML(format, obj);
      }
    }

    return body;
  }

  /**
   * Formats an object returning an SafeString
   * @param format format expected
   * @param obj Object to be formatted
   * @return SafeString
   */
  private SafeString formatPresentationML(StrBuilder format, Object obj) {
    SafeString safeString = presentationFormat(format.toString(), obj);
    format = format.clear().append(safeString.toString()).append("<br/>%s");
    return safeString;
  }

  /**
   * Return the Owner Name from Salesforce json
   * @param node type JsonNode
   * @return The Owner Name if it exists formatted, null otherwise.
   */
  protected SafeString getOwnerNameFormatted(JsonNode node) {
    String ownerName = node.path("Owner").path("Name").asText();

    if (StringUtils.isEmpty(ownerName)) {
      return null;
    }

    return presentationFormat("Owner Name: %s", ownerName);
  }

  /**
   * Return the Owner Email from Salesforce json
   * @param node type JsonNode
   * @return The Owner Email if it exists formatted, null otherwise.
   */
  protected SafeString getOwnerEmailFormatted(JsonNode node) {
    String ownerEmail = getOptionalField(node, "Owner", "Email", "").trim();

    if (StringUtils.isEmpty(ownerEmail)) {
      return null;
    }

    return presentationFormat("Owner Email: %s", ownerEmail);
  }

  /**
   * Return the Type from Salesforce json
   * @param node type JsonNode
   * @return The Type if it exists formatted, null otherwise.
   */
  protected SafeString getTypeFormatted(JsonNode node) {
    String type = node.path("Type").asText();

    if (StringUtils.isEmpty(type)) {
      return null;
    }

    return presentationFormat("Type: %s", type);
  }

  /**
   * Return the Link to Opportunity from Salesforce json
   * @param node type JsonNode
   * @return The Link if it exists formatted, null otherwise.
   */
  protected SafeString getLinkFormatted(JsonNode node) {
    String link = node.path("Link").asText();

    if (StringUtils.isEmpty(link)) {
      return null;
    }

    return presentationFormat("Link: %s", link);
  }

  /**
   * Return the Stage Name from Salesforce json
   * @param node type JsonNode
   * @return The Stage Name if it exists formatted, null otherwise.
   */
  protected SafeString getStageNameFormatted(JsonNode node) {
    String stageName = node.path("StageName").asText();

    if (StringUtils.isEmpty(stageName)) {
      return null;
    }

    return presentationFormat("Stage Name: %s", stageName);
  }

  /**
   * Return the Close Date from Salesforce json
   * @param node type JsonNode
   * @return The Close Date if it exists formatted, null otherwise.
   */
  protected SafeString getCloseDateFormatted(JsonNode node) {
    String closeDate = node.path("CloseDate").asText();

    if (StringUtils.isEmpty(closeDate)) {
      return null;
    }

    return presentationFormat("Close Date: %s", closeDate);
  }

  /**
   * Return the Account Name from Salesforce json
   * @param node type JsonNode
   * @return The Account Name if it exists formatted, null otherwise.
   */
  protected SafeString getAccountNameFormatted(JsonNode node) {
    String accountName = node.path("Account").path("Name").asText();

    if (StringUtils.isEmpty(accountName)) {
      return null;
    }

    return presentationFormat("Account Name: %s", accountName);
  }

  /**
   * Return the Account Link from Salesforce json
   * @param node type JsonNode
   * @return The Account Link if it exists formatted, null otherwise.
   */
  protected SafeString getAccountLinkFormatted(JsonNode node) {
    String accountLink = node.path("Account").path("Link").asText();

    if (StringUtils.isEmpty(accountLink)) {
      return null;
    }

    return presentationFormat("Account Link: %s", accountLink);
  }

  /**
   * Return the Name from Salesforce json
   * @param node type JsonNode
   * @return The Name if it exists formatted, null otherwise.
   */
  protected SafeString getNameFormatted(JsonNode node) {
    String name = node.path("Name").asText();

    if (StringUtils.isEmpty(name)) {
      return null;
    }

    return presentationFormat("Opportunity: %s", name);
  }

  private String getOptionalField(JsonNode node, String path, String key, String defaultValue) {
    String value = node.asText();

    if (value.isEmpty()) {
      return defaultValue;
    }

    return value;
  }

}