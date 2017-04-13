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

import org.symphonyoss.integration.webhook.exception.WebHookParseException;

/**
 * Created by cmarcondes on 11/2/16.
 */
public class SalesforceParseException extends WebHookParseException {

  private static final String COMPONENT = "Salesforce Webhook Dispatcher";

  public SalesforceParseException(String message) {
    super(COMPONENT, message);
  }

  public SalesforceParseException(String message, Exception cause){
    super(COMPONENT, message, cause);
  }
}
