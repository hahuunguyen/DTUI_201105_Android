package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class OrderEntity {
	private String m_table;
	private ArrayList<Integer> m_item;
	
	public OrderEntity (String table){
		m_table = table;
		m_item = new ArrayList<Integer>();
	}
	
	public OrderEntity () {
		this(null);
	}
	
	public void setTable(String table){
		m_table = table;
	}
	/*
	 *  them vao 1 item
	 * 
	 */
	public void addItem(int item, int quantity){
		if ( item > 0 && quantity > 0){
			for ( int i = 0; i <quantity;i++){
				Integer newInteger = new Integer(quantity);
				m_item.add(item);
			}
		}
		else {
			
		}
	}
	
	/* tra ve list kieu NameValuePair
	 * duoc su dung de post du lieu cua 1 order len server
	 */
	public List<NameValuePair> getOrder(){
		if ( m_item.isEmpty() || m_item == null)
			return null;
		else {
			List<NameValuePair> order = new ArrayList<NameValuePair>();
			order.add(new BasicNameValuePair("table_id", m_table));
			int length = m_item.size();
			for (int i =0  ; i<length;i++){
				order.add(new BasicNameValuePair("order_item["+i+"]", m_item.get(i).toString() ));
			}
			return order;
		}
	}
}
