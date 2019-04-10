/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import com.google.common.base.Preconditions;
import com.google.common.flogger.FluentLogger;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.api.client.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

/** import for Fetch Options */
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  private Optional<String> getStringProperty(PropertyContainer container, String propertyName) {
    if (!container.hasProperty(propertyName)) {
      return Optional.empty();
    }

    String value = null;
    try {
      value = (String) container.getProperty(propertyName);
    } catch (ClassCastException wrongType) {
      logger.atSevere().withCause(wrongType).log(
          "Property \"" + propertyName + "\" exists but is not a String.");
    }
    return Optional.ofNullable(value);
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("recipient", message.getRecipient());

    datastore.put(messageEntity);
  }

  /**
   * Gets messages sent between the logged-in user and another user.
   *
   * @return a list of messages sent between the logged-in user and another user, or empty list if
   *     logged-in user or other user have never sent a message to each other. List is sorted by
   *     time ascending.
   */
  public List<Message> getMessagesBetweenTwoUsers(String loggedInUser, String otherUser) {
    // Messages are ordered from oldest to newest since messages between
    // people may include implicit context/reference to previously sent messages,
    // and this order makes the chronology of the conversation and context
    // more intuitive and understandable in the messages list page.

    Preconditions.checkArgument(
        !Strings.isNullOrEmpty(loggedInUser), "loggedInUser is null or empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(otherUser), "otherUser is null or empty");

    Query query = createMessageQueryWithUserFilter(loggedInUser, otherUser);
    PreparedQuery results = datastore.prepare(query);
    return prepareQueryToMesssages(results);
  }

  private Query createMessageQueryWithUserFilter(String loggedInUser, String otherUser) {
    return new Query("Message")
        .setFilter(createUserFilter(loggedInUser, otherUser))
        .addSort("timestamp", SortDirection.ASCENDING);
  }

  private Filter createUserFilter(String loggedInUser, String otherUser) {
    // Messages between two people, where logged-in user is the sender
    Filter messagesSentByLoggedInUser =
        new Query.FilterPredicate("user", FilterOperator.EQUAL, loggedInUser);
    Filter messagesReceivedByOtherUser =
        new Query.FilterPredicate("recipient", FilterOperator.EQUAL, otherUser);

    // All the messages sent by logged-in user to the other user
    Filter loggedInUserMessages =
        CompositeFilterOperator.and(messagesSentByLoggedInUser, messagesReceivedByOtherUser);

    // Messages between two people, where logged-in user is the recipient
    Filter messagesSentByOtherUser =
        new Query.FilterPredicate("user", FilterOperator.EQUAL, otherUser);
    Filter messagesReceivedByLoggedInUser =
        new Query.FilterPredicate("recipient", FilterOperator.EQUAL, loggedInUser);

    // All the messages recieved by logged-in user sent by the other user
    Filter otherUserMessages =
        CompositeFilterOperator.and(messagesSentByOtherUser, messagesReceivedByLoggedInUser);

    // All the messages between the two users
    return CompositeFilterOperator.or(loggedInUserMessages, otherUserMessages);
  }

  public List<Message> getAllMessages() {
    Query query = createPublicMessageQuery();
    PreparedQuery results = datastore.prepare(query);
    return prepareQueryToMesssages(results);
  }

  private List<Message> prepareQueryToMesssages(PreparedQuery query) {
    List<Message> results = new ArrayList<>();
    for (Entity entity : query.asIterable()) {
      results.add(entityToMessage(entity));
    }
    return results;
  }

  private Message entityToMessage(Entity entity) {
    String idString = entity.getKey().getName();
    UUID id = UUID.fromString(idString);
    String text = getStringProperty(entity, "text").orElse("");
    long timestamp = (long) entity.getProperty("timestamp");
    String sender = (String) entity.getProperty("user");
    String receiver = (String) entity.getProperty("recipient");
    return new Message(id, sender, text, timestamp, receiver);
  }

  private Query createPublicMessageQuery() {
    return new Query("Message").addSort("timestamp", SortDirection.ASCENDING);
  }

  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults());
  }

  /**
   * This is to create an user form data to put in the datastore Going with option 2 highlighted in
   * the Meeting Notes
   *
   * @throws EntityNotFoundException
   */
  public void storeUserTeaData(
     Map<String, Long> incomingUserTeaData, String username, String date) {

    Entity datastoreUser;
    String keyName = date + ":" + username;
    Key usernameKey = KeyFactory.createKey("UserTeaData", keyName);

    try {
      datastoreUser = datastore.get(usernameKey);
      datastoreUser.setProperty("username", datastoreUser.getProperty("username"));
      datastoreUser.setProperty("date", datastoreUser.getProperty("date"));

      EmbeddedEntity teaMap = (EmbeddedEntity) datastoreUser.getProperty("teaData");

      for (String key : teaMap.getProperties().keySet()) {
        Long value1 = incomingUserTeaData.get(key);
        Long value2 = value1 + (Long) teaMap.getProperty(key);
        teaMap.setProperty(key, value2);
      }

      datastoreUser.setProperty("teaData", teaMap);
      datastore.put(datastoreUser);

    } catch (EntityNotFoundException e) {

      Entity userTeaConsumption = new Entity(usernameKey);
      userTeaConsumption.setProperty("username", username);
      userTeaConsumption.setProperty("date", date);

      EmbeddedEntity teaMap = new EmbeddedEntity();
      for (String key : incomingUserTeaData.keySet()) {
        teaMap.setProperty(key, incomingUserTeaData.get(key));
      }
      userTeaConsumption.setProperty("teaData", teaMap);
      datastore.put(userTeaConsumption);
    }
  }
}
