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

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

// tag::code[]
public interface ItemRepository extends CrudRepository<Item, String> {

	List<Item> findByNameContaining(String partialName);
	// end::code[]

	// tag::code-2[]
	@Query("{ 'name' : ?0, 'age' : ?1 }")
	List<Item> findItemsForCustomerMonthlyReport(String name, int age);

	@Query(sort = "{ 'age' : -1 }")
	List<Item> findSortedStuffForWeeklyReport();
	// end::code-2[]

	// tag::code-3[]
	// search by name
	List<Item> findByNameContainingIgnoreCase(String partialName);

	// search by description
	List<Item> findByDescriptionContainingIgnoreCase(String partialName);

	// search by name AND description
	List<Item> findByNameContainingAndDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

	// search by name OR description
	List<Item> findByNameContainingOrDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);
	// end::code-3[]
}
