package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOG = LoggerFactory.getLogger(SalesforceMetadataParser.class);

  private static final String METADATA_FILE = "metadataOpportunityNotification.xml";

  private static final String TEMPLATE_FILE = "templateOpportunityNotification.xml";

  private static final String OPPORTUNITY_NOTIFICATION_JSON = "opportunityNotificationJSON";

  private static final String DEFAULT_VALUE_NULL = "-";

  private static final String CROWN_ICON = "new_opportunity.svg";

  private static final String SEPARATOR = " ";

  private static final String COMMA_SEPARATOR = ", ";

  private static final String HYPHEN_SEPARATOR = " - ";

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
    return Arrays.asList(OPPORTUNITY_NOTIFICATION_JSON);
  }

  @Override
  protected void preProcessInputData(JsonNode node) {
    JsonNode currentOpportunityNode = node.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);
    processNodesObjects(currentOpportunityNode);
    processAmountAndCurrencyIsoCode(currentOpportunityNode);
    processCloseDate(currentOpportunityNode);
    processURLIcon(currentOpportunityNode);
    processCrownIcon(currentOpportunityNode);

    JsonNode currentOpportunityOwnerNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_OWNER);
    processOwner(currentOpportunityOwnerNode);
    processOwnerNameAndEmailFormatted(currentOpportunityOwnerNode);

    JsonNode currentOpportunityAccountNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_ACCOUNT);
    processAccountName(currentOpportunityAccountNode);

    JsonNode currentOpportunityLastModifiedBy = currentOpportunityNode.path(SalesforceConstants.LAST_MODIFY_BY);
    processLastModifiedBy(currentOpportunityLastModifiedBy);

    JsonNode previousOpportunityNode = node.path(SalesforceConstants.PREVIOUS_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);
    processUpdatedFields(currentOpportunityNode, previousOpportunityNode);
  }

  private void processLastModifiedBy(JsonNode node) {
    String lastModifiedByEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (!StringUtils.isEmpty(lastModifiedByEmail) && emailExistsAtSymphony(lastModifiedByEmail)) {
      ((ObjectNode) node).put(SalesforceConstants.HAS_LAST_MODIFIED_BY_AT_SYMPHONY, Boolean.TRUE);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.HAS_LAST_MODIFIED_BY_AT_SYMPHONY, Boolean.FALSE);
    }
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
  }

  private void processNodesObjects(JsonNode node) {
    if (!node.has(SalesforceConstants.OPPORTUNITY_OWNER)) {
      ((ObjectNode) node).putObject(SalesforceConstants.OPPORTUNITY_OWNER);
    }

    if (!node.has(SalesforceConstants.OPPORTUNITY_ACCOUNT)) {
      ((ObjectNode) node).putObject(SalesforceConstants.OPPORTUNITY_ACCOUNT);
    }

    if (!node.has(SalesforceConstants.LAST_MODIFY_BY)) {
      ((ObjectNode) node).putObject(SalesforceConstants.LAST_MODIFY_BY);
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

  private void processOwnerNameAndEmailFormatted(JsonNode node) {
    String ownerNameAndEmailFormatted = EMPTY;

    String ownerName = node.path(SalesforceConstants.NAME).asText(EMPTY);
    if (!StringUtils.isEmpty(ownerName)) {
      ownerNameAndEmailFormatted = ownerName;
    }

    String ownerEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);
    if (!StringUtils.isEmpty(ownerEmail)) {
      if (!StringUtils.isEmpty(ownerNameAndEmailFormatted)) {
        ownerNameAndEmailFormatted = ownerNameAndEmailFormatted + HYPHEN_SEPARATOR + ownerEmail;
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

  private void processAmountAndCurrencyIsoCode(JsonNode node) {
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
      formatOptionalField(node, SalesforceConstants.NAME, accountName, DEFAULT_VALUE_NULL);
    }
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(integrationUser, emailAddress);
    return user.getId() != null;
  }

  private void processCloseDate(JsonNode node) {
    String closeDateFormat = node.path(SalesforceConstants.CLOSE_DATE).asText(null);
    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);

    if (!StringUtils.isEmpty(closeDateFormat)) {
      try {
        closeDateFormat = formatter.format(formatter.parse(closeDateFormat));
      } catch (ParseException e) {
        LOG.warn("Couldn't parser date.");
      }

      ((ObjectNode) node).put(SalesforceConstants.CLOSE_DATE, closeDateFormat);
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
          updatedFields = updatedFields + COMMA_SEPARATOR + SalesforceConstants.getOpportunityFieldName(fieldKey);
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

  private void processCrownIcon(JsonNode node) {
    String crownIcon = getURLFromIcon(CROWN_ICON);

    if (!crownIcon.isEmpty()) {
      ((ObjectNode) node).put(SalesforceConstants.CROWN_ICON, crownIcon);
    }
  }

}
