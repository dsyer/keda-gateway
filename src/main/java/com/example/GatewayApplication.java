/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Gateway application to use for testing, aggregates 2 public APIs from the internet and
 * serves them up through a gateway. Run from the IDE or command line with
 * <code>./mvnw spring-boot:test-run</code>.
 */
@SpringBootApplication
public class GatewayApplication {

	private String app = "http://app";

	private String dates = "https://date.nager.at";

	private String wizards = "https://wizard-world-api.herokuapp.com";

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	RouteLocator gateway(RouteLocatorBuilder rlb) {
		return rlb.routes()
			.route(r -> r.path("/app/**").filters(f -> f.stripPrefix(1)).uri(app))
			.route(r -> r.path("/dates/**").filters(f -> f.stripPrefix(1).prefixPath("/api/v3")).uri(dates))
			.route(r -> r.path("/wizards/**").filters(f -> f.stripPrefix(1)).uri(wizards))
			.build();
	}

}