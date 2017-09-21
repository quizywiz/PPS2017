package scout.sim;

import java.util.Set;
import java.util.Random;

abstract public class EnemyMapper {
    abstract public Set<Point> getLocations(int n, int num, Random gen);
}
