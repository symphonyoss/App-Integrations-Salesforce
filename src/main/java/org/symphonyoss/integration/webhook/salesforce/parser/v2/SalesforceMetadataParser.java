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
 *
 * Abstract SALESFORCE parser responsible to augment the JIRA input data querying the user API and
 * pre-processing the input data.
 *
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
  protected void preProcessInputData(JsonNode node) {
    JsonNode currentOpportunityNode = node.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);
    JsonNode currentOpportunityAccountNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_ACCOUNT);
    JsonNode currentOpportunityOwnerNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_OWNER);
    JsonNode currentOpportunityLastModifyByNode = currentOpportunityNode.path(SalesforceConstants.LAST_MODIFY_BY);
    JsonNode previousOpportunityNode = node.path(SalesforceConstants.PREVIOUS_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    processName(currentOpportunityNode);
    processLink(currentOpportunityNode);
    proccessEmailLastModifiedBy(currentOpportunityLastModifyByNode);
    proccessAccountName(currentOpportunityAccountNode);
    proccessAccountLink(currentOpportunityAccountNode);
    processOwner(currentOpportunityOwnerNode);
    processAmount(currentOpportunityNode);
    processCurrencyIsoCode(currentOpportunityNode);
    processCloseDate(currentOpportunityNode);
    processNextStep(currentOpportunityNode);
    processStageName(currentOpportunityNode);
    processProbability(currentOpportunityNode);
    processUpdatedFields(currentOpportunityNode, previousOpportunityNode);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
  }

  private void processName(JsonNode node) {
    String name = node.path(SalesforceConstants.NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NAME, name);
  }

  private void processLink(JsonNode node) {
    String link = node.path(SalesforceConstants.LINK).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.LINK, link);
  }

  private void proccessEmailLastModifiedBy(JsonNode node) {
    String emailLastModifiedBy = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.EMAIL, emailLastModifiedBy);
  }

  private void proccessAccountName(JsonNode node) {
    String accountName = node.path(SalesforceConstants.NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NAME, accountName);
  }

  private void proccessAccountLink(JsonNode node) {
    String accountLinkFormat = node.path(SalesforceConstants.LINK).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.LINK, accountLinkFormat);
  }

  private void processOwner(JsonNode node) {
    String ownerEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (!StringUtils.isEmpty(ownerEmail) && emailExistsAtSymphony(ownerEmail)) {
      ((ObjectNode) node).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.TRUE);
    }
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(integrationUser, emailAddress);
    return user.getId() != null;
  }

  private void processAmount(JsonNode node) {
    String amount = node.path(SalesforceConstants.AMOUNT).asText(EMPTY);

    if (!StringUtils.isEmpty(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);

      ((ObjectNode) node).put(SalesforceConstants.AMOUNT, amount);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.AMOUNT, DEFAULT_VALUE_NULL);
    }
  }

  private void processCurrencyIsoCode(JsonNode node) {
    String currencyIsoCode = node.path(SalesforceConstants.CURRENCY_ISO_CODE).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.CURRENCY_ISO_CODE, currencyIsoCode);
  }

  private void processCloseDate(JsonNode node) {
    String closeDateFormat = node.path(SalesforceConstants.CLOSE_DATE).asText(null);
    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);


    if (!StringUtils.isEmpty(closeDateFormat)) {
      try {
        closeDateFormat = formatter.format(formatter.parse(closeDateFormat));
      } catch (ParseException e) {
        // Do nothing
      }

      ((ObjectNode) node).put(SalesforceConstants.CLOSE_DATE, closeDateFormat);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.CLOSE_DATE, DEFAULT_VALUE_NULL);
    }
  }

  private void processNextStep(JsonNode node) {
    String nextStep = node.path(SalesforceConstants.NEXT_STEP).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NEXT_STEP, nextStep);
  }

  private void processStageName(JsonNode node) {
    String stageName = node.path(SalesforceConstants.STAGE_NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.STAGE_NAME, stageName);
  }

  private void processProbability(JsonNode node) {
    String probability = node.path(SalesforceConstants.PROBABILITY).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.PROBABILITY, probability);
  }

  private void processUpdatedFields(JsonNode nodeCurrent, JsonNode nodePrevious) {
    String updatedFields = null;

    Iterator<Map.Entry<String, JsonNode>> fields = nodePrevious.fields();
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
      ((ObjectNode) nodeCurrent).put(SalesforceConstants.UPDATED_FIELDS, updatedFields);
      ((ObjectNode) nodeCurrent).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.UPDATED_NOTIFICATION);
    } else {
      ((ObjectNode) nodeCurrent).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.CREATED_NOTIFICATION);
    }
  }

  private void formatOptionalField(JsonNode node, String nameNode, String valueNode) {
    if (StringUtils.isEmpty(valueNode)) {
      ((ObjectNode) node).put(nameNode, DEFAULT_VALUE_NULL);
    }
  }

}