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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
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
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@WebMvcTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
public class HypermediaItemControllerDocumentationTest {

	private WebTestClient webTestClient;

	@MockBean InventoryService service;

	@MockBean ItemRepository repository;

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
		when(repository.findAll()) //
				.thenReturn(Arrays.asList( //
						new Item("Alf alarm clock", //
								"nothing I really need", 19.99)));
		when(repository.findById((Integer) null)) //
				.thenReturn(Optional.of( //
						new Item(1, "Alf alarm clock", //
								"nothing I really need", 19.99)));

		this.webTestClient.get().uri("/hypermedia/items") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findAll-hypermedia", //
						preprocessResponse(prettyPrint()))); //
	}
	// end::test1[]

	// tag::test2[]
	// @Test
	void postNewItem() {
		this.webTestClient.post().uri("/hypermedia/items") //
				.body(new Item(1, "Alf alarm clock", //
						"nothing I really need", 19.99), Item.class) //
				.exchange() //
				.expectStatus().isCreated() //
				.expectBody().isEmpty();
	}
	// end::test2[]

	// tag::test3[]
	@Test
	void findOneItem() {
		when(repository.findById(1)).thenReturn(Optional.of( //
				new Item(1, "Alf alarm clock", "nothing I really need", 19.99)));

		this.webTestClient.get().uri("/hypermedia/items/1") //
				.accept(MediaTypes.HAL_JSON) //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()), //
						links( // <1>
								linkWithRel("self").description("Canonical link to this `Item`"), // <2>
								linkWithRel("item").description("Link back to the aggregate root")))); // <3>
	}
	// end::test3[]

	@Test
	void findProfile() {
		this.webTestClient.get().uri("/hypermedia/items/profile") //
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("profile", //
						preprocessResponse(prettyPrint())));
	}
}
