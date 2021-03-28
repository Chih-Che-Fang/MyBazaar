package roles;

/**
 * Peer with no role, it implement buy/sell/lookup and inherit all attributes from person class.
 */
public class NoRole extends Person {

	/**
	 * A No-Role peer's constructor and initialization
	 * @param type  Buyer(b) or Seller(s) or NoRole(na).
	 * @param id id of a NoRole.
	 * @param product product string buy or sell (no meaning for NoRole).
	 * @param neighbors neighbors array to save all the neighbors close to person.
	 * @param count count of items (not used in NoRole).
	 * @param output output place for logging.
	 */
	public NoRole(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}


	/**
	 * log status to testing output file  
	 */
	@Override
	public void logStatus() {
		logger.log(String.format("PeerID:%s with no role start to work", id));
	}

	/**
	 * Implement buy interface for the no-role peer
	 * @param sellerID for NoRole.
	 * @return always false since NoRole can not buy.
	 */
	@Override
	public boolean buy(String sellerID) {
		return false;
	}

	/**
	 * Implement reply interface for the no-role peer
	 * @param buyerID for NoRole.
	 * @param sellerID always false since NoRole can not reply to buyer.
	 * @return
	 */
	@Override
	public boolean reply(String buyerID, String sellerID) {
		return false;
	}

	/**
	 * Implement lookup interface for the no-role peer
	 * @param product for NoRole.
	 * @param hopCount for NoRole.
	 * @return always false since NoRole not actively lookup.
	 */
	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}
}
