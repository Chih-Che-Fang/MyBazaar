package roles;

public class Seller extends Person {

	static final int m = 1;
	
	public Seller(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}

	@Override
	public boolean buy(String sellerID) {
		if(decrementItemNum() && getItemNum() == 0) {
			resetItemNum();
			product = productList[r.nextInt(productList.length)];
			logger.log(String.format("SellerID:%s start to sell %s", id, product));
			return true;
		}
		return false;
	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
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
