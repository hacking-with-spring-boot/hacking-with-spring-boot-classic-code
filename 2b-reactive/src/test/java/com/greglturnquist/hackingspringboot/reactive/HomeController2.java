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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController2 {

	private final CartService cartService;

	public HomeController2(CartService cartService) {
		this.cartService = cartService;
	}

	// tag::4[]
	@PostMapping("/add/{id}")
	Mono<String> addToCart(@PathVariable String id) {
		return this.cartService.addToCart("My Cart", id) //
				.thenReturn("redirect:/");
	}
	// end::4[]

}
