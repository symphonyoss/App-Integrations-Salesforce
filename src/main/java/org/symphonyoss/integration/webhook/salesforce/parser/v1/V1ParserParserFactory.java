package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParserFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Parser factory for the MessageML v1.
 *
 * Created by crepache on 19/04/17.
 */
@Component
public class V1ParserParserFactory extends SalesforceParserFactory {

  @Autowired
  private List<CommonSalesforceParser> beans;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V1.equals(version);
  }

  @Override
  protected List<SalesforceParser> getBeans() {
    return new ArrayList<SalesforceParser>(beans);
  }

}
