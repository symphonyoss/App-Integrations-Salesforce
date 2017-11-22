package org.symphonyoss.integration.webhook.salesforce.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.MessageML;
import org.symphonyoss.integration.entity.MessageMLParser;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceParserFactory implements WebHookParserFactory {

  public static final String OPPORTUNITY_NOTIFICATION_JSON = "opportunityNotificationJSON";

  private Map<String, SalesforceParser> parsers = new HashMap<>();

  @Autowired
  private NullSalesforceParser defaultSalesforceParser;

  /**
   * Map the event type to the parser.
   */
  @PostConstruct
  public void init() {
    for (SalesforceParser parser : getBeans()) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) throws WebHookParseException {
    if (isContentTypeJSON(payload)) {
      return new SalesforceWebHookParserAdapter(getParserJSON());
    }

    Entity mainEntity = parsePayloadToEntity(payload);
    String type = mainEntity.getType();
    SalesforceParser parser = getParser(type);

    if (parser == null) {
      parser = defaultSalesforceParser;
    }

    return new SalesforceWebHookParserAdapter(parser);
  }

  /**
   * Update the integration username on each parser class. This process is required to know which
   * user must be used to query the Symphony API's.
   * @param settings Integration settings
   */
  @Override
  public void onConfigChange(IntegrationSettings settings) {
    String salesforceUser = settings.getType();

    for (SalesforceParser parser : getBeans()) {
      parser.setSalesforceUser(salesforceUser);
    }
  }

  /**
   * Get a list of parsers supported by the factory.
   * @return list of parsers supported by the factory.
   */
  protected abstract List<SalesforceParser> getBeans();

  /**
   * Rerieves the Content-Type header from the webhook payload.
   * @param payload the webhook payload
   * @return value of Content-Type header
   */
  private String getContentType(WebHookPayload payload) {
    return payload.getHeaders().get("content-type");
  }

  /**
   * Returns true when the payload content-type is JSON
   * @param payload the webhook payload
   * @return true when payload is JSON
   */
  private boolean isContentTypeJSON(WebHookPayload payload) {
    return MediaType.APPLICATION_JSON.equals(getContentType(payload));
  }

  private Entity parsePayloadToEntity(WebHookPayload payload) {
    try {
      MessageML messageML = MessageMLParser.parse(payload.getBody());
      return messageML.getEntity();
    } catch (JAXBException e) {
      throw new SalesforceParseException(
          "Something went wrong when trying parse the MessageML payload received by the webhook.",
          e);
    }
  }

  private SalesforceParser getParserJSON() {
    return parsers.get(OPPORTUNITY_NOTIFICATION_JSON);
  }

  private SalesforceParser getParser(String type) {
    return parsers.get(type);
  }


}
