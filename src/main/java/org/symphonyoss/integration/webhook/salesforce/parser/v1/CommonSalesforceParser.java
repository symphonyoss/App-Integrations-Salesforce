package org.symphonyoss.integration.webhook.salesforce.parser.v1;

import static org.symphonyoss.integration.messageml.MessageMLFormatConstants.MESSAGEML_END;
import static org.symphonyoss.integration.messageml.MessageMLFormatConstants.MESSAGEML_START;
import static org.symphonyoss.integration.parser.ParserUtils.presentationFormat;
import static org.symphonyoss.integration.parser.SafeString.EMPTY_SAFE_STRING;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.messageml.MessageMLFormatConstants;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;
import org.symphonyoss.integration.webhook.salesforce.parser.SalesforceParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract class that contains the commons methods required by all the MessageML v1 parsers.
 *
 * Created by crepache on 24/04/17.
 */
public class CommonSalesforceParser implements SalesforceParser {

  private static final String FORMATTED_STRING_WITH_PARENTHESIS = "(%s)";
  private static final String FORMATTED_STRING = "%s";
  private static final String OPPORTUNITY_NAME = "<b>Opportunity:</b> %s";
  private static final String OPPORTUNITY_OWNER = "<b>Opportunity Owner:</b> %s";
  private static final String OPPORTUNITY_TYPE = "<b>Type:</b> %s";
  private static final String OPPORTUNITY_STAGE = "<b>Stage:</b> %s";
  private static final String OPPORTUNITY_CLOSE_DATE = "<b>Close Date:</b> %s";
  private static final String ACCOUNT_NAME = "<b>Account Name:</b> %s";
  private static final String OPPORTUNITY_AMOUNT = "<b>Amount:</b> %s";
  private static final String OPPORTUNITY_NEXT_STEP = "<b>Next Step:</b> %s";
  private static final String OPPORTUNITY_PROBABILITY = "<b>Probability:</b> %s";
  private static final String DEFAULT_CONTENT_FOR_MISSING_FIELD = "-";
  private static final String DEFAULT_VALUE_NULL = "";
  private static final String STRING_NULL = "null";

  @Autowired
  private UserService userService;

  private String salesforceUser;

  @Override
  public String parse(Entity entity) throws SalesforceParseException {
    return null;
  }

  @Override
  public void setSalesforceUser(String user) {
    this.salesforceUser = user;
  }

  @Override
  public List<String> getEvents() {
    return null;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node)
      throws SalesforceParseException {
    String formattedMessage = getMessage(parameters, node);

    if (StringUtils.isNotEmpty(formattedMessage)) {
      String messageML = MESSAGEML_START + formattedMessage + MESSAGEML_END;

      Message message = new Message();
      message.setFormat(Message.FormatEnum.MESSAGEML);
      message.setMessage(messageML);
      message.setVersion(MessageMLVersion.V1);

      return message;
    }

    return null;
  }

  protected String getMessage(Map<String, String> parameters, JsonNode node) throws SalesforceParseException {
    return null;
  }

