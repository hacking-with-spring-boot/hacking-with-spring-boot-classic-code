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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
class InventoryService {

	private ItemRepository itemRepository;

	private CartRepository cartRepository;

	InventoryService(ItemRepository repository, //
			CartRepository cartRepository) {
		this.itemRepository = repository;
		this.cartRepository = cartRepository;
	}

	public Optional<Cart> getCart(String cartId) {
		return this.cartRepository.findById(cartId);
	}

	public Iterable<Item> getInventory() {
		return this.itemRepository.findAll();
	}

	Item saveItem(Item newItem) {
		return this.itemRepository.save(newItem);
	}

	void deleteItem(Integer id) {
		this.itemRepository.deleteById(id);
	}

	// tag::logging[]
	Cart addItemToCart(String cartId, Integer itemId) {

		Cart cart = this.cartRepository.findById(cartId) //
				.orElseGet(() -> new Cart("My Cart")); // <3>

		cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getItem().getId().equals(itemId)) //
				.findAny() //
				.map(cartItem -> {
					cartItem.increment();
					return cart;
				}) //
				.orElseGet(() -> {
					Item item = this.itemRepository.findById(itemId)
							.orElseThrow(() -> new IllegalStateException("Can't seem to find Item type " + itemId));
					cart.getCartItems().add(new CartItem(item, cart));
					return cart;
				});

		return this.cartRepository.save(cart);
	}
	// end::logging[]

	Cart removeOneFromCart(String cartId, Integer itemId) {

		Cart cart = this.cartRepository.findById("My Cart") //
				.orElseGet(() -> new Cart("My Cart")); // <3>

		cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getItem().getId().equals(itemId)) //
				.findAny() //
				.ifPresent(cartItem -> {
					cartItem.decrement();
				});

		List<CartItem> updatedCartItems = cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getQuantity() > 0) //
				.collect(Collectors.toList());

		cart.setCartItems(updatedCartItems);

		return this.cartRepository.save(cart);
	}
}
// end::code[]
