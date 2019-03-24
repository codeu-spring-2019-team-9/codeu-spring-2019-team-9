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

package com.google.codeu.servlets;

import java.util.Optional;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving {@link Message} instances. */
public class MessageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for logged-in user and another user. Responds with
   * an empty array if the user is not logged in or another user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    // Get logged-in user and other user
    Optional<String> loggedInUser = Optional.empty();
    Optional<String> otherUser = Optional.ofNullable(request.getParameter("user"));
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      loggedInUser = Optional.ofNullable(userService.getCurrentUser().getEmail());
    }

    // Request is invalid, return empty array
    // TODO: Make errors JSON objects
    if (loggedInUser.orElse("").isEmpty()) {
      response.setStatus(401);
      response.getWriter().println("Error: Unauthorized access");
      return;
    }
    if (otherUser.orElse("").isEmpty()) {
      response.setStatus(400);
      response.getWriter().println("Error: Missing required query parameter 'user'");
      return;
    }

    List<Message> messages = datastore.getMessagesBetweenTwoUsers(loggedInUser.get(), otherUser.get());
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.none());
    String recipient = request.getParameter("recipient");
    System.out.println("before getting sentiment score");
    System.out.println("text: " + text);
    float sentimentScore = calculateSentimentScore(text);
    //System.out.println("after getting sentiment score: " + sentimentScore);

    Message message = new Message(user, text, recipient, sentimentScore);
    datastore.storeMessage(message);

    response.sendRedirect("/user-page.html?user=" + recipient);
  }

  // Returns the sentiment score of the input text
  private float calculateSentimentScore(String text) throws IOException {
    //System.out.println("before doc; text: " + text);
    Document doc = Document.newBuilder()
        .setContent(text).setType(Type.PLAIN_TEXT).build();
    //System.out.println("after doc and before client; doc: " + doc);
    LanguageServiceClient languageService = LanguageServiceClient.create();
    //System.out.println("after client, before sentiment");
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    //System.out.println("after sentiment, before closing");
    languageService.close();
    //System.out.println("after closing, before return");
    return sentiment.getScore();
  }
}
