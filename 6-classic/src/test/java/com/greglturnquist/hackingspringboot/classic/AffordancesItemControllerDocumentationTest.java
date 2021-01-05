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
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@WebMvcTest(controllers = AffordancesItemController.class) // <1>
@AutoConfigureRestDocs // <2>
public class AffordancesItemControllerDocumentationTest {

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

	// tag::affordances[]
	@Test
	void findSingleItemAffordances() {
		when(repository.findById("item-1")).thenReturn(Optional.of( //
				new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

		this.webTestClient.get().uri("/affordances/items/item-1") // <1>
				.accept(MediaTypes.HAL_FORMS_JSON) // <2>
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("single-item-affordances", //
						preprocessResponse(prettyPrint()))); // <3>
	}
	// end::affordances[]

	// tag::affordances2[]
	@Test
	void findAggregateRootItemAffordances() {
		when(repository.findAll()).thenReturn(Arrays.asList( //
				new Item("Alf alarm clock", "nothing I really need", 19.99)));
		when(repository.findById((String) null)).thenReturn(Optional.of( //
				new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

		this.webTestClient.get().uri("/affordances/items") // <1>
				.accept(MediaTypes.HAL_FORMS_JSON) // <2>
				.exchange() //
				.expectStatus().isOk() //
				.expectBody() //
				.consumeWith(document("aggregate-root-affordances", preprocessResponse(prettyPrint()))); // <3>
	}
	// end::affordances2[]

}
