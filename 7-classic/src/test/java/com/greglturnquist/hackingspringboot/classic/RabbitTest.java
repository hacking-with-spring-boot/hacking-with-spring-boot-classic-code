/*
 * Copyright 2019 the original author or authors.
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
package com.greglturnquist.hackingspringboot.classic;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Greg Turnquist
 */
// tag::setup[]
@SpringBootTest // <1>
@Testcontainers // <2>
@AutoConfigureMockMvc // <3>
public class RabbitTest {

	@Container static RabbitMQContainer container = new RabbitMQContainer(
			DockerImageName.parse("rabbitmq").withTag("3.7.25-management-alpine")); // <4>

	WebTestClient webTestClient;

	@Autowired ItemRepository repository; // <5>

	@DynamicPropertySource // <6>
	static void configure(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
		registry.add("spring.rabbitmq.port", container::getAmqpPort);
	}

	@BeforeEach
	void setUp(@Autowired MockMvc mockMvc) { // <7>
		this.webTestClient = MockMvcWebTestClient //
				.bindTo(mockMvc) //
				.build();
	}

	// end::setup[]

	// tag::spring-amqp-test[]
	@Test
	void verifyMessagingThroughAmqp() throws InterruptedException {
		this.webTestClient.post().uri("/items") // <1>
				.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody();

		Thread.sleep(1500L); // <2>

		this.webTestClient.post().uri("/items") // <3>
				.bodyValue(new Item("Smurf TV tray", "nothing important", 29.99)) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody();

		Thread.sleep(2000L); // <4>

		Iterable<Item> items = this.repository.findAll(); // <5>

		assertThat(items).flatExtracting(Item::getName) //
				.containsExactly("Alf alarm clock", "Smurf TV tray");
		assertThat(items).flatExtracting(Item::getDescription) //
				.containsExactly("nothing important", "nothing important");
		assertThat(items).flatExtracting(Item::getPrice) //
				.containsExactly(19.99, 29.99);
	}
	// end::spring-amqp-test[]

}
