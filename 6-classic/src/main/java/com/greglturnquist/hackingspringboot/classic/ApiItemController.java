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

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@RestController // <1>
public class ApiItemController {

	private final ItemRepository repository; // <2>

	public ApiItemController(ItemRepository repository) {
		this.repository = repository; // <3>
	}
	// end::intro[]

	// tag::all-items[]
	@GetMapping("/api/items") // <1>
	Iterable<Item> findAll() { // <2>
		return this.repository.findAll(); // <3>
	}
	// end::all-items[]

	// tag::one-item[]
	@GetMapping("/api/items/{id}") // <1>
	Optional<Item> findOne(@PathVariable Integer id) { // <2>
		return this.repository.findById(id); // <3>
	}
	// end::one-item[]

	// tag::new-item[]
	@PostMapping("/api/items") // <1>
	ResponseEntity<?> addNewItem(@RequestBody Item item) { // <2>

		Item savedItem = this.repository.save(item);

		return ResponseEntity // <3>
				.created(URI.create("/api/items/" + //
						savedItem.getId()))
				.body(savedItem); // <4>
	}
	// end::new-item[]

	// tag::replace-item[]
	@PutMapping("/api/items/{id}") // <1>
	public ResponseEntity<?> updateItem( //
			@RequestBody Item item, // <2>
			@PathVariable Integer id) { // <3>

		Item newItem = new Item(id, item.getName(), item.getDescription(), // <4>
				item.getPrice());

		this.repository.save(newItem); // <5>

		return ResponseEntity.created(URI.create("/api/items/" + id)).build(); // <6>
	}
	// end::replace-item[]
}
