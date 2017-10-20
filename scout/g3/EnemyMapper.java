package scout.g3;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

public class EnemyMapper extends scout.sim.EnemyMapper {
    // Scatter enemies randomly on diagonals in both directions.
    // Use with n=100, s=15, num=1200, aditional enemies will be randomly distributed.
    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        Set<Point> locations = new HashSet<>();
        int initX = n-2;
        int initY = 2;

        while(initX > 1 || initY < n-1) {
            if(initY > n-2) {
                break;
            }
        	int x = initX;
        	int y = initY;
    		while(initX > 1 && x < n-1 && initY < n-1 && y < n-1) {
        		int nextPos = gen.nextInt(4);
	        	if(nextPos == 1) {
	        		locations.add(new Point(x, y));
	        	} else if(nextPos == 2) {
	        		locations.add(new Point(x+1, y-1));
	        	} else if(nextPos == 3) {
	        		locations.add(new Point(x-1, y+1));
	        	}
	        	x++;
	        	y++;
                if(locations.size() == num) {
                    return locations;
                }
        	}

        	if(initX > n/10+1) {
        		initX -= n/10;
        	} else {
        		initY += n/10;
        	}
        }


        initX = n-2;
        initY = n-2;
         while(initX > 1 || initY > 1) {
            if(initY < 2) {
                break;
            }
        	int x = initX;
        	int y = initY;
    		while(initX > 1 && x < n-1 && initY > 2 && y > 2) {
        		int nextPos = gen.nextInt(4);
	        	if(nextPos == 1) {
	        		locations.add(new Point(x, y));
	        	} else if(nextPos == 2) {
	        		locations.add(new Point(x-1, y-1));
	        	} else if(nextPos == 3) {
	        		locations.add(new Point(x+1, y+1));
	        	}
	        	x++;
	        	y--;
                if(locations.size() == num) {
                    return locations;
                }
        	}

        	if(initX > n/10+1) {
        		initX -= n/10;
        	} else {
        		initY -= n/10;
        	}
        }

        // If locations still left, add random points.
        while(locations.size() < num) {
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }
        return locations;
    }
}
