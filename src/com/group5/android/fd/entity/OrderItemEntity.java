package com.group5.android.fd.entity;

public class OrderItemEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4124202957497494844L;

	public int itemId;
	public int quantity;
	

	// secondary data
	public String itemName;
	public double price;

	public void setup(ItemEntity item, int quantity) {
		itemId = item.itemId;
		itemName = item.itemName;
		price = item.price;
		this.quantity = quantity;
	}
	
}
