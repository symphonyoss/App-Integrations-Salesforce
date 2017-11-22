package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser factory for the MessageML v2.
 *
 * Created by crepache on 19/04/17.
 */
@Component
public class V2SalesforceParserFactory extends SalesforceParserFactory {

  @Autowired
  private List<SalesforceMetadataParser> beans;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V2.equals(version);
  }

  @Override
  protected List<SalesforceParser> getBeans() {
    return new ArrayList<SalesforceParser>(beans);
  }
}
