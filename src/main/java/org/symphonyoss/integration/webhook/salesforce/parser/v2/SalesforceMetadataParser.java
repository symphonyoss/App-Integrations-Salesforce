package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceMetadataParser extends MetadataParser implements SalesforceParser {

  private static final String LABELS_TYPE = "com.symphony.integration.salesforce.label";

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
    processUpdatedFields(input);
  }

  private void processUpdatedFields(JsonNode input) {
    // implementing
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    includeLabels(output, input);
  }

  /**
   * Augment the output entity JSON with the JIRA labels.
   *
   * @param output Output Entity JSON
   * @param input JSON input data
   */
  private void includeLabels(EntityObject output, JsonNode input) {
    EntityObject outputOpportunityNotification = (EntityObject) output.getContent().get(SalesforceConstants.CURRENT_DATA_PATH);

    JsonNode labelsNode = input.path(SalesforceConstants.CURRENT_DATA_PATH).path(SalesforceConstants.OPPORTUNITY);

    if (labelsNode.size() == 0) {
      return;
    }

    List<EntityObject> list = new ArrayList<>();

    for (int i = 0; i < labelsNode.size(); i++) {
      String name = labelsNode.get(i).asText();
      String label = name.replace("#", "");

      EntityObject nestedObject = new EntityObject(LABELS_TYPE, getVersion());
      nestedObject.addContent(SalesforceConstants.TEXT_ENTITY_FIELD, label);

      list.add(nestedObject);
    }

    outputOpportunityNotification.addContent(SalesforceConstants.LABELS_ENTITY_FIELD, list);
  }

}