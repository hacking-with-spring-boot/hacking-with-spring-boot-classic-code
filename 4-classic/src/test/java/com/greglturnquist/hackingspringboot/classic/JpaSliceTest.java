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

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@DataJpaTest // <1>
public class JpaSliceTest {

	@Autowired ItemRepository repository; // <2>

	@Test // <3>
	void itemRepositorySavesItems() {
		Item sampleItem = new Item( //
				"name", "description", 1.99);

		Item savedItem = repository.save(sampleItem);

		assertThat(savedItem.getId()).isNotNull();
		assertThat(savedItem.getName()).isEqualTo("name");
		assertThat(savedItem.getDescription()).isEqualTo("description");
		assertThat(savedItem.getPrice()).isEqualTo(1.99);
	}
}
// end::code[]
