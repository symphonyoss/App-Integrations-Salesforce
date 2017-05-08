package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.media.sound.MidiUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by crepache on 19/04/17.
 */
@Component
public class OpportunityNotificationMetadataParser extends SalesforceMetadataParser {

  private static final String METADATA_FILE = "metadataOpportunityNotificationJSON.xml";

  private static final String TEMPLATE_FILE = "templateOpportunityNotificationJSON.xml";

  public OpportunityNotificationMetadataParser(UserService userService) {
    super(userService);
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

}
