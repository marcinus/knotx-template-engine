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

import io.knotx.dataobjects.ClientResponse;
import io.knotx.dataobjects.Fragment;
import io.knotx.dataobjects.KnotContext;
import io.knotx.knot.AbstractKnotProxy;
import io.knotx.te.api.TemplateEngine;
import io.knotx.te.core.exception.UnsupportedEngineException;
import io.knotx.te.core.fragment.FragmentContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TemplateEngineKnotProxy extends AbstractKnotProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngineKnotProxy.class);
  private static final String SUPPORTED_FRAGMENT_ID = "te";

  private final TemplateEngineKnotOptions options;
  private final Map<String, TemplateEngine> engines;

  TemplateEngineKnotProxy(TemplateEngineKnotOptions options,
      Map<String, TemplateEngine> templateEngines) {
    this.options = options;
    this.engines = templateEngines;
  }

  @Override
  protected Single<KnotContext> processRequest(KnotContext knotContext) {
    return Optional.ofNullable(knotContext.getFragments())
        .map(fragments ->
            Observable.fromIterable(fragments)
                .filter(fragment -> fragment.knots().contains(SUPPORTED_FRAGMENT_ID))
                .doOnNext(this::traceFragment)
                .map(fragment -> FragmentContext.from(fragment, options.getDefaultEngine()))
                .flatMapSingle(this::processFragment)
                .toList()
        ).orElse(Single.just(Collections.emptyList()))
        .map(result -> createSuccessResponse(knotContext))
        .onErrorReturn(error -> processError(knotContext, error));
  }

  protected Single<FragmentContext> processFragment(FragmentContext fc) {
    return Single.just(fc)
        .map(fragmentContext -> {
          final TemplateEngine templateEngine = engines
              .get(fragmentContext.getStrategy());
          if (templateEngine != null) {
            fragmentContext.fragment().content(
                templateEngine
                    .process(fragmentContext.fragment()));
            return fragmentContext;
          } else {
            throw new UnsupportedEngineException(
                "No engine named '" + fragmentContext.getStrategy() + "' found.");
          }
        }).onErrorReturn(e-> {
          fc.fragment().failure(SUPPORTED_FRAGMENT_ID, e);
          if (!fc.fragment().fallback().isPresent()) {
            if (e instanceof RuntimeException) {
              throw (RuntimeException) e;
            } else {
              throw new IllegalStateException(e);
            }
          }
          return fc;
        });
  }

  @Override
  protected boolean shouldProcess(Set<String> knots) {
    return knots.contains(SUPPORTED_FRAGMENT_ID);
  }

  @Override
  protected KnotContext processError(KnotContext knotContext, Throwable error) {
    LOGGER.error("Error happened during Template processing", error);
    ClientResponse errorResponse = new ClientResponse()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    return new KnotContext()
        .setClientRequest(knotContext.getClientRequest())
        .setClientResponse(errorResponse);
  }

  private KnotContext createSuccessResponse(KnotContext inputContext) {
    return new KnotContext()
        .setClientRequest(inputContext.getClientRequest())
        .setClientResponse(inputContext.getClientResponse())
        .setFragments(
            Optional.ofNullable(inputContext.getFragments()).orElse(Collections.emptyList()))
        .setTransition(DEFAULT_TRANSITION);
  }

  private void traceFragment(Fragment fragment) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing fragment {}", fragment.toJson().encodePrettily());
    }
  }
}
