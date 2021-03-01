package roles;

public class Seller extends Person {

	static final int m = 1;
	
	public Seller(String type, String id, String product, String[] neighbors, int count) {
		super(type, id, product, neighbors, count);
	}

	@Override
	public void buy(String sellerID) {
		decrementItemNum();
		if(getItemNum() == 0) {
			resetItemNum();
			product = productList[r.nextInt(productList.length)];
			System.out.println(String.format("SellerID:%s start to sell %s", id, product));
		}
	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
		System.out.println(String.format("SellerID:%s reply BuyerID:%s", sellerID, buyerID));
		return false;
	}

	@Override
	public boolean lookUp(String product, int hopCount) {
		return product.equals(this.product);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
