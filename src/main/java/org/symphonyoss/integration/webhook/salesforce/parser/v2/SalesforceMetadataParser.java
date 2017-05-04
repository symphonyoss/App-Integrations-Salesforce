package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceMetadataParser extends MetadataParser implements SalesforceParser {

  public static final String DEFAULT_VALUE_NULL = "-";

  private UserService userService;

  private String integrationUser;

  @Autowired
  public SalesforceMetadataParser(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void setSalesforceUser(String user) {
    this.integrationUser = user;
  }

  @Override
  public Message parse(WebHookPayload payload) throws SalesforceParseException {
    return null;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node) throws SalesforceParseException {
    return parse(node);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    processName(input);
    processLink(input);
    proccessEmailLastModifiedBy(input);
    proccessAccountName(input);
    proccessAccountLink(input);
    processOwner(input);
    processAmount(input);
    processCurrencyIsoCode(input);
    processCloseDate(input);
    processNextStep(input);
    processStageName(input);
    processProbability(input);
    processUpdatedFields(input);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
  }

  private void processName(JsonNode input) {
    JsonNode nameNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String nameFormat = nameNode.path(SalesforceConstants.NAME).asText(EMPTY);

    if (StringUtils.isEmpty(nameFormat)) {
      ((ObjectNode) nameNode).put(SalesforceConstants.NAME, DEFAULT_VALUE_NULL);
    }
  }

  private void processLink(JsonNode input) {
    JsonNode linkNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String linkFormat = linkNode.path(SalesforceConstants.LINK).asText(EMPTY);

    if (StringUtils.isEmpty(linkFormat)) {
      ((ObjectNode) linkNode).put(SalesforceConstants.LINK, EMPTY);
    }
  }

  private void proccessEmailLastModifiedBy(JsonNode input) {
    JsonNode emailLastModifiedByNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String emailLastModifiedByFormat = emailLastModifiedByNode.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (StringUtils.isEmpty(emailLastModifiedByFormat)) {
      ((ObjectNode) emailLastModifiedByNode).put(SalesforceConstants.EMAIL, EMPTY);
    }
  }

  private void proccessAccountName(JsonNode input) {
    JsonNode accountNameNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY).path(SalesforceConstants.OPPORTUNITY_ACCOUNT);

    String accountNameFormat = accountNameNode.path(SalesforceConstants.NAME).asText(EMPTY);

    if (StringUtils.isEmpty(accountNameFormat)) {
      ((ObjectNode) accountNameNode).put(SalesforceConstants.NAME, EMPTY);
    }
  }

  private void proccessAccountLink(JsonNode input) {
    JsonNode accountLinkNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY).path(SalesforceConstants.OPPORTUNITY_ACCOUNT);

    String accountLinkFormat = accountLinkNode.path(SalesforceConstants.LINK).asText(EMPTY);

    if (StringUtils.isEmpty(accountLinkFormat)) {
      ((ObjectNode) accountLinkNode).put(SalesforceConstants.LINK, EMPTY);
    }
  }

  private void processOwner(JsonNode input) {
    JsonNode ownerNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY).path(SalesforceConstants.OPPORTUNITY_OWNER);

    String ownerEmail = ownerNode.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (!StringUtils.isEmpty(ownerEmail) && emailExistsAtSymphony(ownerEmail)) {
      ((ObjectNode) ownerNode).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.TRUE);
    }
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(integrationUser, emailAddress);
    return user.getId() != null;
  }

  private void processAmount(JsonNode input) {
    JsonNode amountNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String amount = amountNode.path(SalesforceConstants.AMOUNT).asText(DEFAULT_VALUE_NULL);

    if (!StringUtils.isEmpty(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);

      ((ObjectNode) amountNode).put(SalesforceConstants.AMOUNT, amount);
    }
  }

  private void processCurrencyIsoCode(JsonNode input) {
    JsonNode currencyIsoCodeNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String currencyIsoCode = currencyIsoCodeNode.path(SalesforceConstants.CURRENCY_ISO_CODE).asText(EMPTY);

    if (StringUtils.isEmpty(currencyIsoCode)) {
      ((ObjectNode) currencyIsoCodeNode).put(SalesforceConstants.CURRENCY_ISO_CODE, EMPTY);
    }
  }

  /**
   * This method change the issue status to uppercase.
   *
   * @param input JSON input payload
   */
  private void processCloseDate(JsonNode input) {
    JsonNode closeDataNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String closeDateFormat = closeDataNode.path(SalesforceConstants.CLOSE_DATE).asText(null);
    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);


    if (!StringUtils.isEmpty(closeDateFormat)) {
      try {
        closeDateFormat = formatter.format(formatter.parse(closeDateFormat));
      } catch (ParseException e) {
        // Do nothing
      }

      ((ObjectNode) closeDataNode).put(SalesforceConstants.CLOSE_DATE, closeDateFormat);
    }
  }

  private void processNextStep(JsonNode input) {
    JsonNode nextStepNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String nextStep = nextStepNode.path(SalesforceConstants.NEXT_STEP).asText(EMPTY);

    if (StringUtils.isEmpty(nextStep)) {
      ((ObjectNode) nextStepNode).put(SalesforceConstants.NEXT_STEP, EMPTY);
    }
  }

  private void processStageName(JsonNode input) {
    JsonNode stageNameNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String stageName = stageNameNode.path(SalesforceConstants.STAGE_NAME).asText(EMPTY);

    if (StringUtils.isEmpty(stageName)) {
      ((ObjectNode) stageNameNode).put(SalesforceConstants.STAGE_NAME, EMPTY);
    }
  }

  private void processProbability(JsonNode input) {
    JsonNode probabilityNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String probability = probabilityNode.path(SalesforceConstants.PROBABILITY).asText(EMPTY);

    if (StringUtils.isEmpty(probability)) {
      ((ObjectNode) probabilityNode).put(SalesforceConstants.PROBABILITY, EMPTY);
    }
  }

  private void processUpdatedFields(JsonNode input) {
    JsonNode updatedFieldsNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String updatedFields = null;

    Iterator<Map.Entry<String, JsonNode>> fields = updatedFieldsNode.fields();
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

    if (!StringUtils.isEmpty(updatedFields)) {
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.UPDATED_FIELDS, updatedFields);
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.UPDATED_NOTIFICATION);
    } else {
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.CREATED_NOTIFICATION);
    }
  }

}