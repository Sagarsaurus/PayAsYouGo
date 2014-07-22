package project.startup.logic.main;

public class CartItem {
	private String name;
	private int amount;
	private float price;
	
	public CartItem(String name, int amount, float price) {
		setName(name);
		setAmount(amount);
		setPrice(price);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
}
