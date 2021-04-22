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
// tag::code[]
package com.greglturnquist.hackingspringboot.classic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class KitchenService {

	/**
	 * Generates a list of random dish(es).
	 */
	List<Dish> getDishes() {
		return Arrays.asList(randomDish());
	}

	/**
	 * Randomly pick the next dish.
	 */
	private Dish randomDish() {
		return this.menu.get(picker.nextInt(3));
	}

	private List<Dish> menu = Arrays.asList( //
			new Dish("Sesame chicken"), //
			new Dish("Lo mein noodles, plain"), //
			new Dish("Sweet & sour beef"));

	private Random picker = new Random();
}
// end::code[]
