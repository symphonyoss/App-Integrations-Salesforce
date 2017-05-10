package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by crepache on 26/04/17.
 */
@Component
public class NullSalesforceParser implements SalesforceParser {
  @Override
  public void setSalesforceUser(String user) {
    // Do nothing
  }

  @Override
  public List<String> getEvents() {
    return Collections.emptyList();
  }

  @Override
  public Message parse(WebHookPayload payload) throws SalesforceParseException {
    Message message = new Message();
    message.setMessage(payload.getBody());
    message.setFormat(Message.FormatEnum.MESSAGEML);
    message.setVersion(MessageMLVersion.V1);

    return message;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node)
      throws SalesforceParseException {
    return null;
  }
}
