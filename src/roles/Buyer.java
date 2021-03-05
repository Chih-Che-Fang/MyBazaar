package roles;

import utils.Client;

public class Buyer extends Person {

	public Buyer(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}
	
	
	@Override
	public boolean buy(String sellerID) {
		logger.log(String.format("BuyerID:%s bought %s from %s", id, product, sellerID));
		product = productList[r.nextInt(productList.length)];
		logger.log(String.format("BuyerID:%s start to buy %s", id, product));
		return false;
	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
		return buyerID.equals(this.id)? true : false;
	}

	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