  private String getName(JsonNode node) {
    return node.path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getNameFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_NAME, getName(node));
  }

  private String getLink(JsonNode node) {
    return node.path(SalesforceConstants.LINK).asText();
  }

  protected SafeString getLinkFormatted(JsonNode node) {
    String link = getLink(node);

    if (StringUtils.isEmpty(link)) {
      return EMPTY_SAFE_STRING;
    }

    SafeString finalUrl = presentationFormat(MessageMLFormatConstants.MESSAGEML_LINK_HREF_FORMAT, link);

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, finalUrl);
  }

  private String getOwnerName(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_OWNER).path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getOwnerNameFormatted(JsonNode node) {
    if (emailExistsAtSymphony(getOwnerEmail(node))) {
      return presentationFormat(OPPORTUNITY_OWNER, DEFAULT_VALUE_NULL);
    }

    return formatOptionalField(OPPORTUNITY_OWNER, getOwnerName(node));
  }

  private String getOwnerEmail(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_OWNER).path(SalesforceConstants.EMAIL).asText();
  }

  protected SafeString getOwnerEmailFormatted(JsonNode node) {
    String ownerEmail = getOwnerEmail(node);

    if (StringUtils.isBlank(ownerEmail)) {
      return EMPTY_SAFE_STRING;
    }

    if (emailExistsAtSymphony(ownerEmail)) {
      return presentationFormat(MessageMLFormatConstants.MESSAGEML_MENTION_EMAIL_FORMAT, ownerEmail);
    }

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, ownerEmail);
  }

  private String getType(JsonNode node) {
    return node.path(SalesforceConstants.TYPE).asText();
  }

  protected SafeString getTypeFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_TYPE, getType(node));
  }

  private String getStageName(JsonNode node) {
    return node.path(SalesforceConstants.STAGE_NAME).asText();
  }

  protected SafeString getStageNameFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_STAGE, getStageName(node));
  }


  private String getCloseDate(JsonNode node) {
    return node.path(SalesforceConstants.CLOSE_DATE).asText();
  }

  protected SafeString getCloseDateFormatted(JsonNode node) {
    String closeDate = getCloseDate(node);

    SimpleDateFormat formatter = new SimpleDateFormat(SalesforceConstants.TIMESTAMP_FORMAT);
    String closeDateFormat;

    try {
      closeDateFormat = formatter.format(formatter.parse(closeDate));
    } catch (ParseException e) {
      closeDateFormat = DEFAULT_CONTENT_FOR_MISSING_FIELD;
    }

    return presentationFormat(OPPORTUNITY_CLOSE_DATE, closeDateFormat);
  }

  private String getAccountName(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_ACCOUNT).path(SalesforceConstants.NAME).asText();
  }

  protected SafeString getAccountNameFormatted(JsonNode node) {
    return formatOptionalField(ACCOUNT_NAME, getAccountName(node));
  }

  private String getAccountLink(JsonNode node) {
    return node.path(SalesforceConstants.OPPORTUNITY_ACCOUNT).path(SalesforceConstants.LINK).asText();
  }

  protected SafeString getAccountLinkedFormatted(JsonNode node) {
    String accountLink = getAccountLink(node);

    if (StringUtils.isEmpty(accountLink)) {
      return EMPTY_SAFE_STRING;
    }

    SafeString finalUrl = presentationFormat(MessageMLFormatConstants.MESSAGEML_LINK_HREF_FORMAT, accountLink);

    return presentationFormat(FORMATTED_STRING_WITH_PARENTHESIS, finalUrl);
  }

  private String getAmount(JsonNode node) {
    return node.path(SalesforceConstants.AMOUNT).asText();
  }

  protected SafeString getAmountFormatted(JsonNode node) {
    String amount = getAmount(node);

    if (StringUtils.isNotBlank(amount)) {
      amount = NumberFormatUtils.formatValueWithLocale(Locale.US, amount);
    }

    return formatOptionalField(OPPORTUNITY_AMOUNT, amount);
  }

  private String getNextStep(JsonNode node) {
    return node.path(SalesforceConstants.NEXT_STEP).asText();
  }

  protected SafeString getNextStepFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_NEXT_STEP, getNextStep(node));
  }

  private String getProbability(JsonNode node) {
    return node.path(SalesforceConstants.PROBABILITY).asText();
  }

  protected SafeString getProbabilityFormatted(JsonNode node) {
    return formatOptionalField(OPPORTUNITY_PROBABILITY, getProbability(node));
  }

  private String getCurrencyIsoCode(JsonNode node) {
    return node.path(SalesforceConstants.CURRENCY_ISO_CODE).asText();
  }

  protected SafeString getCurrencyIsoCodeFormatted(JsonNode node) {
    String currencyIsoCode = getCurrencyIsoCode(node);

    if (StringUtils.isEmpty(getAmount(node))) {
      currencyIsoCode = StringUtils.EMPTY;
    }

    return presentationFormat(FORMATTED_STRING, currencyIsoCode);
  }

  private SafeString formatOptionalField(String format, String value) {
    if (value.isEmpty() || value.equals(STRING_NULL)) {
      value = DEFAULT_CONTENT_FOR_MISSING_FIELD;
    }

    return presentationFormat(format, value);
  }

  private boolean emailExistsAtSymphony(String emailAddress) {
    if (StringUtils.isBlank(emailAddress)) {
      return false;
    }

    User user = userService.getUserByEmail(salesforceUser, emailAddress);
    return user.getId() != null;
  }

  private String getEmailLastModifiedBy(JsonNode node) {
    return node.path(SalesforceConstants.LAST_MODIFY_BY).path(SalesforceConstants.EMAIL).asText();
  }

  protected SafeString getEmailLastModifiedByFormatted(JsonNode node) {
    String emailLastModifiedBy = getEmailLastModifiedBy(node);

    if (!emailExistsAtSymphony(emailLastModifiedBy)) {
      return EMPTY_SAFE_STRING;
    }

    return presentationFormat(MessageMLFormatConstants.MESSAGEML_MENTION_EMAIL_FORMAT, emailLastModifiedBy);
  }

  protected SafeString getUpdatedFields(JsonNode node) {
    String updatedFields = null;

    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext()) {

      String fieldKey = fields.next().getKey();

      if (!StringUtils.isBlank(SalesforceConstants.getOpportunityFieldName(fieldKey))) {
        if (StringUtils.isEmpty(updatedFields)) {
          updatedFields = SalesforceConstants.getOpportunityFieldName(fieldKey);
        } else {
          updatedFields = updatedFields + ", " + SalesforceConstants.getOpportunityFieldName(fieldKey);
        }
      }
    }

    return presentationFormat(FORMATTED_STRING, updatedFields);
  }

}
