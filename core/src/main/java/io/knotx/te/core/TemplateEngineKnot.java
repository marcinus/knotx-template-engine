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
package io.knotx.te.core;

import io.knotx.engine.api.KnotProxy;
import io.knotx.te.api.TemplateEngine;
import io.knotx.te.api.TemplateEngineFactory;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class TemplateEngineKnot extends AbstractVerticle {

  public final static String EB_ADDRESS = "knotx.knot.te";

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineKnot.class);

  private TemplateEngineKnotOptions options;

  private MessageConsumer<JsonObject> consumer;

  private ServiceBinder serviceBinder;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.options = new TemplateEngineKnotOptions(config());
  }

  @Override
  public void start() {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());

    //register the service proxy on event bus
    serviceBinder = new ServiceBinder(getVertx());
    consumer = serviceBinder
        .setAddress(EB_ADDRESS)
        .register(KnotProxy.class, new TemplateEngineKnotProxy(options, loadTemplateEngines()));
  }

  @Override
  public void stop() {
    serviceBinder.unregister(consumer);
  }

  private Map<String, TemplateEngine> loadTemplateEngines() {
    return loadTemplateEngineFactories()
        .stream()
        .map(factory -> options.getEngines().stream()
            .filter(teEntry -> factory.getName().equals(teEntry.getName()))
            .findFirst()
            .map(teEntry -> factory.create(vertx, teEntry.getConfig()))
            .map(teHandler -> Pair.of(factory.getName(), teHandler))
            .<IllegalArgumentException>orElseThrow(IllegalArgumentException::new))
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
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
