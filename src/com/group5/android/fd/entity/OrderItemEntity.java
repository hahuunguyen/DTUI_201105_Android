package com.group5.android.fd.entity;

/**
 * An order item
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class OrderItemEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4124202957497494844L;

	public int itemId;
	public int quantity;
	public int orderItemId;

	public String itemName;
	public double price;

	/**
	 * Setup the order item with an {@link ItemEntity} and a quantity
	 * 
	 * @param item
	 * @param quantity
	 * @param orderItemId
	 */
	public void setup(ItemEntity item, int quantity, int orderItemId) {
		itemId = item.itemId;
		itemName = item.itemName;
		price = item.price;
		this.quantity = quantity;
		this.orderItemId = orderItemId;
	}

	/**
	 * Setup the order item with an {@link ItemEntity} and a quantity
	 * 
	 * @param item
	 * @param quantity
	 */
	public void setup(ItemEntity item, int quantity) {
		setup(item, quantity, 0);
	}

	/**
	 * Setup the order item from another task
	 * 
	 * @param other
	 */
	public void parse(OrderItemEntity other) {
		itemId = other.itemId;
		itemName = other.itemName;
		price = other.price;
		quantity = other.quantity;
		orderItemId = other.orderItemId;
	}

}
