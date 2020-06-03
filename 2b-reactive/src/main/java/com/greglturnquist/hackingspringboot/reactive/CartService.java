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

package com.greglturnquist.hackingspringboot.reactive;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service // <1>
class CartService {

	private final ItemRepository itemRepository;
	private final CartRepository cartRepository;

	CartService(ItemRepository itemRepository, // <2>
			CartRepository cartRepository) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;
	}

	Mono<Cart> addToCart(String cartId, String id) { // <3>
		return this.cartRepository.findById(cartId) //
				.defaultIfEmpty(new Cart(cartId)) //
				.flatMap(cart -> cart.getCartItems().stream() //
						.filter(cartItem -> cartItem.getItem().getId().equals(id)) //
						.findAny() //
						.map(cartItem -> {
							cartItem.increment();
							return Mono.just(cart);
						}).orElseGet(() -> {
							return this.itemRepository.findById(id) //
									.map(CartItem::new) // <4>
									.doOnNext(cartItem -> cart.getCartItems().add(cartItem)) //
									.map(cartItem -> cart);
						}))
				.flatMap(this.cartRepository::save); // <5>
	}
}
// end::code[]
