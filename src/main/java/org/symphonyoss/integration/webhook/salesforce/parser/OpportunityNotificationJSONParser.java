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
 * Class responsable to handle the Opportunity Notification event of Salesforce
 * with format JSON
 *
 * Created by crepache on 3/24/17.
 */
@Component
public class OpportunityNotificationJSONParser extends BaseSalesforceParser
    implements SalesforceParser {

  protected static final String OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT = "%s %s<br/>%s %s<br/>%s %s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s";

  @Override
  public List<String> getEvents() {
    return Arrays.asList("opportunityNotificationJSON");
  }

  @Override
  public String parse(Entity entity) throws SalesforceParseException {
    return null;
  }

  @Override
  public String parse(Map<String, String> parameters, JsonNode node)
      throws SalesforceParseException {
    SafeString presentationML = getPresentationML(node);

    return presentationML.toString();
  }

  /**
   * Returns the presentationML for created OpportunityNotification with integration JSON.
   * @param node
   * @return presentationML
   * @throws SalesforceParseException
   */
  private SafeString getPresentationML(JsonNode node) throws SalesforceParseException {
    JsonNode fields = node.path(SalesforceConstants.OPPORTUNITY_PATH).path(SalesforceConstants.FIELDS_PATH);

    SafeString accountName = getAccountNameFormatted(fields);
    SafeString accountEmail = getAccountLinkedFormatted(fields);
    SafeString ownerName = getOwnerNameFormatted(fields);
    SafeString ownerEmail = getOwnerEmailFormatted(fields);
    SafeString amount = getAmountFormatted(fields);
    SafeString currencyIsoCode = getCurrencyIsoCodeFormatted(fields);
    SafeString closeDate = getCloseDateFormatted(fields);
    SafeString nextStep = getNextStepFormatted(fields);
    SafeString type = getTypeFormatted(fields);
    SafeString stageName = getStageNameFormatted(fields);
    SafeString probability = getProbabilityFormatted(fields);

    return ParserUtils.presentationFormat(OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT, accountName, accountEmail, ownerName, ownerEmail, amount, currencyIsoCode, closeDate, nextStep, type, stageName, probability);
  }

}