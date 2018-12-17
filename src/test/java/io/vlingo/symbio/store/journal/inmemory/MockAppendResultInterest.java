// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal.inmemory;

import java.util.List;

import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest;

public class MockAppendResultInterest implements AppendResultInterest<String> {

  @Override
  public void appendResultedIn(Result result, String streamName, int streamVersion, Event<String> event, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, Exception cause, String streamName, int streamVersion, Event<String> event, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, String streamName, int streamVersion, Event<String> event, State<String> snapshot, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, Exception cause, String streamName, int streamVersion, Event<String> events, State<String> snapshot, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, String streamName, int streamVersion, List<Event<String>> events, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, Exception cause, String streamName, int streamVersion, List<Event<String>> event, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, String streamName, int streamVersion, List<Event<String>> events, State<String> snapshot, Object object) {
    
  }

  @Override
  public void appendResultedIn(Result result, Exception cause, String streamName, int streamVersion, List<Event<String>> events, State<String> snapshot, Object object) {
    
  }
}
