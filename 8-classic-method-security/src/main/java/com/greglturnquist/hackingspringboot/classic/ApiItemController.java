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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ApiItemController {

	// tag::inventory[]
	private static final SimpleGrantedAuthority ROLE_INVENTORY = //
			new SimpleGrantedAuthority("ROLE_" + SecurityConfig.INVENTORY);
	// end::inventory[]

	private final ItemRepository repository;

	public ApiItemController(ItemRepository repository) {
		this.repository = repository;
	}
	// end::intro[]

	// tag::root[]
	@GetMapping("/api")
	RepresentationModel<?> root(Authentication auth) {
		ApiItemController controller = //
				methodOn(ApiItemController.class);

		Link selfLink = linkTo(controller.root(auth)).withSelfRel();

		Link itemsAggregateLink = //
				linkTo(controller.findAll(auth)) //
						.withRel(IanaLinkRelations.ITEM);

		return new RepresentationModel<>(Links.of(selfLink, itemsAggregateLink));
	}
	// end::root[]

	// tag::find-all[]
	@GetMapping("/api/items")
	CollectionModel<EntityModel<Item>> findAll(Authentication auth) {
		ApiItemController controller = methodOn(ApiItemController.class);

		Link selfLink = linkTo(controller.findAll(auth)).withSelfRel();

		Links allLinks;

		if (auth.getAuthorities().contains(ROLE_INVENTORY)) {
			Link addNewLink = linkTo(controller.addNewItem(null, auth)).withRel("add");

			allLinks = Links.of(selfLink, addNewLink);
		} else {
			allLinks = Links.of(selfLink);
		}

		List<EntityModel<Item>> items = StreamSupport.stream(this.repository.findAll().spliterator(), false) //
				.map(item -> findOne(item.getId(), auth)) //
				.collect(Collectors.toList());

		return CollectionModel.of(items, allLinks);
	}
	// end::find-all[]

	// tag::find-one[]
	@GetMapping("/api/items/{id}")
	EntityModel<Item> findOne(@PathVariable Integer id, Authentication auth) {
		ApiItemController controller = methodOn(ApiItemController.class);

		Link selfLink = linkTo(controller.findOne(id, auth)).withSelfRel();

		Link aggregateLink = linkTo(controller.findAll(auth)) //
				.withRel(IanaLinkRelations.ITEM);

		Links allLinks; // <1>

		if (auth.getAuthorities().contains(ROLE_INVENTORY)) { // <2>
			Link deleteLink = linkTo(controller.deleteItem(id)).withRel("delete");

			allLinks = Links.of(selfLink, aggregateLink, deleteLink);
		} else { // <3>
			allLinks = Links.of(selfLink, aggregateLink);
		}

		return this.repository.findById(id) // <4>
				.map(item -> EntityModel.of(item, allLinks)) //
				.orElseThrow(() -> new IllegalStateException("Couldn't find item " + id));
	}

	// end::find-one[]

	// tag::add-new-item[]
	@PreAuthorize("hasRole('" + SecurityConfig.INVENTORY + "')") // <1>
	@PostMapping("/api/items/add") // <2>
	ResponseEntity<?> addNewItem(@RequestBody Item item, Authentication auth) { // <3>
		Item savedItem = this.repository.save(item);

		EntityModel<Item> newModel = findOne(savedItem.getId(), auth);

		return ResponseEntity.created(newModel //
				.getRequiredLink(IanaLinkRelations.SELF) //
				.toUri()).build();
	}
	// end::add-new-item[]

	// tag::delete-item[]
	@PreAuthorize("hasRole('" + SecurityConfig.INVENTORY + "')")
	@DeleteMapping("/api/items/delete/{id}")
	ResponseEntity<?> deleteItem(@PathVariable Integer id) {
		this.repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	// end::delete-item[]

	// tag::update-item[]
	@PutMapping("/api/items/{id}") // <1>
	public ResponseEntity<?> updateItem(@RequestBody EntityModel<Item> itemEntity, // <2>
			@PathVariable Integer id, Authentication auth) {
		Item content = itemEntity.getContent();

		Item newItem = new Item(id, content.getName(), // <3>
				content.getDescription(), content.getPrice());

		Item savedItem = this.repository.save(newItem);

		EntityModel<Item> newModel = findOne(savedItem.getId(), auth);

		return ResponseEntity.noContent() // <6>
				.location(newModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).build();
	}
	// end::update-item[]

	// tag::profile[]
	@GetMapping(value = "/api/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
	public Alps profile() {
		return alps() //
				.descriptor(Collections.singletonList(descriptor() //
						.id(Item.class.getSimpleName() + "-representation") //
						.descriptor(Arrays.stream(Item.class.getDeclaredFields()) //
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
