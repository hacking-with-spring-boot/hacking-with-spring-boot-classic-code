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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

/**
 * @author Greg Turnquist
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

	WebTestClient webTestClient;

	@Autowired ItemRepository repository;

	@BeforeEach
	void setUp(@Autowired MockMvc mockMvc) {
		this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc).build();
	}

	@Test
	void verifyLoginPageBlocksAccess() {
		this.webTestClient.get().uri("/") //
				.exchange() //
				.expectStatus().isUnauthorized();
	}

	@Test
	@WithMockUser(username = "ada")
	void verifyLoginPageWorks() {
		this.webTestClient.get().uri("/") //
				.exchange() //
				.expectStatus().isOk();
	}

	// tag::add-inventory-without-role[]
	@Test
	@WithMockUser(username = "alice", roles = { "SOME_OTHER_ROLE" }) // <1>
	void addingInventoryWithoutProperRoleFails() {
		this.webTestClient.post().uri("/") // <2>
				.exchange() // <3>
				.expectStatus().isForbidden(); // <4>
	}
	// end::add-inventory-without-role[]

	// tag::add-inventory-with-role[]
	@Test
	@WithMockUser(username = "bob", roles = { "INVENTORY" }) // <1>
	void addingInventoryWithProperRoleSucceeds() throws InterruptedException {
		this.webTestClient //
				.post().uri("/") //
				.contentType(MediaType.APPLICATION_JSON) // <2>
				.bodyValue("{" + // <3>
						"\"name\": \"iPhone 11\", " + //
						"\"description\": \"upgrade\", " + //
						"\"price\": 999.99" + //
						"}") //
				.exchange() //
				.expectStatus().isFound(); // <4>

		assertThat(this.repository.findByName("iPhone 11")).hasValueSatisfying(item -> { // <5>
			assertThat(item.getDescription()).isEqualTo("upgrade"); // <6>
			assertThat(item.getPrice()).isEqualTo(999.99);
		});
	}
	// end::add-inventory-with-role[]

	@Test
	@WithMockUser(username = "carol", roles = { "SOME_OTHER_ROLE" })
	void deletingInventoryWithoutProperRoleFails() {
		this.webTestClient.delete().uri("/some-item") //
				.exchange() //
				.expectStatus().isForbidden();
	}

	@Test
	@WithMockUser(username = "dan", roles = { "INVENTORY" })
	void deletingInventoryWithProperRoleSucceeds() {
		Integer id = this.repository.findByName("Alf alarm clock") //
				.map(Item::getId) //
				.orElseThrow(() -> new IllegalStateException("Could find Alf alarm clock"));

		this.webTestClient //
				.delete().uri("/delete/" + id) //
				.exchange() //
				.expectStatus().isFound();

		assertThat(this.repository.findByName("Alf alarm clock")).isEmpty();
	}
}
