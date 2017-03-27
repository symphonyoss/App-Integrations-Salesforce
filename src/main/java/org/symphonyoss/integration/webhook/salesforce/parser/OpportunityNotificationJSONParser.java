package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;
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

  protected static final String OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT = "%s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s<br/>%s";

  @Autowired
  private UserService userService;

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

    SafeString name = getNameFormatted(fields);
    SafeString ownerName = getOwnerNameFormatted(fields);
    SafeString ownerEmail = getOwnerEmailFormatted(fields);
    SafeString type = getTypeFormatted(fields);
    SafeString link = getLinkFormatted(fields);
    SafeString stage = getStageNameFormatted(fields);
    SafeString closeDate = getCloseDateFormatted(fields);
    SafeString accountName = getAccountNameFormatted(fields);
    SafeString accountEmail = getAccountLinkFormatted(fields);

    return ParserUtils.presentationFormat(OPPORTUNITY_NOTIFICATION_FORMATTED_TEXT, name, link, stage, accountName, accountEmail, ownerName, ownerEmail, type, closeDate );
  }

}