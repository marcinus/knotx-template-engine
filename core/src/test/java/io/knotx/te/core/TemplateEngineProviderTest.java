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
package io.knotx.te.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.knotx.te.api.TemplateEngine;
import io.knotx.te.core.DummyTemplateEngineFactory.DummyTemplateEngine;
import io.knotx.te.core.exception.UnsupportedEngineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TemplateEngineProviderTest {

  @Test
  @DisplayName("Expect UnsupportedEngineException thrown when trying to instantiate TE with not existing TE factory")
  void expectUnsupportedEngineException() {
    TemplateEngineProvider provider = new TemplateEngineProvider(null);
    TemplateEngineEntry config = Mockito.mock(TemplateEngineEntry.class);
    when(config.getFactory()).thenReturn("notexisting");
    assertThrows(UnsupportedEngineException.class, () -> provider.loadTemplateEngine(config));
  }

  @Test
  @DisplayName("Expect Template Engine properly instantiated when its factory is present")
  void expectEngineInstantiated() {
    TemplateEngineProvider provider = new TemplateEngineProvider(null);
    TemplateEngineEntry config = Mockito.mock(TemplateEngineEntry.class);
    when(config.getFactory()).thenReturn("dummy");
    TemplateEngine templateEngine = provider.loadTemplateEngine(config);
    assertTrue(templateEngine instanceof DummyTemplateEngine);
  }

}
