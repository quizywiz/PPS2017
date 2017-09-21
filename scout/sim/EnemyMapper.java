package scout.sim;

import java.util.List;
import java.util.Random;

abstract public class EnemyMapper {
    abstract public List<Point> getLocations(int n, int num, Random gen);
}
