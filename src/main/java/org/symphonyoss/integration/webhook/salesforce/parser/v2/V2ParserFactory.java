package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1ParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser factory for the MessageML v2.
 *
 * Created by crepache on 19/04/17.
 */
@Component
public class V2ParserFactory extends SalesforceFactory {

  @Autowired
  private List<SalesforceMetadataParser> beans;

  @Autowired
  private V1ParserFactory fallbackFactory;

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
