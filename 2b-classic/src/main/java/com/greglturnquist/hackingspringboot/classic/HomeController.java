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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Greg Turnquist
 */
// tag::1[]
@Controller // <1>
public class HomeController {

	private ItemRepository itemRepository;
	private com.greglturnquist.hackingspringboot.classic.CartRepository cartRepository;
	private InventoryService inventoryService;

	public HomeController(ItemRepository itemRepository, CartRepository cartRepository,
			InventoryService inventoryService) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;
		this.inventoryService = inventoryService;
	}
	// end::1[]

	// tag::2[]
	@GetMapping
	String home(Model model) { // <1>
		model.addAttribute("items", //
				this.itemRepository.findAll()); // <3>
		model.addAttribute("cart", //
				this.cartRepository.findById("My Cart") // <4>
						.orElseGet(() -> new Cart("My Cart")));
		return "home";
	}
	// end::2[]

	// tag::3[]
	@PostMapping("/add/{id}")
	// <1>
	String addToCart(@PathVariable String id) { // <2>
		Cart cart = this.cartRepository.findById("My Cart") //
				.orElseGet(() -> new Cart("My Cart")); // <3>

		cart.getCartItems().stream() //
				.filter(cartItem -> cartItem.getItem().getId().equals(id)) //
				.findAny() //
				.map(cartItem -> {
					cartItem.increment();
					return cart;
				}) //
				.orElseGet(() -> {
					this.itemRepository.findById(id) //
							.map(item -> new CartItem(item)) //
							.map(cartItem -> {
								cart.getCartItems().add(cartItem);
								return cart;
							}) //
							.orElseGet(() -> cart);
					return cart;
				});

		this.cartRepository.save(cart);

		return "redirect:/";
	}
	// end::3[]

	@PostMapping
	String createItem(@ModelAttribute Item newItem) {
		this.itemRepository.save(newItem);
		return "redirect:/";
	}

	@DeleteMapping("/delete/{id}")
	String deleteItem(@PathVariable String id) {
		this.itemRepository.deleteById(id);
		return "redirect:/";
	}

	// tag::search[]
	@GetMapping("/search") // <1>
	String search( //
			@RequestParam(required = false) String name, // <2>
			@RequestParam(required = false) String description, //
			@RequestParam boolean useAnd, //
			Model model) {
		model.addAttribute("results", inventoryService.searchByExample(name, description, useAnd));
		return "home";
	}
	// end::search[]
}
