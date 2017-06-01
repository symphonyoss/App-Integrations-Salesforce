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

  private static final String PATH_IMG = "img";

  private static final String INTEGRATION_NAME = "salesforce";

  public static final String SALESFORCE_LOGO = "salesforce.svg";

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

  protected void formatOptionalField(JsonNode node, String nodeName, String nodeValue, String defaultValue) {
    if (StringUtils.isEmpty(nodeValue)) {
      ((ObjectNode) node).put(nodeName, defaultValue);
    }
  }

  /**
   * Add an entry for Salesforce icon in the JSON node.
   * @param node JSON node to have the icon added in.
   */
  protected void processURLIcon(JsonNode node) {
    String urlIconIntegration = getURLFromIcon(SALESFORCE_LOGO);

    if (!urlIconIntegration.isEmpty()) {
      ((ObjectNode) node).put(SalesforceConstants.URL_ICON_INTEGRATION, urlIconIntegration);
    }
  }
}