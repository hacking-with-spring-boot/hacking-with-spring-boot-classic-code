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

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@WebMvcTest(controllers = ApiItemController.class) // <1>
@AutoConfigureRestDocs // <2>
public class ApiItemControllerDocumentationTest {

	private WebTestClient webTestClient; // <3>

	@MockBean InventoryService service; // <4>

	@MockBean ItemRepository repository; // <5>

	@BeforeEach
	void setUp(@Autowired MockMvc mockMvc, @Autowired RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = MockMvcWebTestClient //
				.bindTo(mockMvc) //
				.filter(documentationConfiguration(restDocumentation)) //
				.build();
	}
	// end::intro[]

	// tag::test1[]
	@Test
	void findingAllItems() {
		when(repository.findAll()).thenReturn( // <1>
				Arrays.asList(new Item("item-1", "Alf alarm clock", //
						"nothing I really need", 19.99)));

		this.webTestClient.get().uri("/api/items") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findAll", preprocessResponse(prettyPrint()))); // <2>
	}
	// end::test1[]

	// tag::test2[]
	@Test
	void postNewItem() {
		when(repository.save(any())).thenReturn( //
				new Item("1", "Alf alarm clock", "nothing important", 19.99));

		this.webTestClient.post().uri("/api/items") // <1>
				.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) // <2>
				.exchange() //
				.expectStatus().isCreated() // <3>
				.expectBody(Item.class) //
				.consumeWith(document("post-new-item", preprocessResponse(prettyPrint()))); // <4>
	}
	// end::test2[]

	// tag::test3[]
	@Test
	void findOneItem() {
		when(repository.findById("item-1")).thenReturn( //
				Optional.of(new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99))); // <1>

		this.webTestClient.get().uri("/api/items/item-1") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findOne", preprocessResponse(prettyPrint()))); // <2>
	}
	// end::test3[]

}
