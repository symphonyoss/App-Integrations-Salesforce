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

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

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
      if (isContentTypeJSON(payload)) {
        JsonNode rootNode = JsonUtils.readTree(payload.getBody());
        Map<String, String> parameters = payload.getParameters();

        return parser.parse(parameters, rootNode);
      }

      return parser.parse(payload);
    } catch (IOException e) {
      throw new SalesforceParseException("Something went wrong while trying to convert your message to the expected format", e);
    } catch (JAXBException e) {
      throw new SalesforceParseException("Something went wrong when trying parse the MessageML payload received by the webhook.", e);
    }
  }

  /**
   * Rerieves the Content-Type header from the webhook payload.
   * @param payload the webhook payload
   * @return value of Content-Type header
   */
  private String getContentType(WebHookPayload payload) {
    return payload.getHeaders().get("content-type");
  }

  /**
   * Returns true when the payload content is JSON
   * @param payload the webhook payload
   * @return true when payload is JSON
   */
  private boolean isContentTypeJSON(WebHookPayload payload) {
    return MediaType.APPLICATION_JSON.equals(getContentType(payload));
  }

}