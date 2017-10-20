package scout.g2;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class EnemyMapper extends scout.sim.EnemyMapper {
    
    // number of enemies: 70, n=50, total time = 400
    
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();
        List<Point> lis = new ArrayList<Point>();

        int Xc = n-5;
        int Yc = n/2;
        
        lis.add(new Point(Xc, Yc));
        for (int i = 0; i < 3; i++) {
            Yc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 3; i++) {
            Xc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 3; i++) {
            Yc--;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 3; i++) {
            Xc--;
            lis.add(new Point(Xc, Yc));
        }

        Xc = n-6;
        Yc = n/2 - 1;
        lis.add(new Point(Xc, Yc));
        for (int i = 0; i < 5; i++) {
            Yc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 5; i++) {
            Xc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 5; i++) {
            Yc--;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 5; i++) {
            Xc--;
            lis.add(new Point(Xc, Yc));
        }
        Xc = n-7;
        Yc = n/2 - 2;
        lis.add(new Point(Xc, Yc));
        for (int i = 0; i < 7; i++) {
            Yc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 7; i++) {
            Xc++;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 7; i++) {
            Yc--;
            lis.add(new Point(Xc, Yc));
        }
        for (int i = 0; i < 7; i++) {
            Xc--;
            lis.add(new Point(Xc, Yc));
        }


        for(int i = 1; i < lis.size() ;i++){
        	if(locations.size() < num){
            locations.add(lis.get(i));
        	}
        }

        while (locations.size() < num) {
            locations.add(new Point(gen.nextInt(n)+1, gen.nextInt(n)+1));
        }
        return locations;
    }
}