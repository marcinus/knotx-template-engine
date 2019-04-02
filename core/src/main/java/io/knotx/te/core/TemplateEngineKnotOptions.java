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

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes Template Engine configuration
 */
@DataObject(generateConverter = true, publicConverter = false)
public class TemplateEngineKnotOptions {

  /**
   * Default EB address of the verticle
   */
  public final static String DEFAULT_ADDRESS = "knotx.knot.te";

  private String address;
  private String defaultEngine;
  private List<TemplateEngineEntry> engines;

  /**
   * Default constructor
   */
  public TemplateEngineKnotOptions() {
    init();
  }

  /**
   * Copy constructor
   *
   * @param other the instance to copy
   */
  public TemplateEngineKnotOptions(TemplateEngineKnotOptions other) {
    this.address = other.address;
    this.defaultEngine = other.defaultEngine;
    this.engines = other.engines;
  }

  /**
   * Create an settings from JSON
   *
   * @param json the JSON
   */
  public TemplateEngineKnotOptions(JsonObject json) {
    init();
    TemplateEngineKnotOptionsConverter.fromJson(json, this);
  }

  /**
   * Convert to JSON
   *
   * @return the JSON
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TemplateEngineKnotOptionsConverter.toJson(this, json);
    return json;
  }

  private void init() {
    address = DEFAULT_ADDRESS;
    engines = new ArrayList<>();
  }

  /**
   * @return EB address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the EB address of the verticle. Default is `knotx.knot.te`
   *
   * @param address EB address of the verticle
   * @return a reference to this, so the API can be used fluently
   */
  public TemplateEngineKnotOptions setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return default template engine name
   */
  public String getDefaultEngine() {
    return defaultEngine;
  }

  /**
   * Sets the default template engine name. This template engine will be used every time no strategy
   * is defined in the Fragment Content.
   *
   * @param defaultEngine default engine name
   * @return a reference to this, so the API can be used fluently
   */
  public TemplateEngineKnotOptions setDefaultEngine(String defaultEngine) {
    this.defaultEngine = defaultEngine;
    return this;
  }

  /**
   * @return the list of configured template engines.
   */
  public List<TemplateEngineEntry> getEngines() {
    return engines;
  }

  /**
   * List of the Template Engine Entries. Each item contains definition of new Template Engine
   * strategy.
   *
   * @param engines - list of engines.
   * @return reference to this, so the API can be used fluently
   */
  public TemplateEngineKnotOptions setEngines(List<TemplateEngineEntry> engines) {
    this.engines = engines;
    return this;
  }
}
