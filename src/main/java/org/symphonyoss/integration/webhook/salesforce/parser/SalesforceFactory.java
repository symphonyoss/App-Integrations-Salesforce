package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;

import java.util.List;

/**
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceFactory implements WebHookParserFactory {


  /**
   * Update the integration username on each parser class. This process is required to know which user
   * must be used to query the Symphony API's.
   * @param settings Integration settings
   */
  @Override
  public void onConfigChange(IntegrationSettings settings) {
    String salesforceUser = settings.getType();

    for (SalesforceParser parser : getBeans()) {
      parser.setSalesforceUser(salesforceUser);
    }
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    //TODO
    return null;
  }

  public SalesforceParser getParser(JsonNode node) {
    return null;
  }

  /**
   * Get a list of parsers supported by the factory.
   * @return list of parsers supported by the factory.
   */
  protected abstract List<SalesforceParser> getBeans();

}
