package roles;

public class NoRole extends Person {
	public NoRole(String type, String id, String product, String[] neighbors, int count, String output) {
		super(type, id, product, neighbors, count, output);
	}
	
	
	@Override
	public void logStatus() {
		logger.log(String.format("PeerID:%s with no role start to work", id));
	}
	
	@Override
	public boolean buy(String sellerID) {
		return false;
	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
		return false;
	}

	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}
}
