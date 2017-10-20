package scout.Structures;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

public class EnemyMapper extends scout.sim.EnemyMapper {
	@Override
	public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
		Set<Point> locations = new HashSet<>();
		
		while(locations.size() < num-10) {
			int x = gen.nextInt(n-2) + 2;
			int y = gen.nextInt(n-2) + 2;
			locations.add(new Point(x-1,y-1));
			locations.add(new Point(x-1,y));
			locations.add(new Point(x-1,y+1));
			locations.add(new Point(x,y-1));
			boolean abc = gen.nextInt(2)==1;
			if(abc) 
				locations.add(new Point(x,y));
			locations.add(new Point(x,y+1));
			locations.add(new Point(x+1,y-1));
			locations.add(new Point(x+1,y));
			locations.add(new Point(x+1,y+1));
			locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
		}
		while(locations.size() < num) {
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }
		return locations;
	}
}
