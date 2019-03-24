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

import java.util.UUID;

/** A single message posted by a user. */
public class Tea {
    
    private String user;
    private int greenTea;
    private int whiteTea;
    private int yellowTea;
    private int oolongTea;
    private int blackTea;
    private int matchaTea;

    /**
     * This is using the username as the identifier, but should consider using UUID as the identifier
     */
  //use the String user for now, but consider replacing it with UUID
  public Tea(String user, int greenTea, int whiteTea, int yellowTea, int oolongTea, int blackTea, int matchaTea) {
      this.user = user;
      this.greenTea = greenTea;
      this.whiteTea = whiteTea;
      this.yellowTea = yellowTea;
      this.oolongTea = oolongTea;
      this.blackTea = blackTea;
      this.matchaTea = matchaTea;
  }

  public String getUser() {
    return user;
  }
  public int getGreenTea() {
      return greenTea;
  }
  public int getWhiteTea() {
      return whiteTea;
  }
  public int getYellowTea() {
      return yellowTea;
  }
  public int getOolongTea() {
      return oolongTea;
  }
  public int getBlackTea() {
      return blackTea;
  }
  public int getMatchaTea() {
      return matchaTea;
  }
  public void setGreenTea(int greenTea) {
      this.greenTea = greenTea;
  }
  public void setWhiteTea(int whiteTea) {
      this.whiteTea = whiteTea;
  }
  public void setYellowTea(int yellowTea) {
    this.yellowTea = yellowTea;
}
public void setOolongTea(int oolongTea) {
    this.oolongTea = oolongTea;
}
public void setBlackTea(int blackTea) {
    this.blackTea = blackTea;
}
public void setMatchaTea(int matchaTea) {
    this.matchaTea = matchaTea;
}


}
