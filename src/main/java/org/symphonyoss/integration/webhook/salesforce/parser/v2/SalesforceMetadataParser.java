package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.Application;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.SalesforceWebHookIntegration;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 *
 * Abstract SALESFORCE parser responsible to augment the SALESFORCE input data querying the user API and
 * pre-processing the input data.
 *
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceMetadataParser extends MetadataParser implements SalesforceParser {

  public static final String DEFAULT_VALUE_NULL = "-";

  private static final String PATH_IMG = "img";

  private static final String INTEGRATION_NAME = "salesforce";

  private IntegrationProperties integrationProperties;

  private UserService userService;

  private String integrationUser;

  @Autowired
  public SalesforceMetadataParser(UserService userService, IntegrationProperties integrationProperties) {
    this.userService = userService;
    this.integrationProperties = integrationProperties;
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

  protected void processName(JsonNode node) {
    String name = node.path(SalesforceConstants.NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NAME, name);
  }

  protected void processLink(JsonNode node) {
    String link = node.path(SalesforceConstants.LINK).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.LINK, link);
  }

  protected void proccessEmailLastModifiedBy(JsonNode node) {
    String emailLastModifiedBy = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.EMAIL, emailLastModifiedBy);
  }

  protected void proccessAccountName(JsonNode node) {
    String accountName = node.path(SalesforceConstants.NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NAME, accountName);
  }

  protected void proccessAccountLink(JsonNode node) {
    String accountLinkFormat = node.path(SalesforceConstants.LINK).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.LINK, accountLinkFormat);
  }

  protected void processOwner(JsonNode node) {
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

  protected void processAmount(JsonNode node) {
    String amount = node.path(SalesforceConstants.AMOUNT).asText(EMPTY);

    if (!StringUtils.isEmpty(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);

      ((ObjectNode) node).put(SalesforceConstants.AMOUNT, amount);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.AMOUNT, DEFAULT_VALUE_NULL);
    }
  }

  protected void processCurrencyIsoCode(JsonNode node) {
    String currencyIsoCode = node.path(SalesforceConstants.CURRENCY_ISO_CODE).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.CURRENCY_ISO_CODE, currencyIsoCode);
  }

  protected void processCloseDate(JsonNode node) {
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

  protected void processNextStep(JsonNode node) {
    String nextStep = node.path(SalesforceConstants.NEXT_STEP).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.NEXT_STEP, nextStep);
  }

  protected void processStageName(JsonNode node) {
    String stageName = node.path(SalesforceConstants.STAGE_NAME).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.STAGE_NAME, stageName);
  }

  protected void processProbability(JsonNode node) {
    String probability = node.path(SalesforceConstants.PROBABILITY).asText(EMPTY);

    formatOptionalField(node, SalesforceConstants.PROBABILITY, probability);
  }

  protected void proccessURLIconIntegration(JsonNode node) {
    ((ObjectNode) node).put(SalesforceConstants.URL_ICON_INTEGRATION, getURLFromIcon("salesforce.svgre"));
  }

  protected void proccessIconCrown(JsonNode node) {
    ((ObjectNode) node).put(SalesforceConstants.ICON_CROWN, getURLFromIcon("new_opportunity.svg"));
  }

  protected String getURLFromIcon(String iconName) {
    String urlBase = integrationProperties.getApplicationUrl(INTEGRATION_NAME);

    return String.format("%s/%s/%s", urlBase, PATH_IMG, iconName);
  }

  protected void processUpdatedFields(JsonNode currentNode, JsonNode previousNode) {
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

  private void formatOptionalField(JsonNode node, String nodeName, String nodeValue) {
    if (StringUtils.isEmpty(nodeValue)) {
      ((ObjectNode) node).put(nodeName, DEFAULT_VALUE_NULL);
    }
  }

}