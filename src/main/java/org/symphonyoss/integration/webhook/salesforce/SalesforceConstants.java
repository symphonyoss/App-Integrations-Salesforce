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

  public static final String OPPORTUNITY = "opportunity";
  public static final String OWNER = "owner";
  public static final String EMAIL_ADDRESS = "emailAddress";
  public static final String INTEGRATION_NAME = "sfdc";
  public static final String ACCOUNT = "account";
  public static final String OPPORTUNITIES = "opportunities";
  public static final String ACTIVITIES = "activities";
  public static final String ASSIGNEE = "assignee";
  public static final String OPPORTUNITY_PATH = "current";
  public static final String FIELDS_PATH = "opportunity";
  public static final String WEBHOOK_EVENT = "webhookEvent";
}
