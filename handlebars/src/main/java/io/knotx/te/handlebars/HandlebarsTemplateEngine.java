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
package io.knotx.te.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.knotx.fragment.Fragment;
import io.knotx.te.api.TemplateEngine;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;

class HandlebarsTemplateEngine implements TemplateEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsTemplateEngine.class);

  private Handlebars handlebars;
  private Cache<String, Template> cache;
  private MessageDigest digest;

  HandlebarsTemplateEngine(Vertx vertx, HandlebarsEngineOptions options) {
    LOGGER.info("<{}> instance created", this.getClass().getSimpleName());
    this.handlebars = createHandlebars(options);
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(options.getCacheSize())
        .removalListener(listener -> LOGGER.warn(
            "Cache limit exceeded. Revisit 'cacheSize' setting"))
        .build();
    try {
      this.digest = MessageDigest.getInstance(options.getCacheKeyAlgorithm());
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("No such algorithm available {}.", options.getCacheKeyAlgorithm(), e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public String process(Fragment fragment) {
    Template template = template(fragment);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing with handlebars: {}!", fragment);
    }
    try {
      return template.apply(
          Context.newBuilder(fragment.getPayload())
              .push(JsonObjectValueResolver.INSTANCE)
              .build());
    } catch (IOException e) {
      LOGGER.error("Could not apply context to fragment [{}]", fragment.abbreviate(), e);
      throw new IllegalStateException(e);
    }
  }

  private Template template(Fragment fragment) {
    try {
      String cacheKey = getCacheKey(fragment);

      return cache.get(cacheKey, () -> {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Compiled Handlebars fragment [{}]", fragment);
        }
        return handlebars.compileInline(fragment.getBody());
      });
    } catch (ExecutionException e) {
      LOGGER.error("Could not compile fragment [{}]", fragment.abbreviate(), e);
      throw new IllegalStateException(e);
    }
  }

  private String getCacheKey(Fragment fragment) {
    byte[] cacheKeyBytes = digest.digest(fragment.getBody().getBytes(StandardCharsets.UTF_8));
    return new String(cacheKeyBytes);
  }

  private Handlebars createHandlebars(HandlebarsEngineOptions options) {
    Handlebars newHandlebars = new Handlebars();
    newHandlebars.setStartDelimiter(options.getStartDelimiter());
    newHandlebars.setEndDelimiter(options.getEndDelimiter());
    ServiceLoader.load(CustomHandlebarsHelper.class)
        .iterator().forEachRemaining(helper -> {
      newHandlebars.registerHelper(helper.getName(), helper);
      LOGGER.info("Registered custom Handlebars helper: {}", helper.getName());
    });

    return newHandlebars;
  }

}
