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

	public String itemName;
	public double price;

	/**
	 * Setup the order item with an {$link {@link ItemEntity} and a quantity
	 * 
	 * @param item
	 * @param quantity
	 */
	public void setup(ItemEntity item, int quantity) {
		itemId = item.itemId;
		itemName = item.itemName;
		price = item.price;
		this.quantity = quantity;
	}

}
