package roles;

import utils.Client;

/**
 * Buyer Class extends Person Parent Class
 */
public class Buyer extends Person {

	/**
	 * Buyer Constructor
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
	 * logStatus to printout information
	 */
	@Override
	public void logStatus() {
		logger.log(String.format("BuyerID:%s start to buy %s", id, product));
	}

	/**
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
	 * @param buyerID
	 * @param sellerID
	 * @return buyer ID match the current Id respond to this reply msg.
	 */
	@Override
	public boolean reply(String buyerID, String sellerID) {
		return buyerID.equals(this.id)? true : false;
	}

	/**
	 * @param product
	 * @param hopCount
	 * @return false since Buyer doesn't respond to lookUp.
	 */
	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}

	/**
	 * method stub
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
