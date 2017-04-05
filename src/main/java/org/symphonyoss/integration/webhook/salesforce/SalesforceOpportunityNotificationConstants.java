package org.symphonyoss.integration.webhook.salesforce;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crepache on 05/04/17.
 */
public class SalesforceOpportunityNotificationConstants {

  private static final Map<String, String> FIELDS_NAME = createMap();

  private static Map<String, String> createMap() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("StageName", "Stage");
    result.put("LastModifiedBy", "Last Modified By");
    result.put("TotalOpportunityQuantity", "Total Opportunity Quantity");
    result.put("NextStep", "Next Step");
    result.put("CurrencyIsoCode", "Currency Iso Code");
    result.put("CloseDate", "Close Date");

    return Collections.unmodifiableMap(result);
  }

  public static String getValue(String key) {
    if (StringUtils.isEmpty(key)) {
      throw new SalesforceParseException("Key null");
    }

    if (FIELDS_NAME.containsKey(key)) {
      return FIELDS_NAME.get(key);
    }

    return key;
  }

}
