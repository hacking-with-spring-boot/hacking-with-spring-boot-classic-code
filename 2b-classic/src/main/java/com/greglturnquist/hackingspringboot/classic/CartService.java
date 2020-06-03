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

import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service // <1>
class CartService {

	private final ItemRepository itemRepository;
	private final com.greglturnquist.hackingspringboot.classic.CartRepository cartRepository;

	CartService(ItemRepository itemRepository, // <2>
			CartRepository cartRepository) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;
	}

	Cart addToCart(String cartId, String id) { // <3>

		Cart cart = this.cartRepository.findById(cartId) //
				.orElseGet(() -> new Cart(cartId));

		cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getItem().getId().equals(id)) //
				.findAny() //
				.map(cartItem -> {
					cartItem.increment();
					return cart;
				}) //
				.orElseGet(() -> {
					Item item = this.itemRepository.findById(id) //
							.orElseThrow(() -> new IllegalStateException("Can't seem to find Item type " + id));
					cart.getCartItems().add(new CartItem(item));
					return cart;
				});

		return this.cartRepository.save(cart);
	}
}
// end::code[]
