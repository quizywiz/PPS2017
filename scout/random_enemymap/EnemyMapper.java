package scout.random_enemymap;

import scout.sim.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyMapper extends scout.sim.EnemyMapper {
    @Override
    public List<Point> getLocations(int n, int num, Random gen) {
        List<Point> locations = new ArrayList<>();
        for(int i = 0 ; i < num ; ++ i) {
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }
        return locations;
    }
}
