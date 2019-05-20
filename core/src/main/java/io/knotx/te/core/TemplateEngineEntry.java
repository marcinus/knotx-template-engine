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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.commons.lang3.StringUtils;

/**
 * Describes a details of template engine.
 */
@DataObject(generateConverter = true, publicConverter = false)
public class TemplateEngineEntry {

  private String factory;
  private JsonObject config;

  /**
   * Create settings from JSON
   *
   * @param json the JSON
   */
  public TemplateEngineEntry(JsonObject json) {
    init();
    TemplateEngineEntryConverter.fromJson(json, this);
    if (StringUtils.isBlank(factory)) {
      throw new IllegalStateException("Engine name in engines configuration can not be null!");
    }
  }

  private void init() {
    this.config = new JsonObject();
  }

  /**
   * @return {@link io.knotx.te.api.TemplateEngineFactory} name
   */
  public String getFactory() {
    return factory;
  }

  /**
   * Sets the template engine factory name. This name would be used to get the {@code
   * TemplateEngineFactory} that provides the Template Engine
   *
   * @param factory template factory name
   * @return reference to this, so the API can be used fluently
   */
  public TemplateEngineEntry setFactory(String factory) {
    this.factory = factory;
    return this;
  }

  /**
   * @return JSON configuration used during {@link io.knotx.te.api.TemplateEngineFactory#create(Vertx,
   * JsonObject)} initialization
   */
  public JsonObject getConfig() {
    return config == null ? new JsonObject() : config;
  }

  /**
   * Sets {@code io.knotx.te.api.TemplateEngine} implementation configuration.
   *
   * @param config handler JSON configuration
   * @return reference to this, so the API can be used fluently
   */
  public TemplateEngineEntry setConfig(JsonObject config) {
    this.config = config;
    return this;
  }

  @Override
  public String toString() {
    return "TemplateEngineEntry{" +
        "factory='" + factory + '\'' +
        ", config=" + config +
        '}';
  }
}
