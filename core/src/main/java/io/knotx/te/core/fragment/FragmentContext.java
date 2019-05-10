/*
 * Copyright (C) 2019 Knot.x Project
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

import io.knotx.fragment.Fragment;
import io.vertx.core.json.JsonObject;
import java.util.Optional;

public class FragmentContext {

  private static final String TE_STRATEGY = "te-strategy";

  private Fragment fragment;
  private String strategy;

  private FragmentContext(Fragment fragment, String strategy) {
    this.fragment = fragment;
    this.strategy = strategy;
  }

  /**
   * Factory method that creates context from the {@link Fragment}. Template Engine strategy is
   * extracted to separate field.
   *
   * @param fragment - fragment from which the context will be created.
   * @param defaultStrategy - default strategy that will be used when no strategy is defined in the
   * fragment
   * @return a FragmentContext that wraps given fragment.
   */
  public static FragmentContext from(Fragment fragment, String defaultStrategy) {
    JsonObject attributes = fragment.getConfiguration();
    String strategy = Optional.ofNullable(attributes.getString(TE_STRATEGY))
        .orElse(defaultStrategy);
    return new FragmentContext(fragment, strategy);
  }

  /**
   * @return fragment
   */
  public Fragment fragment() {
    return fragment;
  }

  /**
   * @return strategy for template engine processing of the fragment
   */
  public String strategy() {
    return strategy;
  }

}
