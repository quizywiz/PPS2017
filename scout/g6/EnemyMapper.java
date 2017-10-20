package scout.g6;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
// Spiral with a box outside
// Run it for -m sparse_landmarks -em random_enemymap -n 100 -e 800 -s 15 -t 1000
public class EnemyMapper extends scout.sim.EnemyMapper {
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();
        double theta = 0;
        for(int i=0;i<Math.min(num,n);i++){
            locations.add( new Point(n,i+1));
            locations.add(new Point(i+1,n));
        }
        for(int i=0;i<Math.min(num,n);i++){
            locations.add(new Point(1,i+1));
            locations.add(new Point(i+1,1));
        }

        for(int i=0;i<n/4;i++) {
            if(locations.size()==num){
                break;
            }
            for(int k=0;k<36;k++){
                if(locations.size()==num){
                    break;
                }
                int x = n / 2 + (i % 2 == 1 ? (int) Math.ceil((2*i+1) * Math.cos(theta)) : -1 * (int) Math.floor((2*i+1) * Math.cos(theta)));
                int y = n / 2 + (i % 2 == 1 ? (int) Math.ceil((2*i+1) * Math.sin(theta)) : -1 * (int) Math.floor((2*i+1) * Math.sin(theta)));
                theta+=Math.toRadians(7);
                locations.add(new Point(x,y));
            }
        }

        //System.out.println(locations.size());
        while(locations.size()<num){
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }
        //System.out.println(locations.size());
        return locations;
    }
}
