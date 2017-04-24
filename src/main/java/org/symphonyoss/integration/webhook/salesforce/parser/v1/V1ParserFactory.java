package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.salesforce.parser.BaseSalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crepache on 19/04/17.
 */
@Component
public class V1ParserFactory extends SalesforceFactory {

  @Autowired
  private List<BaseSalesforceParser> beans;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V1.equals(version);
  }

  @Override
  protected List<SalesforceParser> getBeans() {
    return new ArrayList<SalesforceParser>(beans);
  }

}