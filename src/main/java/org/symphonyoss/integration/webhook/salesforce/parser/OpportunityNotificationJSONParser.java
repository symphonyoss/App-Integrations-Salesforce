package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class responsible to handle the Opportunity Notification event from Salesforce (in JSON format)
 *
 * Created by crepache on 3/24/17.
 */
@Component
public class OpportunityNotificationJSONParser extends BaseSalesforceParser
    implements SalesforceParser {

  protected static final String OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT =
      "%s %s - %s <b>%s</b> %s<br/>%s %s<br/>%s %s<br/>%s %s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s";

  @Override
  public List<String> getEvents() {
    return Arrays.asList("opportunityNotificationJSON");
  }

  @Override
  public String parse(Entity entity) throws SalesforceParseException {
    throw new SalesforceParseException("Parser used for XML format but not used for JSON.");
  }

  @Override
  public String parse(Map<String, String> parameters, JsonNode node)
      throws SalesforceParseException {
    SafeString presentationML = getPresentationML(node);

    return presentationML.toString();
  }

  /**
   * Returns the presentationML for an opportunity notification payload.
   * @param node the opportunity notification payload
   * @return presentationML for the notification
   * @throws SalesforceParseException in case an error occurs while parsing the payload
   */
  private SafeString getPresentationML(JsonNode node) throws SalesforceParseException {
    String typeEvent = SalesforceConstants.CREATED;
    JsonNode fieldsCurrent = node.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    SafeString name = getNameFormatted(fieldsCurrent);
    SafeString link = getLinkFormatted(fieldsCurrent);
    SafeString emailLastModifyBy = getEmailLastModifiedByFormatted(fieldsCurrent);
    SafeString accountName = getAccountNameFormatted(fieldsCurrent);
    SafeString accountEmail = getAccountLinkedFormatted(fieldsCurrent);
    SafeString ownerName = getOwnerNameFormatted(fieldsCurrent);
    SafeString ownerEmail = getOwnerEmailFormatted(fieldsCurrent);
    SafeString amount = getAmountFormatted(fieldsCurrent);
    SafeString currencyIsoCode = getCurrencyIsoCodeFormatted(fieldsCurrent);
    SafeString closeDate = getCloseDateFormatted(fieldsCurrent);
    SafeString nextStep = getNextStepFormatted(fieldsCurrent);
    SafeString type = getTypeFormatted(fieldsCurrent);
    SafeString stageName = getStageNameFormatted(fieldsCurrent);
    SafeString probability = getProbabilityFormatted(fieldsCurrent);

    JsonNode fieldsPrevious = node.path(SalesforceConstants.PREVIOUS_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    SafeString fieldsUpdated = null;
    if (fieldsPrevious.size() > 0) {
      typeEvent = SalesforceConstants.UPDATED;

      fieldsUpdated = getUpdatedFields(fieldsPrevious);
    }

    return ParserUtils.presentationFormat(OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT, name, link, emailLastModifyBy, typeEvent, fieldsUpdated, accountName, accountEmail,
        ownerName, ownerEmail, amount, currencyIsoCode, closeDate, nextStep, type, stageName, probability);
  }

}