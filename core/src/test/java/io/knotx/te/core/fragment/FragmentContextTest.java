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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FragmentContextTest {

  @Test
  void from_whenFragment_expectStrategyApplied() {
    // given
    String expectedStrategy = "customStrategy";
    Fragment fragment = new Fragment("type", new JsonObject().put("te-strategy", expectedStrategy),
        "body");

    final FragmentContext fragmentContext = FragmentContext.from(fragment, "defaultStrategy");
    Assertions.assertEquals(expectedStrategy, fragmentContext.strategy());
  }

  @Test
  void from_whenFragmentWithNoStrategy_expectDefaultStrategyApplied() {
    // given
    Fragment fragment = new Fragment("type", new JsonObject(), "body");

    final FragmentContext fragmentContext = FragmentContext.from(fragment, "defaultStrategy");
    Assertions.assertEquals("defaultStrategy", fragmentContext.strategy());
  }

}
