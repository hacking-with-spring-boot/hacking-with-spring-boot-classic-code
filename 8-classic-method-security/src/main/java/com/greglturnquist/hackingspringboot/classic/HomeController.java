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

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private final InventoryService inventoryService;

	public HomeController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	// tag::user-cart[]
	@GetMapping
	String home(Authentication auth, Model model) { // <1>
		model.addAttribute("items", //
				this.inventoryService.getInventory());
		model.addAttribute("cart", //
				this.inventoryService.getCart(cartName(auth)) //
						.orElseGet(() -> new Cart(cartName(auth)))); // <2>
		model.addAttribute("auth", auth);

		return "home";
	}
	// end::user-cart[]

	// tag::adjust-cart[]
	@PostMapping("/add/{id}")
	String addToCart(Authentication auth, @PathVariable Integer id) {
		this.inventoryService.addItemToCart(cartName(auth), id);
		return "redirect:/";
	}

	@DeleteMapping("/remove/{id}")
	String removeFromCart(Authentication auth, @PathVariable Integer id) {
		this.inventoryService.removeOneFromCart(cartName(auth), id);
		return "redirect:/";
	}
	// end::adjust-cart[]

	// tag::cartName[]
	private static String cartName(Authentication auth) {
		return auth.getName() + "'s Cart";
	}
	// end::cartName[]
}
