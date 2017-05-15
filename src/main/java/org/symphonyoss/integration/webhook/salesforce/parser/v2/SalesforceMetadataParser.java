package org.symphonyoss.integration.webhook.salesforce.parser.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.util.Map;

/**
 *
 * Abstract SALESFORCE parser responsible to augment the SALESFORCE input data querying the user API and
 * pre-processing the input data.
 *
 * Created by crepache on 19/04/17.
 */
public abstract class SalesforceMetadataParser extends MetadataParser implements SalesforceParser {

  public static final String DEFAULT_VALUE_NULL = "-";

  private static final String PATH_IMG = "img";

  private static final String INTEGRATION_NAME = "salesforce";

  private IntegrationProperties integrationProperties;

  protected UserService userService;

  protected String integrationUser;

  @Autowired
  public SalesforceMetadataParser(UserService userService, IntegrationProperties integrationProperties) {
    this.userService = userService;
    this.integrationProperties = integrationProperties;
  }

  @Override
  public void setSalesforceUser(String user) {
    this.integrationUser = user;
  }

  @Override
  public Message parse(WebHookPayload payload) throws SalesforceParseException {
    return null;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node) throws SalesforceParseException {
    return parse(node);
  }

  protected String getURLFromIcon(String iconName) {
    String urlBase = integrationProperties.getApplicationUrl(INTEGRATION_NAME);

    if (!urlBase.isEmpty()) {
      return String.format("%s/%s/%s", urlBase, PATH_IMG, iconName);
    } else {
      return StringUtils.EMPTY;
    }
  }

  protected void formatOptionalField(JsonNode node, String nodeName, String nodeValue) {
    if (StringUtils.isEmpty(nodeValue)) {
      ((ObjectNode) node).put(nodeName, DEFAULT_VALUE_NULL);
    }
  }

  protected void proccessIconCrown(JsonNode node) {
    String iconCrown = getURLFromIcon("new_opportunity.svg");

    if (!iconCrown.isEmpty()) {
      ((ObjectNode) node).put(SalesforceConstants.ICON_CROWN, iconCrown);
    }
  }
}