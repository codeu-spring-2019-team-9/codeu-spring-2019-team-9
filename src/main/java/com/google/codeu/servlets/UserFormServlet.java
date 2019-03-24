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

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;



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
    return datastore;
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
  
    // Create a map of the httpParameters that we want and run it through jSoup
    Map<String, String> blogContent =
        req.getParameterMap()
            .entrySet()
            .stream()
            .filter(a -> a.getKey().startsWith("blogContent_"))
            .collect(
                Collectors.toMap(
                    p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));
  
    Entity post = new Entity("Blogpost"); // create a new entity
  
    post.setProperty("title", blogContent.get("blogContent_title"));
    post.setProperty("author", blogContent.get("blogContent_author"));
    post.setProperty("body", blogContent.get("blogContent_description"));
    post.setProperty("timestamp", new Date().getTime());
  
    try {
      datastore.put(post); // store the entity
  
      // Send the user to the confirmation page with personalised confirmation text
      String confirmation = "Post with title " + blogContent.get("blogContent_title") + " created.";
  
      req.setAttribute("confirmation", confirmation);
      req.getRequestDispatcher("/confirm.jsp").forward(req, resp);
    } catch (DatastoreFailureException e) {
      throw new ServletException("Datastore error", e);
    }
    
  }
  


}
