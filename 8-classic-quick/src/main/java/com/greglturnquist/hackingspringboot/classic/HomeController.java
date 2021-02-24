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
@Controller
public class HomeController {

	private final InventoryService inventoryService;

	public HomeController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@GetMapping
	String home(Model model) { // <1>
		model.addAttribute("items", //
				this.inventoryService.getInventory());
		model.addAttribute("cart", //
				this.inventoryService.getCart("My Cart") //
						.orElseGet(() -> new Cart("My Cart")));

		return "home";
	}

	@PostMapping("/add/{id}")
	String addToCart(@PathVariable Integer id) {
		this.inventoryService.addItemToCart("My Cart", id);
		return "redirect:/";
	}

	@DeleteMapping("/remove/{id}")
	String removeFromCart(@PathVariable Integer id) {
		this.inventoryService.removeOneFromCart("My Cart", id);
		return "redirect:/";
	}

	@PostMapping
	String createItem(@RequestBody Item newItem) {
		this.inventoryService.saveItem(newItem);
		return "redirect:/";
	}

	@DeleteMapping("/delete/{id}")
	String deleteItem(@PathVariable Integer id) {
		this.inventoryService.deleteItem(id);
		return "redirect:/";
	}
}
