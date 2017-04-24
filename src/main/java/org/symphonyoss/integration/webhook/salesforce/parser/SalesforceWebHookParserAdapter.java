package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Adapt the interface {@link WebHookParser} to {@link SalesforceParser}.
 *
 * {@link WebHookParser} is the common interface implemented by all the parsers that support MessageML v2.
 * {@link SalesforceParser} is the interface implemented by all the JIRA parsers.
 *
 * Created by crepache on 24/04/17.
 */
public class SalesforceWebHookParserAdapter implements WebHookParser {

  private SalesforceParser parser;

  public SalesforceWebHookParserAdapter(SalesforceParser parser) {
    this.parser = parser;
  }

  @Override
  public List<String> getEvents() {
    return parser.getEvents();
  }

  @Override
  public Message parse(WebHookPayload payload) throws WebHookParseException {
    try {
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());
      Map<String, String> parameters = payload.getParameters();

      return parser.parse(parameters, rootNode);
    } catch (IOException e) {
      throw new SalesforceParseException("Something went wrong while trying to convert your message to the expected format", e);
    }
  }

}
