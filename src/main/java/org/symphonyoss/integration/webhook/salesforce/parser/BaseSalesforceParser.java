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
import static org.symphonyoss.integration.parser.SafeString.EMPTY_SAFE_STRING;
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
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Utility methods for Salesforce Parsers
 * Created by cmarcondes on 11/3/16.
 */
@Component
public abstract class BaseSalesforceParser implements SalesforceParser{

  private static final String FORMATTED_STRING_WITH_PARENTHESIS = "(%s)";
  private static final String FORMATTED_STRING = "%s";
  private static final String OPPORTUNITY_NAME = "<b>Opportunity:</b> %s";
  private static final String OPPORTUNITY_OWNER = "<b>Opportunity Owner:</b> %s";
  private static final String OPPORTUNITY_TYPE = "<b>Type:</b> %s";
  private static final String OPPORTUNITY_STAGE = "<b>Stage:</b> %s";
  private static final String OPPORTUNITY_CLOSE_DATE = "<b>Close Date:</b> %s";
  private static final String ACCOUNT_NAME = "<b>Account Name:</b> %s";
  private static final String OPPORTUNITY_AMOUNT = "<b>Amount:</b> %s";
  private static final String OPPORTUNITY_NEXT_STEP = "<b>Next Step:</b> %s";
  private static final String OPPORTUNITY_PROBABILITY = "<b>Probability:</b> %s";
  private static final String DEFAULT_CONTENT_FOR_MISSING_FIELD = "-";
  private static final String DEFAULT_VALUE_NULL = "";
  public static final String STRING_NULL = "null";

  @Autowired
  private UserService userService;

  private String salesforceUser;

  @Override
  public void setSalesforceUser(String user) {
    this.salesforceUser = user;
  }

