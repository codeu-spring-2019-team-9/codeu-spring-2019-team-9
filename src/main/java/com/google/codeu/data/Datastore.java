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
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
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

    // test if sender/reciever messages are fetched
    for (int i = 0; i < messages.size(); i++) {
      System.out.println(messages.get(i));
    }

    return messages;
  }

  /**
   * Gets messages sent between user and a recipient.
   *
   * @return a list of messages sent between the user and a recipient, or empty list if user or recipient has never sent a
   *     message to each other. List is sorted by time descending.
   */

  // TODO: Finish this feature
  public List<Message> getMessagesBetweenTwoUsers(String user, String recipient) {
    List<Message> messages = new ArrayList<>();

    // Messages between two people, where user is the sender
    Filter userSentMessages = new Query.FilterPredicate("user", FilterOperator.EQUAL, user);
    Filter recipientReceivedMessages = new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient);

    // Messages between two people, where user is the receiver
    Filter userReceivedMessages = new Query.FilterPredicate("recipient", FilterOperator.EQUAL, user);
    Filter recipientSentMessages = new Query.FilterPredicate("user", FilterOperator.EQUAL, recipient);

    // All the messages sent by user to the other person
    Filter userMessages = CompositeFilterOperator.and(userSentMessages, recipientReceivedMessages);

    // All the messaged recieved by user sent by the other person
    Filter recipientMessages = CompositeFilterOperator.and(recipientSentMessages, userReceivedMessages);

    // All the messages between the two users
    Filter directMessages = CompositeFilterOperator.or(userMessages, recipientMessages);

    Query query =
        new Query("Message")
            .setFilter(directMessages)
            .addSort("timestamp", SortDirection.DESCENDING);
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

    // test if sender/reciever messages are fetched
    for (int i = 0; i < messages.size(); i++) {
      System.out.println(messages.get(i));
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
