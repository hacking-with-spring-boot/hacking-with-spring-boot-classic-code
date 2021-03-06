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
// tag::controller[]
package com.greglturnquist.hackingspringboot.classic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

	private final KitchenService kitchen;

	public ServerController(KitchenService kitchen) {
		this.kitchen = kitchen;
	}

	@GetMapping("/server")
	List<Dish> serveDishes() {
		return this.kitchen.getDishes();
	}
	// end::controller[]

	// tag::deliver[]
	@GetMapping("/served-dishes")
	List<Dish> deliverDishes() {
		return this.kitchen.getDishes().stream() //
				.map(dish -> Dish.deliver(dish)) //
				.collect(Collectors.toList());
	}
	// end::deliver[]
}
