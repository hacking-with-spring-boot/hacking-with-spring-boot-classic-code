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

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
class InventoryService {

	private ItemRepository repository;
	private ItemByExampleRepository exampleRepository;

	InventoryService(ItemRepository repository, //
			ItemByExampleRepository exampleRepository) {
		this.repository = repository;
		this.exampleRepository = exampleRepository;
	}

	List<Item> getItems() {
		// imagine calling a remote service!
		return Collections.emptyList();
	}

	// tag::code-2[]
	Iterable<Item> search(String partialName, String partialDescription, boolean useAnd) {
		if (partialName != null) {
			if (partialDescription != null) {
				if (useAnd) {
					return repository //
							.findByNameContainingAndDescriptionContainingAllIgnoreCase( //
									partialName, partialDescription);
				} else {
					return repository.findByNameContainingOrDescriptionContainingAllIgnoreCase( //
							partialName, partialDescription);
				}
			} else {
				return repository.findByNameContaining(partialName);
			}
		} else {
			if (partialDescription != null) {
				return repository.findByDescriptionContainingIgnoreCase(partialDescription);
			} else {
				return repository.findAll();
			}
		}
	}
	// end::code-2[]

	// tag::code-3[]
	Iterable<Item> searchByExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0); // <1>

		ExampleMatcher matcher = (useAnd // <2>
				? ExampleMatcher.matchingAll() //
				: ExampleMatcher.matchingAny()) //
						.withStringMatcher(StringMatcher.CONTAINING) // <3>
						.withIgnoreCase() // <4>
						.withIgnorePaths("price"); // <5>

		Example<Item> probe = Example.of(item, matcher); // <6>

		return exampleRepository.findAll(probe); // <7>
	}
	// end::code-3[]

}
// end::code[]
