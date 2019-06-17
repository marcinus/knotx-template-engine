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

import io.knotx.fragments.api.Fragment;
import io.knotx.fragments.handler.api.Knot;
import io.knotx.fragments.handler.api.domain.FragmentContext;
import io.knotx.fragments.handler.api.domain.FragmentResult;
import io.knotx.te.api.TemplateEngine;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class TemplateEngineKnot extends AbstractVerticle implements Knot {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineKnot.class);

  private MessageConsumer<JsonObject> consumer;
  private ServiceBinder serviceBinder;

  private TemplateEngineKnotOptions options;
  private TemplateEngine templateEngine;

  @Override
  public void apply(io.knotx.fragments.handler.api.domain.FragmentContext fragmentContext,
      Handler<AsyncResult<FragmentResult>> result) {
    Single.just(fragmentContext)
        .map(FragmentContext::getFragment)
        .doOnSuccess(this::traceFragment)
        .map(this::processFragment)
        .map(this::handleSuccessProcessing)
        .subscribe(
            fragmentResult -> {
              LOGGER.debug("Processing ends with result [{}]", fragmentResult);
              Future.succeededFuture(fragmentResult).setHandler(result);
            },
            error -> {
              LOGGER.error("Processing ends with exception!", error);
              Future<FragmentResult> future = Future.failedFuture(error);
              future.setHandler(result);
            }
        );
  }

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
    templateEngine = new TemplateEngineProvider(vertx).loadTemplateEngine(options.getEngine());

    consumer = serviceBinder.setAddress(options.getAddress()).register(Knot.class, this);
  }

  @Override
  public void stop() {
    serviceBinder.unregister(consumer);
  }

  private FragmentResult handleSuccessProcessing(Fragment fragment) {
    return new FragmentResult(fragment, FragmentResult.SUCCESS_TRANSITION);
  }

  private void traceFragment(Fragment fragment) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", fragment.toJson().encodePrettily());
    }
  }

  private Fragment processFragment(Fragment fragment) {
    return fragment.setBody(templateEngine.process(fragment));
  }
}