  /**
   * Searches a matching Symphony user for the given Salesforce user email.
   * @param email email to be found
   * @return user information, including a matching Symphony ID (if any)
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

  private String getName(JsonNode node) {
    return node.path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getNameFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_NAME, getName(node));
  }

  private String getLink(JsonNode node) {
    return node.path(SalesforceConstants.LINK).asText();
  }

  protected SafeString getLinkFormatted(JsonNode node) {
    String link = getLink(node);

    if (StringUtils.isEmpty(link)) {
      return EMPTY_SAFE_STRING;
    }

    SafeString finalUrl = presentationFormat(MessageMLFormatConstants.MESSAGEML_LINK_HREF_FORMAT, link);

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, finalUrl);
  }

  private String getOwnerName(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_OWNER).path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getOwnerNameFormatted(JsonNode node) {
    if (emailExistsAtSymphony(getOwnerEmail(node))) {
      return presentationFormat(OPPORTUNITY_OWNER, DEFAULT_VALUE_NULL);
    }

    return formatOptionalField(OPPORTUNITY_OWNER, getOwnerName(node));
  }

  private String getOwnerEmail(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_OWNER).path(SalesforceConstants.EMAIL).asText();
  }

  protected SafeString getOwnerEmailFormatted(JsonNode node) {
    String ownerEmail = getOwnerEmail(node);

    if (StringUtils.isBlank(ownerEmail)) {
      return EMPTY_SAFE_STRING;
    }

    if (emailExistsAtSymphony(ownerEmail)) {
      return presentationFormat(MessageMLFormatConstants.MESSAGEML_MENTION_EMAIL_FORMAT, ownerEmail);
    }

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, ownerEmail);
  }

  private String getType(JsonNode node) {
    return node.path(SalesforceConstants.TYPE).asText();
  }

  protected SafeString getTypeFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_TYPE, getType(node));
  }

  private String getStageName(JsonNode node) {
    return node.path(SalesforceConstants.STAGE_NAME).asText();
  }

  protected SafeString getStageNameFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_STAGE, getStageName(node));
  }


  private String getCloseDate(JsonNode node) {
    return node.path(SalesforceConstants.CLOSE_DATE).asText();
  }

  protected SafeString getCloseDateFormatted(JsonNode node) {
    String closeDate = getCloseDate(node);

    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);
    String closeDateFormat;

    try {
      closeDateFormat = formatter.format(formatter.parse(closeDate));
    } catch (ParseException e) {
      closeDateFormat = DEFAULT_CONTENT_FOR_MISSING_FIELD;
    }

    return presentationFormat(OPPORTUNITY_CLOSE_DATE, closeDateFormat);
  }

  private String getAccountName(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_ACCOUNT).path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getAccountNameFormatted(JsonNode node) {
    return formatOptionalField(ACCOUNT_NAME, getAccountName(node));
  }

  private String getAccountLink(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_ACCOUNT).path(SalesforceConstants.LINK).asText();
  }

  protected SafeString getAccountLinkedFormatted(JsonNode node) {
    String accountLink = getAccountLink(node);

    if (StringUtils.isEmpty(accountLink)) {
      return EMPTY_SAFE_STRING;
    }

    SafeString finalUrl = presentationFormat(MessageMLFormatConstants.MESSAGEML_LINK_HREF_FORMAT, accountLink);

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, finalUrl);
  }

  private String getAmount(JsonNode node) {
    return node.path(SalesforceConstants.AMOUNT).asText();
  }

  protected SafeString getAmountFormatted(JsonNode node) {
    String amount = getAmount(node);

    if (StringUtils.isNotBlank(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);
    }

    return formatOptionalField(OPPORTUNITY_AMOUNT, amount);
  }

  private String getNextStep(JsonNode node) {
    return node.path(SalesforceConstants.NEXT_STEP).asText();
  }

  protected SafeString getNextStepFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_NEXT_STEP, getNextStep(node));
  }

  private String getProbability(JsonNode node) {
    return node.path(SalesforceConstants.PROBABILITY).asText();
  }

  protected SafeString getProbabilityFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_PROBABILITY, getProbability(node));
  }

  private String getCurrencyIsoCode(JsonNode node) {
    return node.path(SalesforceConstants.CURRENCY_ISO_CODE).asText();
  }

  protected SafeString getCurrencyIsoCodeFormatted(JsonNode node) {
    String currencyIsoCode = getCurrencyIsoCode(node);

    if (StringUtils.isEmpty(getAmount(node))) {
      currencyIsoCode = StringUtils.EMPTY;
    }

    return presentationFormat(FORMATTED_STRING, currencyIsoCode);
  }

  private SafeString formatOptionalField(String format, String value) {
    if (value.isEmpty() || value.equals(STRING_NULL)) {
      value = DEFAULT_CONTENT_FOR_MISSING_FIELD;
    }

    return presentationFormat(format, value);
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(salesforceUser, emailAddress);
    return user.getId() != null;
  }

  private String getEmailLastModifiedBy(JsonNode node) {
    return node.path(SalesforceConstants.LAST_MODIFY_BY).path(SalesforceConstants.EMAIL).asText();
  }

  protected SafeString getEmailLastModifiedByFormatted(JsonNode node) {
    String emailLastModifiedBy = getEmailLastModifiedBy(node);

    if (!emailExistsAtSymphony(emailLastModifiedBy)) {
      return EMPTY_SAFE_STRING;
    }

    return presentationFormat(MessageMLFormatConstants.MESSAGEML_MENTION_EMAIL_FORMAT, emailLastModifiedBy);
  }

  protected SafeString getUpdatedFields(JsonNode node) {
    String updatedFields = null;

    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext()) {

      String fieldKey = fields.next().getKey();

      if (!StringUtils.isBlank(SalesforceConstants.getOpportunityFieldName(fieldKey))) {
        if (StringUtils.isEmpty(updatedFields)) {
          updatedFields = SalesforceConstants.getOpportunityFieldName(fieldKey);
        } else {
          updatedFields = updatedFields + ", " + SalesforceConstants.getOpportunityFieldName(fieldKey);
        }
      }
    }

    return presentationFormat(FORMATTED_STRING, updatedFields);
  }

}