package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser factory for the MessageML v2.
 *
 * Created by crepache on 19/04/17.
 */
public class ParserFactory extends SalesforceFactory  {

  @Autowired
  private List<SalesforceMetadataParser> beans;

  @Autowired
  private org.symphonyoss.integration.webhook.salesforce.parser.v1.ParserFactory fallbackFactory;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V2.equals(version);
  }

  @Override
  protected List<SalesforceParser> getBeans() {
    return new ArrayList<SalesforceParser>(beans);
  }

  @Override
  public SalesforceParser getParser(JsonNode node) {
    SalesforceParser result = super.getParser(node);

    if (result == null) {
      return fallbackFactory.getParser(node);
    }

    return result;
  }

}
