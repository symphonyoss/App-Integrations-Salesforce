package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.media.sound.MidiUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by crepache on 19/04/17.
 */
@Component
public class OpportunityNotificationJSONMetadataParser extends SalesforceMetadataParser {

  private static final String METADATA_FILE = "metadataOpportunityNotificationJSON.xml";

  private static final String TEMPLATE_FILE = "templateOpportunityNotificationJSON.xml";

  public OpportunityNotificationJSONMetadataParser(UserService userService) {
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
}
