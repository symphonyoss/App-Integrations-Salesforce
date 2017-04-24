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

import static org.symphonyoss.integration.parser.ParserUtils.presentationFormat;
import static org.symphonyoss.integration.parser.SafeString.EMPTY_SAFE_STRING;
import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.EMAIL_ADDRESS;
import static org.symphonyoss.integration.webhook.salesforce.SalesforceConstants.INTEGRATION_NAME;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.messageml.MessageMLFormatConstants;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.NumberFormatUtils;
import org.symphonyoss.integration.webhook.salesforce.SalesforceConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Utility methods for Salesforce Parsers
 * Created by cmarcondes on 11/3/16.
 */
@Component
public abstract class BaseSalesforceParser implements SalesforceParser {

  @Autowired
  private UserService userService;

  private String salesforceUser;

  @Override
  public void setSalesforceUser(String user) {
    this.salesforceUser = user;
  }

  /**
   * Searches a matching Symphony user for the given Salesforce user email.
   * @param email email to be found
   * @return user information, including a matching Symphony ID (if any)
   */
  private User getUser(String email) {
    return userService.getUserByEmail(salesforceUser, email);
  }

  protected void createMentionTagFor(Entity mainEntity, String userEntityName) {
    Entity userEntity = mainEntity.getEntityByName(userEntityName);
    if(userEntity == null){
      return;
    }
    String email = getEmail(userEntity);
    if (!StringUtils.isEmpty(email)) {
      User user = getUser(email);
      EntityBuilder.forEntity(userEntity).nestedEntity(user.getMentionEntity(INTEGRATION_NAME));
    }
  }

  /**
   * Creates the mention tag for the entity received.
   * @param mainEntity Main entity to find the nested entity
   * @param nestedEntityName Entity to find in the main entity
   * @param userEntityName Entity name for set the mention tag
   */
  protected void createListOfMentionsFor(Entity mainEntity, String nestedEntityName, String userEntityName){
    Entity entity = mainEntity.getEntityByType(nestedEntityName);
    if (entity != null) {
      for (Entity nestedEntity : entity.getEntities()) {
        createMentionTagFor(nestedEntity, userEntityName);
      }
    }
  }

  private String getEmail(Entity entity) {
    return entity.getAttributeValue(EMAIL_ADDRESS);
  }

}