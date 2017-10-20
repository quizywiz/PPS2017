package scout.Quadmap;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

public class EnemyMapper extends scout.sim.EnemyMapper {
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();
        while(locations.size() < num) {
            locations.add(new Point((gen.nextInt(n/2)) + 1, (gen.nextInt(n/2)) + 1) );
        }
        return locations;
    }
}
