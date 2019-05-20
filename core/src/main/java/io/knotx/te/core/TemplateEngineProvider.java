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

import io.knotx.te.api.TemplateEngine;
import io.knotx.te.api.TemplateEngineFactory;
import io.knotx.te.core.exception.UnsupportedEngineException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

class TemplateEngineProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineProvider.class);
  private final Vertx vertx;

  TemplateEngineProvider(Vertx vertx) {
    this.vertx = vertx;
  }

  TemplateEngine loadTemplateEngine(TemplateEngineEntry engineConfig) {
    return loadTemplateEngineFactories()
        .stream()
        .filter(factory -> factory.getName().equals(engineConfig.getFactory()))
        .findFirst()
        .map(factory -> factory.create(vertx, engineConfig.getConfig()))
        .orElseThrow(() -> new UnsupportedEngineException(engineConfig.getFactory()));
  }

  private List<TemplateEngineFactory> loadTemplateEngineFactories() {
    List<TemplateEngineFactory> templateEngineFactories = new ArrayList<>();
    ServiceLoader.load(TemplateEngineFactory.class)
        .iterator()
        .forEachRemaining(templateEngineFactories::add);

    LOGGER.info("Template Engines [{}] registered.",
        templateEngineFactories.stream().map(TemplateEngineFactory::getName).collect(Collectors
            .joining(",")));

    return templateEngineFactories;
  }


}
