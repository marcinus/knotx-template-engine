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

import io.knotx.engine.api.FragmentEventContext;
import io.knotx.engine.api.FragmentEventResult;
import io.knotx.engine.api.KnotProcessingFatalException;
import io.knotx.engine.api.TraceableKnotOptions;
import io.knotx.engine.api.TraceableKnotProxy;
import io.knotx.fragment.Fragment;
import io.knotx.te.api.TemplateEngine;
import io.knotx.te.core.fragment.FragmentContext;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;

public class TemplateEngineKnotProxy extends TraceableKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineKnotProxy.class);
  private static final String DEFAULT_TRANSITION = "next";

  private final TemplateEngineKnotOptions options;
  private final Map<String, TemplateEngine> engines;

  TemplateEngineKnotProxy(TemplateEngineKnotOptions options,
      Map<String, TemplateEngine> templateEngines) {
    // TODO make it configurable
    super(new TraceableKnotOptions());
    this.options = options;
    this.engines = templateEngines;
  }

  @Override
  protected Maybe<FragmentEventResult> execute(FragmentEventContext fragmentContext) {
    return Single.just(fragmentContext)
        .map(ctx -> FragmentContext.from(ctx.getFragmentEvent(), options.getDefaultEngine()))
        .flatMap(this::processFragment)
        .map(this::createSuccessResponse)
        .toMaybe();
  }

  @Override
  protected String getAddress() {
    return null;
  }

  protected Single<FragmentContext> processFragment(FragmentContext fc) {
    return Single.just(fc)
        .map(fragmentContext -> {
          final TemplateEngine templateEngine = engines
              .get(fragmentContext.strategy());
          Fragment fragment = fragmentContext.event().getFragment();
          if (templateEngine != null) {
            fragment.setBody(templateEngine.process(fragment));
            return fragmentContext;
          } else {
            throw new KnotProcessingFatalException(fragment);
          }
        });
  }

  private FragmentEventResult createSuccessResponse(FragmentContext fragmentContext) {
    return new FragmentEventResult(fragmentContext.event(), DEFAULT_TRANSITION);
  }

  private void traceFragment(FragmentContext ctx) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", ctx.event().toJson().encodePrettily());
    }
  }
}
