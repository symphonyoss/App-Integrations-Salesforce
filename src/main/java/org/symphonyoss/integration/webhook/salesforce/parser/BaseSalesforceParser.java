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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.messageml.MessageMLFormatConstants;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility methods for Salesforce Parsers
 * Created by cmarcondes on 11/3/16.
 */
@Component
public abstract class BaseSalesforceParser implements SalesforceParser{

  public static final String LINKED_FORMATTED_TEXT = "(%s)";
  public static final String FORMATTED = "%s";
  public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd";
  public static final String OPPORTUNITY_OWNER = "Opportunity Owner: %s";
  public static final String TYPE_FORMATTED = "Type: %s";
  public static final String STAGE_FORMATTED = "Stage: %s";
  public static final String CLOSE_DATE_FORMATTED = "Close Date: %s";
  public static final String ACCOUNT_NAME_FORMATTED = "Account Name: %s";
  public static final String AMOUNT_FORMATTED = "Amount: %s";
  public static final String NEXT_STEP_FORMATTED = "Next Step: %s";
  public static final String PROBABILITY_FORMATTED = "Probability: %s";

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

  private String getOwnerName(JsonNode node) {
    return node.path("Owner").path("Name").asText();
  }

  /**
   * Return the Owner Name Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Owner Name if it exists formatted, null otherwise.
   */
  protected SafeString getOwnerNameFormatted(JsonNode node) {
    String ownerName = getOptionalField(getOwnerName(node), "-");

    return presentationFormat(OPPORTUNITY_OWNER, ownerName);
  }

  private String getOwnerEmail(JsonNode node) {
    return node.path("Owner").path("Email").asText();
  }

  /**
   * Return the Owner Email Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Owner Email if it exists formatted, null otherwise.
   */
  protected SafeString getOwnerEmailFormatted(JsonNode node) {
    String ownerEmail = getOptionalField(getOwnerEmail(node), "-");

    if (emailExistsInSimphony(ownerEmail.toString())) {
      return presentationFormat(FORMATTED, presentationFormat(MessageMLFormatConstants.MESSAGEML_MENTION_EMAIL_FORMAT, ownerEmail));
    }

    return presentationFormat(LINKED_FORMATTED_TEXT, ownerEmail);
  }

  private String getType(JsonNode node) {
    return node.path("Type").asText();
  }

  /**
   * Return the Type Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Type if it exists formatted, null otherwise.
   */
  protected SafeString getTypeFormatted(JsonNode node) {
    String type = getOptionalField(getType(node), "-");

    return presentationFormat(TYPE_FORMATTED, type);
  }

  private String getStageName(JsonNode node) {
    return node.path("StageName").asText();
  }

  /**
   * Return the Stage Name Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Stage Name if it exists formatted, null otherwise.
   */
  protected SafeString getStageNameFormatted(JsonNode node) {
    String stageName = getOptionalField(getStageName(node), "-");

    return presentationFormat(STAGE_FORMATTED, stageName);
  }


  private String getCloseDate(JsonNode node) {
    return node.path("CloseDate").asText();
  }

  /**
   * Return the Close Date Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Close Date if it exists formatted, null otherwise.
   */
  protected SafeString getCloseDateFormatted(JsonNode node) {
    String closeDate = getOptionalField(getCloseDate(node), "-");

    SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
    String closeDateFormat;

    try {
      closeDateFormat = formatter.format(formatter.parse(closeDate));
    } catch (ParseException e) {
      return presentationFormat(CLOSE_DATE_FORMATTED, new SafeString("-"));
    }

    return presentationFormat(CLOSE_DATE_FORMATTED, closeDateFormat);
  }

  private String getAccountName(JsonNode node) {
    return node.path("Account").path("Name").asText();
  }

  /**
   * Return the Account Name Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Account Name if it exists formatted, null otherwise.
   */
  protected SafeString getAccountNameFormatted(JsonNode node) {
    String accountName = getOptionalField(getAccountName(node), "-");

    return presentationFormat(ACCOUNT_NAME_FORMATTED, accountName);
  }

  private String getAccountLink(JsonNode node) {
    return node.path("Account").path("Link").asText();
  }

  /**
   * Return the URL from Account Formattedjson formated.
   * @param node type JsonNode
   * @return (<a href="https://symdev1-dev-ed.my.salesforce.com/00146000004oPCcAAM"/>)
   */
  protected SafeString getAccountLinkedFormatted(JsonNode node) {
    String accountLink = getOptionalField(getAccountLink(node), "-");

    SafeString finalUrl = presentationFormat(MessageMLFormatConstants.MESSAGEML_LINK_HREF_FORMAT, accountLink.toString());

    return presentationFormat(LINKED_FORMATTED_TEXT, finalUrl);
  }

  private String getAmount(JsonNode node) {
    return node.path("Amount").asText();
  }

  /**
   * Return the Amount Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Amount if it exists formatted, null otherwise.
   */
  protected SafeString getAmountFormatted(JsonNode node) {
    String amount = getOptionalField(getAmount(node), "-");

    return presentationFormat(AMOUNT_FORMATTED, amount);
  }

  private String getNextStep(JsonNode node) {
    return node.path("NextStep").asText();
  }

  /**
   * Return the Next Step Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Next Step if it exists formatted, null otherwise.
   */
  protected SafeString getNextStepFormatted(JsonNode node) {
    String nextStep = getOptionalField(getNextStep(node), "-");

    if (StringUtils.isEmpty(nextStep)) {
      return SafeString.EMPTY_SAFE_STRING;
    }

    return presentationFormat(NEXT_STEP_FORMATTED, nextStep);
  }

  private String getProbability(JsonNode node) {
    return node.path("Probability").asText();
  }

  /**
   * Return the Probability Formatted from Salesforce json
   * @param node type JsonNode
   * @return The Probability if it exists formatted, null otherwise.
   */
  protected SafeString getProbabilityFormatted(JsonNode node) {
    String probability = getOptionalField(getProbability(node), "-");

    return presentationFormat(PROBABILITY_FORMATTED, probability);
  }

  private String getCurrencyIsoCode(JsonNode node) {
    return node.path("CurrencyIsoCode").asText();
  }
  /**
   * Return the CurrencyIsoCode Formatted from Salesforce json
   * @param node type JsonNode
   * @return The CurrencyIsoCode if it exists formatted, null otherwise.
   */
  protected SafeString getCurrencyIsoCodeFormatted(JsonNode node) {
    if (StringUtils.isEmpty(getAmount(node))) {
      return presentationFormat(FORMATTED, new SafeString(""));
    }

    String currencyIsoCode = getOptionalField(getCurrencyIsoCode(node), "-");

    return presentationFormat(FORMATTED, currencyIsoCode);
  }

  private String getOptionalField(String value, String defaultValue) {
    if (value.isEmpty()) {
      return defaultValue;
    }

    return value;
  }

  /**
   * Verified if already exists email address
   * @param emailAddress
   * @return
   */
  private boolean emailExistsInSimphony(String emailAddress) {
    if ((emailAddress == null) || (emailAddress.isEmpty())) {
      return false;
    }

    User user = userService.getUserByEmail(salesforceUser, emailAddress);
    if (user.getId() == null) {
      return false;
    }

    return true;
  }

}