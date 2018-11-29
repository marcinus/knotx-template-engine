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

import io.knotx.dataobjects.Fragment;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FragmentContext {

  private static final String TE_STRATEGY =
      ".*te-strategy.*";

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
    Document document = Jsoup.parseBodyFragment(fragment.content());
    Element scriptTag = document.body().child(0);

    List<Attribute> attributes = scriptTag.attributes().asList();
    final String strategy = attributes.stream()
        .filter(attribute -> attribute.getKey().matches(TE_STRATEGY))
        .findFirst()
        .map(Attribute::getValue)
        .orElse(defaultStrategy);

    return new FragmentContext(fragment, strategy);
  }

  /**
   * @return a fragment wrapped in this context.
   */
  public Fragment fragment() {
    return fragment;
  }

  /**
   * @return strategy for template engine processing of the fragment
   */
  public String getStrategy() {
    return strategy;
  }

}
