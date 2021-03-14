package roles;

/**
 * A seller peer, it implement buy/sell/lookup and inherit all attributes from person class.  
 */
public class Seller extends Person {

	static final int m = 1;

	/**
	 * @param type  Buyer(b) or Seller(s) or NoRole(na).
	 * @param id id of a seller.
	 * @param product product string buy or sell (salt, boar, fish).
	 * @param neighbors neighbors array to save all the neighbors close to person.
	 * @param count count of items seller hold.
	 * @param output output place for logging.
	 */
	public Seller(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}

	/**
	 * logStatus for logging.
	 */
	@Override
	public void logStatus() {
		logger.log(String.format("SellerID:%s start to sell %s", id, product));
	}

	/**
	 * @param sellerID
	 * @return
	 */
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

	/**
	 * @param buyerID buyerID seller reply to.
	 * @param sellerID SellerId
	 * @return always false since seller do not response to another seller's reply msg.
	 */
	@Override
	public boolean reply(String buyerID, String sellerID) {
		return false;
	}

	/**
	 * @param product seller check if product match.
	 * @param hopCount hopCount
	 * @return return true if product match, false if not.
	 */
	@Override
	public boolean lookUp(String product, int hopCount) {
		return product.equals(this.product);
	}
}
