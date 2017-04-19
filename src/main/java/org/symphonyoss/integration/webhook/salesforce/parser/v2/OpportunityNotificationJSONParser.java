package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.media.sound.MidiUtils;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;

/**
 * Created by crepache on 19/04/17.
 */
public class OpportunityNotificationJSONParser extends MetadataParser {

  private static final String METADATA_FILE = "metadataIssueCreated.xml";

  private static final String TEMPLATE_FILE = "templateIssueCreated.xml";

  @Override
  protected void preProcessInputData(JsonNode input) {
    // TODO implements method
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
    // TODO implements method
  }

  @Override
  protected String getTemplateFile() {
    return TEMPLATE_FILE;
  }

  @Override
  protected String getMetadataFile() {
    return METADATA_FILE;
  }
}
