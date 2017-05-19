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

import org.apache.commons.lang3.StringUtils;

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
  public static final String UPDATED_FIELDS = "UpdatedFields";
  public static final String CREATED_OR_UPDATED = "CreatedOrUpdated";
  public static final String UPDATED_NOTIFICATION = "Updated";
  public static final String CREATED_NOTIFICATION = "Created";
  public static final String HAS_OWNER_AT_SYMPHONY = "hasOwnerAtSymphony";
  public static final String URL_ICON_INTEGRATION = "URLIconIntegration";
  public static final String ICON_CROWN = "IconCrown";
  public static final String NAME_AND_EMAIL = "NameAndEmail";
  public static final String AMOUNT_AND_CURRENCY_ISO_CODE = "AmountAndCurrencyIsoCode";

  private static Map<String, String> createMap() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("Id", "id");
    result.put("Type", "type");
    result.put("Name", "name");
    result.put("Link", "link");
    result.put("Owner", "owner");
    result.put("Account", "account");
    result.put("NextStep", "next step");
    result.put("StageName", "stage");
    result.put("CloseDate", "close date");
    result.put("Probability", "probability");
    result.put("CurrencyIsoCode", "currency");
    result.put("TotalOpportunityQuantity", "amount");

    return Collections.unmodifiableMap(result);
  }

  /**
   * Returns a readable field name for Opportunity fields.
   * @param key The opportunity field key for which the name should be retrieved.
   * @return readable field name, or null when the field does not have a corresponding name
   */
  public static String getOpportunityFieldName(String key) {
    if (StringUtils.isNotBlank(key) && FIELDS_NAME.containsKey(key)) {
      return FIELDS_NAME.get(key);
    }

    return StringUtils.EMPTY;
  }

}
