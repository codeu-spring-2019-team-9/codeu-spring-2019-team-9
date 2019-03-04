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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** import for Fetch Options */
import com.google.appengine.api.datastore.FetchOptions;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
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
   * Gets messages received by a specific user.
   *
   * @return a list of messages received by the user, or empty list if user has never received a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String recipient) {
    List<Message> messages = new ArrayList<>();

    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient))
            .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String user = (String) entity.getProperty("user");
        Message message = new Message(id, user, text, timestamp, recipient);

        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  /**
   * Gets messages sent between the logged-in user and another user.
   *
   * @return a list of messages sent between the logged-in user and another user, or empty list
   * if logged-in user or other user have never sent a message to each other. List is sorted by time ascending.
   */
  public List<Message> getMessagesBetweenTwoUsers(String loggedInUser, String otherUser) {
    List<Message> messages = new ArrayList<>();

    // Messages between two people, where logged-in user is the sender
    Filter messagesSentByLoggedInUser = new Query.FilterPredicate("user", FilterOperator.EQUAL, loggedInUser);
    Filter messagesReceivedByOtherUser = new Query.FilterPredicate("recipient", FilterOperator.EQUAL, otherUser);

    // All the messages sent by logged-in user to the other user
    Filter loggedInUserMessages = CompositeFilterOperator.and(messagesSentByLoggedInUser, messagesReceivedByOtherUser);

    // Messages between two people, where logged-in user is the recipient
    Filter messagesSentByOtherUser = new Query.FilterPredicate("user", FilterOperator.EQUAL, otherUser);
    Filter messagesReceivedByLoggedInUser = new Query.FilterPredicate("recipient", FilterOperator.EQUAL, loggedInUser);
    
    // All the messages recieved by logged-in user sent by the other user
    Filter otherUserMessages = CompositeFilterOperator.and(messagesSentByOtherUser, messagesReceivedByLoggedInUser);

    // All the messages between the two users
    Filter directMessages = CompositeFilterOperator.or(loggedInUserMessages, otherUserMessages);

    Query query =
        new Query("Message")
            .setFilter(directMessages)
            .addSort("timestamp", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String sender = (String) entity.getProperty("user");
        String receiver = (String) entity.getProperty("receiver");
        Message message = new Message(id, sender, text, timestamp, receiver);

        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults());
  }

}
