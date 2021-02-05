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

import java.util.Objects;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Entity
class CartItem {

	private @Id @GeneratedValue Integer id;
	private @ManyToOne(fetch = FetchType.LAZY) Cart cart;
	private @ManyToOne(fetch = FetchType.LAZY) Item item;
	private int quantity;

	protected CartItem() {}

	CartItem(Item item, Cart cart) {
		this.item = item;
		this.cart = cart;
		this.quantity = 1;
	}

	// end::code[]

	public void increment() {
		this.quantity++;
	}

	public void decrement() {
		this.quantity--;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CartItem))
			return false;
		CartItem cartItem = (CartItem) o;
		return quantity == cartItem.quantity && Objects.equals(id, cartItem.id) && Objects.equals(cart, cartItem.cart)
				&& Objects.equals(item, cartItem.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, cart, item, quantity);
	}

	@Override
	public String toString() {
		String cartId = Optional.ofNullable(this.cart).map(Cart::getId).orElse("NA");
		Integer itemId = Optional.ofNullable(this.item).map(Item::getId).orElse(-1);

		return "CartItem{" + "id=" + id + ", cartId=" + cartId + ", itemId=" + itemId + ", quantity=" + quantity + '}';
	}
}
