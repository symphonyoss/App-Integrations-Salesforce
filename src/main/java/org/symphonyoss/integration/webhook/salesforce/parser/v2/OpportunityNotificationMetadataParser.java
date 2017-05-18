package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.media.sound.MidiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

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
    JsonNode currentOpportunityOwnerNode = currentOpportunityNode.path(SalesforceConstants.OPPORTUNITY_OWNER);
    JsonNode previousOpportunityNode = node.path(SalesforceConstants.PREVIOUS_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    processOwner(currentOpportunityOwnerNode);
    processAmount(currentOpportunityNode);
    processCloseDate(currentOpportunityNode);
    proccessURLIconIntegration(currentOpportunityNode);
    proccessIconCrown(currentOpportunityNode);
    processUpdatedFields(currentOpportunityNode, previousOpportunityNode);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
  }

  private void processOwner(JsonNode node) {
    String ownerEmail = node.path(SalesforceConstants.EMAIL).asText(EMPTY);

    if (!StringUtils.isEmpty(ownerEmail) && emailExistsAtSymphony(ownerEmail)) {
      ((ObjectNode) node).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.TRUE);
    } else {
      ((ObjectNode) node).put(SalesforceConstants.HAS_OWNER_AT_SYMPHONY, Boolean.FALSE);
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
    }
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
