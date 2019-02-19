/*
 * Copyright (C) 2018 Knot.x Project
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
package io.knotx.te.core.fragment;

import io.knotx.engine.api.FragmentEvent;
import io.vertx.core.json.JsonObject;
import java.util.Optional;

public class FragmentContext {

  private static final String TE_STRATEGY = "te-strategy";

  private FragmentEvent event;
  private String strategy;

  private FragmentContext(FragmentEvent event, String strategy) {
    this.event = event;
    this.strategy = strategy;
  }

  /**
   * Factory method that creates context from the {@link FragmentEvent}. Template Engine strategy is
   * extracted to separate field.
   *
   * @param event - fragment event from which the context will be created.
   * @param defaultStrategy - default strategy that will be used when no strategy is defined in the
   * fragment
   * @return a FragmentContext that wraps given fragment.
   */
  public static FragmentContext from(FragmentEvent event, String defaultStrategy) {
    JsonObject attributes = event.getFragment().getConfiguration();
    String strategy = Optional.ofNullable(attributes.getString(TE_STRATEGY))
        .orElse(defaultStrategy);
    return new FragmentContext(event, strategy);
  }

  /**
   * @return fragment event
   */
  public FragmentEvent event() {
    return event;
  }

  /**
   * @return strategy for template engine processing of the fragment
   */
  public String strategy() {
    return strategy;
  }

}
