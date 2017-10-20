package scout.Diagonal;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

public class EnemyMapper extends scout.sim.EnemyMapper {
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();
        for(int x=1;x<=n && locations.size() < num;++x) locations.add(new Point(x,x));
        for(int x=1;x<=n && locations.size() < num;++x) locations.add(new Point(x,n+1-x));

        while(locations.size() < num) {
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }
        return locations;
    }
}
