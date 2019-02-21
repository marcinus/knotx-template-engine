package io.knotx.te.core.fragment;

import io.knotx.engine.api.FragmentEvent;
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
    FragmentEvent event = new FragmentEvent(fragment, null);

    final FragmentContext fragmentContext = FragmentContext.from(event, "defaultStrategy");
    Assertions.assertEquals(expectedStrategy, fragmentContext.strategy());
  }

  @Test
  void from_whenFragmentWithNoStrategy_expectDefaultStrategyApplied() {
    // given
    Fragment fragment = new Fragment("type", new JsonObject(), "body");
    FragmentEvent event = new FragmentEvent(fragment, null);

    final FragmentContext fragmentContext = FragmentContext.from(event, "defaultStrategy");
    Assertions.assertEquals("defaultStrategy", fragmentContext.strategy());
  }

}
