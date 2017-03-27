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

package org.symphonyoss.integration.webhook.salesforce.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.webhook.salesforce.SalesforceParseException;

import java.util.List;
import java.util.Map;

/**
 * Interface that defines methods to validate Salesforce messages
 * Created by cmarcondes on 11/2/16.
 */
public interface SalesforceParser {

  String parse(Entity entity) throws SalesforceParseException;

  void setSalesforceUser(String user);

  List<String> getEvents();

  String parse(Map<String, String> parameters, JsonNode node) throws SalesforceParseException;
}
