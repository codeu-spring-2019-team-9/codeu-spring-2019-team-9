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


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Tea;




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

@Override
public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }
    String user = userService.getCurrentUser().getEmail();
    int greenTea = Integer.parseInt(request.getParameter("greenTea"));
    int whiteTea = Integer.parseInt(request.getParameter("whiteTea"));
    int yellowTea = Integer.parseInt(request.getParameter("yellowTea"));
    int oolongTea = Integer.parseInt(request.getParameter("oolongTea"));
    int blackTea = Integer.parseInt(request.getParameter("blackTea"));
    int matchaTea = Integer.parseInt(request.getParameter("matchaTea"));

    Tea userTeaData = new Tea(user,greenTea,whiteTea,yellowTea,oolongTea,blackTea,matchaTea);

    datastore.storeUserFormData(userTeaData);
  }
}
  



  // Entity post = new Entity("userFormData"); // create a new entity

  // post.setProperty("greenTea", blogContent.get("greenTea"));
  // post.setProperty("whiteTea", blogContent.get("whiteTea"));
  // post.setProperty("yellowTea", blogContent.get("yellowTea"));
  // post.setProperty("oolongTea", blogContent.get("oolongTea"));
  // post.setProperty("blackTea", blogContent.get("blackTea"));
  // post.setProperty("matchaTea", blogContent.get("matchaTea"));

  // try {
  //   datastore.put(post); // store the entity

  //   // Send the user to the confirmation page with personalised confirmation text
  //   String confirmation = "Post with title " + blogContent.get("blogContent_title") + " created.";

  //   req.setAttribute("confirmation", confirmation);
  //   req.getRequestDispatcher("/confirm.jsp").forward(req, resp);
  // } catch (DatastoreFailureException e) {
  //   throw new ServletException("Datastore error", e);
  // }


  
  // @Override
  // public void doPost(HttpServletRequest req, HttpServletResponse resp)
  //     throws ServletException, IOException {

  //   PrintWriter out = resp.getWriter();

  //   out.println(
  //       "User drinking tea: " 
  //       + req.getParameter("greenTea") + "green tea" + '\n'
  //       + req.getParameter("whiteTea") + "white tea" + '\n'
  //       + req.getParameter("yellowTea") + "yellow Tea" + '\n'
  //       + req.getParameter("oolongTea") + "oolong Tea" + '\n'
  //       + req.getParameter("blackTea") + "black Tea" + '\n'
  //       + req.getParameter("matchaTea")+ "matcha Tea");
  // }


