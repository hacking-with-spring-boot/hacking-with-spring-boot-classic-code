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

import static org.springframework.hateoas.mediatype.alps.Alps.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
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
@RestController
public class AffordancesItemController {

	private final ItemRepository repository;

	public AffordancesItemController(ItemRepository repository) {
		this.repository = repository;
	}
	// end::intro[]

	// tag::root[]
	@GetMapping("/affordances")
	RepresentationModel<?> root() {
		AffordancesItemController controller = methodOn(AffordancesItemController.class);

		Link selfLink = linkTo(controller.root()) //
				.withSelfRel();

		Link itemsAggregateLink = linkTo(controller.findAll()) //
				.withRel(IanaLinkRelations.ITEM);

		return new RepresentationModel<>(Links.of(selfLink, itemsAggregateLink));
	}
	// end::root[]

	// tag::find-all[]
	@GetMapping("/affordances/items")
	CollectionModel<EntityModel<Item>> findAll() {
		AffordancesItemController controller = methodOn(AffordancesItemController.class);

		Link aggregateRoot = linkTo(controller.findAll()) //
				.withSelfRel() //
				.andAffordance(afford(controller.addNewItem(null))); // <1>

		List<EntityModel<Item>> entityModels = //
				StreamSupport.stream(this.repository.findAll().spliterator(), false) // <2>
						.map(item -> findOne(item.getId())) // <3>
						.collect(Collectors.toList());

		return CollectionModel.of(entityModels, aggregateRoot); // <4>
	}
	// end::find-all[]

	// tag::find-one[]
	@GetMapping("/affordances/items/{id}") // <1>
	EntityModel<Item> findOne(@PathVariable Integer id) {
		AffordancesItemController controller = methodOn(AffordancesItemController.class); // <2>

		Link selfLink = linkTo(controller.findOne(id)) //
				.withSelfRel() //
				.andAffordance(afford(controller.updateItem(null, id))); // <3>

		Link aggregateLink = linkTo(controller.findAll()) //
				.withRel(IanaLinkRelations.ITEM);

		return this.repository.findById(id) //
				.map(item -> EntityModel.of(item, selfLink, aggregateLink)) //
				.orElseThrow(() -> new IllegalStateException("Couldn't find item " + id));
	}
	// end::find-one[]

	// tag::add-new-item[]
	@PostMapping("/affordances/items") // <1>
	ResponseEntity<?> addNewItem(@RequestBody EntityModel<Item> itemEntity) { // <2>
		Item content = itemEntity.getContent(); // <3>
		Item savedItem = this.repository.save(content); // <4>
		EntityModel<Item> newModel = findOne(savedItem.getId()); // <5>

		return ResponseEntity // <6>
				.created(newModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(newModel.getContent()); //
	}
	// end::add-new-item[]

	// tag::update-item[]
	@PutMapping("/affordances/items/{id}") // <1>
	public ResponseEntity<?> updateItem(@RequestBody EntityModel<Item> itemEntity, // <2>
			@PathVariable Integer id) {
		Item content = itemEntity.getContent();
		Item newItem = new Item(id, content.getName(), // <3>
				content.getDescription(), content.getPrice());

		this.repository.save(newItem); // <4>

		return ResponseEntity.noContent() //
				.location(findOne(id).getRequiredLink(IanaLinkRelations.SELF).toUri()) // <5>
				.build();
	}
	// end::update-item[]

	// tag::profile[]
	@GetMapping(value = "/affordances/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
	public Alps profile() {
		return alps() //
				.descriptor(Collections.singletonList(descriptor() //
						.id(Item.class.getSimpleName() + "-representation") //
						.descriptor( //
								Arrays.stream(Item.class.getDeclaredFields()) //
										.map(field -> descriptor() //
												.name(field.getName()) //
												.type(Type.SEMANTIC) //
												.build()) //
										.collect(Collectors.toList())) //
						.build())) //
				.build();
	}
	// end::profile[]
}
