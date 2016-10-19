package com.freshO2O.shoppingcart;

public class ShopBean implements java.io.Serializable{
	private int GId;				//商品ID
	private String GPic;		//商品图片资源ID
	private String storeName;		//店家名称
	private String shopName;		//商品名称
	private String shopDescription;	//商品描述
	private double shopPrice;		//商品单价
	private int shopNumber;			//商品数量
	private boolean isChoosed;		//商品是否在购物车中被选中
	public int getShopId() {
		return GId;
	}
	public void setShopId(int shopId) {
		this.GId = shopId;
	}
	public String getShopPicture() {
		return GPic;
	}
	public void setShopPicture(String shopPicture) {
		this.GPic = shopPicture;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getShopDescription() {
		return shopDescription;
	}
	public void setShopDescription(String shopDescription) {
		this.shopDescription = shopDescription;
	}
	public double getShopPrice() {
		return shopPrice;
	}
	public void setShopPrice(double shopPrice) {
		this.shopPrice = shopPrice;
	}
	public int getShopNumber() {
		return shopNumber;
	}
	public void setShopNumber(int shopNumber) {
		this.shopNumber = shopNumber;
	}
	public boolean isChoosed() {
		return isChoosed;
	}
	public void setChoosed(boolean isChoosed) {
		this.isChoosed = isChoosed;
	}
}
