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
package io.knotx.te.api;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

/**
 * Defines factory object that provides handler instance with the particular name.
 */
public interface TemplateEngineFactory {

  /**
   * The handler name that is used in operations configuration.
   * @return the handler name
   */
  String getName();

  /**
   * Creates template engine instance.
   * @param vertx vertx instance
   * @param config handler configuration
   * @return handler instance
   */
  TemplateEngine create(Vertx vertx, JsonObject config);

}
