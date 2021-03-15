package roles;

import utils.Client;

/**
 * A buyer peer, it implement buy/sell/lookup and inherit all attributes from person class.  
 */
public class Buyer extends Person {

	/**
	 * Buyer constructor and initialization
	 * @param type  Buyer(b) or Seller(s) or NoRole(na).
	 * @param id id of a buyer.
	 * @param product product string buy or sell (salt, boar, fish).
	 * @param neighbors neighbors array to save all the neighbors close to person.
	 * @param count count of items seller hold.
	 * @param output output place for logging.
	 */
	public Buyer(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}

	/**
	 * log status to testing output file  
	 */
	@Override
	public void logStatus() {
		logger.log(String.format("BuyerID:%s start to buy %s", id, product));
	}

	/**
	 * Implement buy interface for the buyer
	 * @param sellerID seller ID that buyer check.
	 * @return return false since buyer just relay buy msg.
	 */
	@Override
	public boolean buy(String sellerID) {
		logger.log(String.format("BuyerID:%s bought %s from %s", id, product, sellerID));
		product = productList[r.nextInt(productList.length)];
		logger.log(String.format("BuyerID:%s start to buy %s", id, product));
		return false;
	}

	/**
	 * Implement reply interface for the buyer
	 * @param buyerID
	 * @param sellerID
	 * @return buyer ID match the current Id respond to this reply msg.
	 */
	@Override
	public boolean reply(String buyerID, String sellerID) {
		return buyerID.equals(this.id)? true : false;
	}

	/**
	 * Implement lookup interface for the buyer
	 * @param product
	 * @param hopCount
	 * @return false since Buyer doesn't respond to lookUp.
	 */
	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}
}
