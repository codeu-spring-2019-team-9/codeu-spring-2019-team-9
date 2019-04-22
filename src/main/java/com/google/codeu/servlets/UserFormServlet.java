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

import java.io.IOException;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;


public class UserFormServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    datastore = createDatastore();
  }

  /*
   * Instantiates a connection to a datastore. In tests, override this with a
   * method that returns a fake/mock datastore object.
   */
  protected Datastore createDatastore() {
    return new Datastore();
  }

 
  /*
   * This will always create a new Map with the values of the tea's
   * Currently, there is no error handling as of right now
   */

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

        Map<String, Long> userTeaData = new HashMap<String, Long>();
        
        //GAP will only take Date as a property so we are going to just convert ZonedDateTime, it has timezone in it 
        //EX: "Mon Apr 22 04:45:54 UTC 2019"
        Date date = Date.from(java.time.ZonedDateTime.now().toInstant());

        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        String username = userService.getCurrentUser().getEmail();
        List<String> teaNames = Arrays.asList("greenTea", "whiteTea", "blackTea", "herbalTea");

        for (String tea : teaNames) {

          String formTea = request.getParameter(tea);
          Long amountOfTea = Long.MIN_VALUE;

          try {
            amountOfTea = Long.parseLong(formTea);
          } catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
          userTeaData.put(tea, amountOfTea);
          }
        datastore.storeUserTeaData(userTeaData, username, date);
  }
}
