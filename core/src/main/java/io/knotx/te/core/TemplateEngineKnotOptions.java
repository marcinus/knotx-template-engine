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

/**
 * Describes Template Engine configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class TemplateEngineKnotOptions {

  public final static String DEFAULT_EB_ADDRESS = "knotx.knot.te";

  private String address;
  private TemplateEngineEntry engine;

  public TemplateEngineKnotOptions() {
    init();
  }

  public TemplateEngineKnotOptions(TemplateEngineKnotOptions other) {
    this.address = other.address;
    this.engine = other.engine;
  }

  public TemplateEngineKnotOptions(JsonObject json) {
    init();
    TemplateEngineKnotOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TemplateEngineKnotOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_EB_ADDRESS;
  }

  /**
   * @return EB address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the EB address of the Verticle. Default is {@code "knotx.knot.te"}.
   *
   * @param address EB address of the verticle
   * @return a reference to this, so the API can be used fluently
   */
  public TemplateEngineKnotOptions setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return template engine strategy
   */
  public TemplateEngineEntry getEngine() {
    return engine;
  }

  /**
   * Sets the template engine strategy for this Knot instance. This template engine will be used
   * every time Fragment is processed by this Knot.
   *
   * @param engine Template Engine strategy with configuration
   * @return a reference to this, so the API can be used fluently
   */
  public TemplateEngineKnotOptions setEngine(TemplateEngineEntry engine) {
    this.engine = engine;
    return this;
  }
}
