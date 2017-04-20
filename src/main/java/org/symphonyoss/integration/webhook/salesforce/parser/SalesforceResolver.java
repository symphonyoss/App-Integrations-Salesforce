package org.symphonyoss.integration.webhook.salesforce.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;
import org.symphonyoss.integration.webhook.parser.WebHookParserResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the parser factory should be used based on MessageML version supported by Agent.
 *
 * Created by crepache on 19/04/17.
 */
public class SalesforceResolver extends WebHookParserResolver {

  @Autowired
  private List<SalesforceFactory> factories;

  /**
   * Retrieve all parser factories of SALESFORCE integration for all MessageML versions.
   * @return Parser factories
   */
  @Override
  protected List<WebHookParserFactory> getFactories() {
    return new ArrayList<WebHookParserFactory>(factories);
  }

}
