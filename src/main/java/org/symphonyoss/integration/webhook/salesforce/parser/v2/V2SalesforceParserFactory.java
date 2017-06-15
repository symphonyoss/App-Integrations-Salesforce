package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParserFactory;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;
import org.symphonyoss.integration.webhook.salesforce.parser.v1.V1SalesforceParserFactory;

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

  @Autowired
  private V1SalesforceParserFactory fallbackFactory;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V2.equals(version);
  }

  @Override
  protected List<SalesforceParser> getBeans() {
    return new ArrayList<SalesforceParser>(beans);
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    WebHookParser result = super.getParser(payload);

    if (result == null) {
      return fallbackFactory.getParser(payload);
    }

    return result;
  }
}
