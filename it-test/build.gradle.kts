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
import org.nosphere.apache.rat.RatTask

plugins {
  id("java-library")
  id("org.nosphere.apache.rat") version "0.4.0"
}

description = "Knot.x Template Engine Integration Tests"

dependencies {
  implementation(project(":knotx-template-engine-api"))
  implementation(project(":knotx-template-engine-core"))
  implementation(project(":knotx-template-engine-handlebars"))

  testImplementation("io.knotx:knotx-junit5:${project.version}")
  testImplementation("io.knotx:knotx-launcher:${project.version}")
}

sourceSets.named("test") {
  resources.srcDir("../conf")
}

tasks {
  named<RatTask>("rat") {
    excludes.addAll("**/build/*", "**/out/*", "**/resources/*")
  }
  getByName("build").dependsOn("rat")
}

apply(from = "../gradle/common.deps.gradle.kts")
