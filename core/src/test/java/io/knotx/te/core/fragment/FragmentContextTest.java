package io.knotx.te.core.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.knotx.dataobjects.Fragment;
import io.knotx.junit.converter.FragmentArgumentConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

class FragmentContextTest {

  @ParameterizedTest
  @CsvSource(value = {
      "snippets/with_defined_te_strategy.txt;customStrategy",
      "snippets/with_no_strategy.txt;defaultStrategy"
  }, delimiter = ';')
  void from_whenFragment_expectStrategyApplied(
      @ConvertWith(FragmentArgumentConverter.class) Fragment fragment, String expectedStrategy) {
    final FragmentContext fragmentContext = FragmentContext.from(fragment, "defaultStrategy");
    assertThat(fragmentContext.getStrategy(), is(expectedStrategy));
  }
}
