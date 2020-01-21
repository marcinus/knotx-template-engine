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
package io.knotx.te.pebble;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.knotx.fragments.api.Fragment;
import io.knotx.te.api.TemplateEngine;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

class PebbleTemplateEngine implements TemplateEngine {

  private static final Logger LOGGER = LoggerFactory.getLogger(PebbleTemplateEngine.class);

  private final PebbleEngine pebbleEngine;
  private final Cache<String, PebbleTemplate> cache;
  private final MessageDigest digest;

  PebbleTemplateEngine(Vertx vertx, PebbleEngineOptions options) {
    LOGGER.info("<{}> instance created", this.getClass().getSimpleName());
    this.pebbleEngine = new PebbleEngine.Builder().loader(new StringLoader()).cacheActive(false)
        .build();
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
    PebbleTemplate template = template(fragment);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Processing with Peeble: {}!", fragment);
    }
    try {
      StringWriter writer = new StringWriter();
      template.evaluate(writer, fragment.getPayload().getMap());
      return writer.toString();
    } catch (IOException e) {
      LOGGER.error("Could not apply context to fragment [{}]", fragment.abbreviate(), e);
      throw new IllegalStateException(e);
    }
  }

  private PebbleTemplate template(Fragment fragment) {
    try {
      String cacheKey = getCacheKey(fragment);

      return cache.get(cacheKey, () -> {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Compiled Pebble fragment [{}]", fragment);
        }
        return pebbleEngine.getTemplate(fragment.getBody());
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

}
