package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by crepache on 19/04/17.
 */
@Component
public class OpportunityNotificationMetadataParser extends SalesforceMetadataParser {

  private static final String METADATA_FILE = "metadataOpportunityNotification.xml";

  private static final String TEMPLATE_FILE = "templateOpportunityNotification.xml";

  public static final String DEFAULT_VALUE_NULL = "-";

  public static final String SEPARATOR = " - ";

  public OpportunityNotificationMetadataParser(UserService userService, IntegrationProperties integrationProperties) {
    super(userService, integrationProperties);
  }

  @Override
  protected String getTemplateFile() {
    return TEMPLATE_FILE;
  }

  @Override
  protected String getMetadataFile() {
    return METADATA_FILE;
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList("opportunityNotificationJSON");
  }

  @Override
  protected void preProcessInputData(JsonNode node) {
    JsonNode currentOpportunityNode = node.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);
    proccessNodesObjects(currentOpportunityNode);
    proccessAmountAndCurrencyIsoCode(currentOpportunityNode);
    proccessCloseDate(currentOpportunityNode);
    proccessURLIconIntegration(currentOpportunityNode);
    proccessIconCrown(currentOpportunityNode);

    JsonNode currentOpportunityOwnerNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_OWNER);
    processOwner(currentOpportunityOwnerNode);
    proccessOwnerNameAndEmailFormatted(currentOpportunityOwnerNode);

    JsonNode currentOpportunityAccountNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_ACCOUNT);
    processAccountName(currentOpportunityAccountNode);

    JsonNode previousOpportunityNode = node.path(SalesforceConstants.PREVIOUS_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);
    processUpdatedFields(currentOpportunityNode, previousOpportunityNode);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
  }

  private void proccessNodesObjects(JsonNode node) {
    if (node.path(SalesforceConstants.OPPORTUNITY_OWNER).getNodeType() == JsonNodeType.MISSING) {
      ((ObjectNode) node).putObject(SalesforceConstants.OPPORTUNITY_OWNER);
    }

    if (node.path(SalesforceConstants.OPPORTUNITY_ACCOUNT).getNodeType() == JsonNodeType.MISSING) {
      ((ObjectNode) node).putObject(SalesforceConstants.OPPORTUNITY_ACCOUNT);
    }
  }

  private void processOwner(JsonNode node) {
    String ownerEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (!StringUtils.isEmpty(ownerEmail) && emailExistsAtSymphony(ownerEmail)) {
      ((ObjectNode) node).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.TRUE);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.FALSE);
    }
  }

  private void proccessOwnerNameAndEmailFormatted(JsonNode node) {
    String ownerNameAndEmailFormatted = EMPTY;

    String ownerName = node.path(SalesforceConstants.NAME).asText(EMPTY);
    if (!StringUtils.isEmpty(ownerName)) {
      ownerNameAndEmailFormatted = ownerName;
    }

    String ownerEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);
    if (!StringUtils.isEmpty(ownerEmail)) {
      if (!StringUtils.isEmpty(ownerNameAndEmailFormatted)) {
        ownerNameAndEmailFormatted = ownerNameAndEmailFormatted + SEPARATOR + ownerEmail;
      } else {
        ownerNameAndEmailFormatted = ownerEmail;
      }
    }

   ((ObjectNode) node).put(SalesforceConstants.NAME_AND_EMAIL, ownerNameAndEmailFormatted);
  }

  private String getAmountFormatted(JsonNode node) {
    String amount = node.path(SalesforceConstants.AMOUNT).asText(EMPTY);

    if (!StringUtils.isEmpty(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);
    }

    return amount;
  }

  private void proccessAmountAndCurrencyIsoCode(JsonNode node) {
    String amountAndCurrencyIsoCodeFormatted = EMPTY;

    String amount = getAmountFormatted(node);
    if (!StringUtils.isEmpty(amount)) {
      amountAndCurrencyIsoCodeFormatted = amount;
    }

    String currencyIsoCode = node.path(SalesforceConstants.CURRENCY_ISO_CODE).asText(EMPTY);
    if (!StringUtils.isEmpty(currencyIsoCode)) {
      if (!StringUtils.isEmpty(amountAndCurrencyIsoCodeFormatted)) {
        amountAndCurrencyIsoCodeFormatted = amountAndCurrencyIsoCodeFormatted + SEPARATOR + currencyIsoCode;
      } else {
        amountAndCurrencyIsoCodeFormatted = currencyIsoCode;
      }
    }

    ((ObjectNode) node).put(SalesforceConstants.AMOUNT_AND_CURRENCY_ISO_CODE, amountAndCurrencyIsoCodeFormatted);
  }

  private void processAccountName(JsonNode node) {
    String accountName = node.path(SalesforceConstants.NAME).asText(EMPTY);

    if (StringUtils.isEmpty(accountName)) {
      ((ObjectNode) node).put(SalesforceConstants.NAME, DEFAULT_VALUE_NULL);
    }
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(integrationUser, emailAddress);
    return user.getId() != null;
  }

  private void proccessCloseDate(JsonNode node) {
    String closeDateFormat = node.path(SalesforceConstants.CLOSE_DATE).asText(null);
    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);

    if (!StringUtils.isEmpty(closeDateFormat)) {
      try {
        closeDateFormat = formatter.format(formatter.parse(closeDateFormat));
      } catch (ParseException e) {
        // Do nothing
      }

      ((ObjectNode) node).put(SalesforceConstants.CLOSE_DATE, closeDateFormat);
    }
  }

  private void proccessURLIconIntegration(JsonNode node) {
    String urlIconIntegration = getURLFromIcon("salesforce.svg");

    if (!urlIconIntegration.isEmpty()) {
      ((ObjectNode) node).put(SalesforceConstants.URL_ICON_INTEGRATION, urlIconIntegration);
    }
  }

  private void processUpdatedFields(JsonNode currentNode, JsonNode previousNode) {
    String updatedFields = null;

    Iterator<Map.Entry<String, JsonNode>> fields = previousNode.fields();
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
      ((ObjectNode) currentNode).put(SalesforceConstants.UPDATED_FIELDS, updatedFields);
      ((ObjectNode) currentNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.UPDATED_NOTIFICATION);
    } else {
      ((ObjectNode) currentNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.CREATED_NOTIFICATION);
    }
  }
}
