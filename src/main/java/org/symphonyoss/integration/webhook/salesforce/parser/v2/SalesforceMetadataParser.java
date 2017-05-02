package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

  private static final String LABELS_TYPE = "com.symphony.integration.salesforce.label";

  public static final String OPPORTUNITY_NOTIFICATION_JSON = "opportunityNotificationJSON";

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
    processCloseDate(input);
    processAmount(input);
    processUpdatedFields(input);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // Do nothing
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

  private void processAmount(JsonNode input) {
    JsonNode amountNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    String amount = amountNode.path(SalesforceConstants.AMOUNT).asText(null);

    if (!StringUtils.isEmpty(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);

      ((ObjectNode) amountNode).put(SalesforceConstants.AMOUNT, amount);
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
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.UPDATED_FIELDS_NODE, updatedFields);
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.UPDATED_NOTIFICATION);
    } else {
      ((ObjectNode) updatedFieldsNode).put(SalesforceConstants.CREATED_OR_UPDATED, SalesforceConstants.CREATED_NOTIFICATION);
    }
  }

}