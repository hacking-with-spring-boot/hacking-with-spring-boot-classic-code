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
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@WebMvcTest(HomeController.class) // <1>
public class HomeControllerSliceTest {

	private WebTestClient client;

	@MockBean // <3>
	InventoryService inventoryService;

	@BeforeEach
	void setUp(@Autowired MockMvc mockMvc) {
		this.client = MockMvcWebTestClient.bindTo(mockMvc).build();
	}

	@Test
	void homePage() {
		when(inventoryService.getInventory()).thenReturn(Arrays.asList( //
				new Item(1, "name1", "desc1", 1.99), //
				new Item(2, "name2", "desc2", 9.99) //
		));
		when(inventoryService.getCart("My Cart")) //
				.thenReturn(Optional.of(new Cart("My Cart")));

		client.get().uri("/").exchange() //
				.expectStatus().isOk() //
				.expectBody(String.class) //
				.consumeWith(exchangeResult -> {
					assertThat( //
							exchangeResult.getResponseBody()).contains("action=\"/add/1\"");
					assertThat( //
							exchangeResult.getResponseBody()).contains("action=\"/add/2\"");
				});
	}
}
// end::code[]
