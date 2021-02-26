package roles;

import utils.Client;

public class Buyer extends Person {

	public Buyer(String type, String id, String product, String[] neighbors, int count) {
		super(type, id, product, neighbors, count);
	}
	
	
	@Override
	public void buy(String sellerID) {

	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
		if(buyerID.equals(this.id)) {
			System.out.println(String.format("BuyerID:%s bought %s from %s", id, product, sellerID));
			product = productList[r.nextInt(productList.length)];
			System.out.println(String.format("BuyerID:%s start to buy %s", id, product));
			return true;
		}
		return false;
	}

	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
