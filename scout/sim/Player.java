package scout.sim;

import java.util.ArrayList;
import java.util.List;

abstract public class Player extends CellObject {
	public Player(int id) {
		super("P" + id);
	}

	//called on at the start
	public abstract void init(String id, int s, int n, int t, List<Point> landmarkLocations);

	//called on turns when you can move
	public abstract Point move(
			//3 x 3 grid of list of neighbouring IDs
			ArrayList<ArrayList<ArrayList<String>>> nearbyIds,
			//objects on your location
			List<CellObject> concurrentObjects);

	//called every turn
	public abstract void communicate(
			ArrayList<ArrayList<ArrayList<String>>> nearbyIds,
			List<CellObject> concurrentObjects
	);

}