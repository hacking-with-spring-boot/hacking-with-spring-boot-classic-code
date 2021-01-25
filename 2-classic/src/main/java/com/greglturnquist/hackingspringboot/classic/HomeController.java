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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Greg Turnquist
 */
// tag::1[]
@Controller // <1>
public class HomeController {

	private ItemRepository itemRepository;
	private CartRepository cartRepository;

	public HomeController(ItemRepository itemRepository, // <2>
			CartRepository cartRepository) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;
	}
	// end::1[]

	// tag::2[]
	@GetMapping
	String home(Model model) { // <1>
		model.addAttribute("items", //
				this.itemRepository.findAll()); // <2>
		model.addAttribute("cart", //
				this.cartRepository.findById("My Cart") // <3>
						.orElseGet(() -> new Cart("My Cart")));
		return "home";
	}
	// end::2[]

	// tag::3[]
	@PostMapping("/add/{id}") // <1>
	String addToCart(@PathVariable Integer id) { // <2>
		Cart cart = this.cartRepository.findById("My Cart") //
				.orElseGet(() -> new Cart("My Cart")); // <3>

		cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getItem().getId().equals(id)) //
				.findAny() // <4>
				.map(cartItem -> {
					cartItem.increment();
					return cart;
				}) //
				.orElseGet(() -> {
					this.itemRepository.findById(id) // <5>
							.map(item -> new CartItem(item)) //
							.map(cartItem -> {
								cart.getCartItems().add(cartItem);
								return cart;
							}) //
							.orElseGet(() -> cart);
					return cart;
				});

		this.cartRepository.save(cart); // <6>

		return "redirect:/"; // <7>
	}
	// end::3[]

	@PostMapping
	String createItem(@RequestBody Item newItem) {
		this.itemRepository.save(newItem);
		return "redirect:/";
	}

	@DeleteMapping("/delete/{id}")
	String deleteItem(@PathVariable Integer id) {
		this.itemRepository.deleteById(id);
		return "redirect:/";
	}
}
