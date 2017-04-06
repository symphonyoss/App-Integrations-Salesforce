/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.salesforce;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Salesforce constants
 *
 * Created by cmarcondes on 11/3/16.
 */
public class SalesforceConstants {

  /**
   * Constructor to avoid instantiation
   */
  private SalesforceConstants(){}

  private static final Map<String, String> FIELDS_NAME = createMap();

  public static final String OPPORTUNITY = "opportunity";
  public static final String OWNER = "owner";
  public static final String EMAIL_ADDRESS = "emailAddress";
  public static final String INTEGRATION_NAME = "sfdc";
  public static final String ACCOUNT = "account";
  public static final String OPPORTUNITIES = "opportunities";
  public static final String ACTIVITIES = "activities";
  public static final String ASSIGNEE = "assignee";

  public static final String PREVIOUS_DATA_PATH = "previous";
  public static final String CURRENT_DATA_PATH = "current";
  public static final String OPPORTUNITY_OWNER = "Owner";
  public static final String LAST_MODIFY_BY = "LastModifiedBy";
  public static final String NAME = "Name";
  public static final String EMAIL = "Email";
  public static final String STAGE_NAME = "StageName";
  public static final String CLOSE_DATE = "CloseDate";
  public static final String OPPORTUNITY_ACCOUNT = "Account";
  public static final String LINK = "Link";
  public static final String AMOUNT = "Amount";
  public static final String NEXT_STEP = "NextStep";
  public static final String PROBABILITY = "Probability";
  public static final String CURRENCY_ISO_CODE = "CurrencyIsoCode";
  public static final String TYPE = "Type";
  public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd";
  public static final String UPDATED = "updated";
  public static final String CREATED = "created";

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

  /**
   * Returns the value of map FieldsName
   * @param String key
   * @return Value if exists, but not return key
   * @throws SalesforceParseException in case this key not informed
   */
  public static String getValueOfMapFieldsName(String key) {
    if (StringUtils.isEmpty(key)) {
      throw new SalesforceParseException("Key null");
    }

    if (FIELDS_NAME.containsKey(key)) {
      return FIELDS_NAME.get(key);
    }

    return key;
  }

}
