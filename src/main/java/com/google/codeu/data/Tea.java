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

// import java.util.UUID;

/** A single message posted by a user. */
public class Tea {
    
    private String userName;
    private int greenTea;
    private int whiteTea;
    private int yellowTea;
    private int oolongTea;
    private int blackTea;
    private int matchaTea;
    
    
    
    public Tea(String userName, int greenTea, int whiteTea, int yellowTea, int oolongTea, int blackTea, int matchaTea) {
        this.userName = userName;
        this.greenTea = greenTea;
      this.whiteTea = whiteTea;
      this.yellowTea = yellowTea;
      this.oolongTea = oolongTea;
      this.blackTea = blackTea;
      this.matchaTea = matchaTea;
  }
  public String getUserName() {
      return this.userName;
  }
  public int getGreenTea() {
      return this.greenTea;
  }
  public int getWhiteTea() {
      return this.whiteTea;
  }
  public int getYellowTea() {
      return this.yellowTea;
  }
  public int getOolongTea() {
      return this.oolongTea;
  }
  public int getBlackTea() {
      return this.blackTea;
  }
  public int getMatchaTea() {
      return this.matchaTea;
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
