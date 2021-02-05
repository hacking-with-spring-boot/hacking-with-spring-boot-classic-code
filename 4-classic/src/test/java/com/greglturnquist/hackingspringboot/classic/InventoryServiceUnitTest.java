/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greglturnquist.hackingspringboot.classic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Greg Turnquist
 */
// tag::extend[]
@ExtendWith(SpringExtension.class) // <1>
class InventoryServiceUnitTest { // <2>
	// end::extend[]

	// tag::class-under-test[]
	InventoryService inventoryService; // <1>

	@MockBean private ItemRepository itemRepository; // <2>

	@MockBean private CartRepository cartRepository; // <2>
	// end::class-under-test[]

	// tag::before[]
	@BeforeEach // <1>
	void setUp() {
		// Define test data <2>
		Item sampleItem = new Item(1, "TV tray", "Alf TV tray", 19.99);
		CartItem sampleCartItem = new CartItem(sampleItem, null);
		Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));
		sampleCartItem.setCart(sampleCart);

		// Define mock interactions provided
		// by your collaborators <3>
		when(cartRepository.findById(anyString())).thenReturn(Optional.empty());
		when(itemRepository.findById(anyInt())).thenReturn(Optional.of(sampleItem));
		when(cartRepository.save(any(Cart.class))).thenReturn(sampleCart);

		inventoryService = new InventoryService(itemRepository, cartRepository); // <4>
	}
	// end::before[]

	// tag::test[]
	@Test
	void addItemToEmptyCartShouldProduceOneCartItem() { // <1>
		Cart cart = inventoryService.addItemToCart("My Cart", 1); // <2>

		assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
				.containsExactlyInAnyOrder(1); // <3>

		assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
				.containsExactly(new Item(1, "TV tray", "Alf TV tray", 19.99)); // <4>
	}
	// end::test[]
}
