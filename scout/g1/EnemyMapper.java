package scout.g1;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.lang.Math;

/*
 * Expected parameters: t=500, n=100, s=11, e=100, landmarks=average
 */

public class EnemyMapper extends scout.sim.EnemyMapper {
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();

        int perRow = (int) Math.ceil(Math.sqrt(num));
        int space = n/perRow;
        for(int x = space/2; x < n; x += space) {
            for(int y = space/2; y < n; y += space) {
                if(num-- > 0) {
                    locations.add(new Point(x, y));
                }
            }
        }
       
        return locations;
    }
}
