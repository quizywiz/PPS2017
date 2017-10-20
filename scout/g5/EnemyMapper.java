package scout.g5;

import scout.sim.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

/*
    Parameters (ready to copy in the Makefile :) ):
        n=100
        s=3
        e=1000
        t=2000
        repeats=1
        p=g1
        m=sparse_landmarks
        em=g5_enemymap
        fps=1000
 */
public class EnemyMapper extends scout.sim.EnemyMapper {

    int n;
    Random gen;

    @Override
    public Set<Point> getLocations(int n, int num, List<Point> landmarkLocation, Random gen) {
        this.n = n;
        this.gen = gen;
        Set<Point> locations = new HashSet<>();

        // Add an enemy in every landmark. There should be 24 of them.
        for (Point landmark : landmarkLocation) {
            locations.add(new Point(landmark.x, landmark.y));
        }

        // Add an L-shaped cluster of enemies near the outposts, facing to the center.
        locations.add(new Point(1, 1));
        locations.add(new Point(1, n));
        locations.add(new Point(n, 1));
        locations.add(new Point(n, n));
        for (int i = 1; i < 3; ++i) {
            locations.add(new Point(1+i, 1));
            locations.add(new Point(1, 1+i));

            locations.add(new Point(1+i, n));
            locations.add(new Point(1, n-i));

            locations.add(new Point(n-i, 1));
            locations.add(new Point(n, 1+i));

            locations.add(new Point(n-i, n));
            locations.add(new Point(n, n-i));
        }

        // Add a 8x8 enemies cluster in the middle, to difficult communication there.
        int clusterSize = 8;
        createCluster(locations, clusterSize, (n/2) - (clusterSize/2), (n/2) - (clusterSize/2));

        // Add different size clusters in random positions.
        createRandomClusters(locations, 7, 6);
        createRandomClusters(locations, 6, 9);
        createRandomClusters(locations, 5, 10);

        // Add the rest of the enemies in random positions.
        while(locations.size() < num) {
            locations.add(new Point((gen.nextInt(n)) + 1, (gen.nextInt(n)) + 1) );
        }

        return locations;
    }

    private void createRandomClusters(Set<Point> locations, int clusterSize, int numClusters) {
        int topLeftX, topLeftY;
        for (int i = 0; i < numClusters; ++i) {
            topLeftX = Math.max(1, gen.nextInt(n) + 1 - clusterSize);
            topLeftY = Math.max(1, gen.nextInt(n) + 1 - clusterSize);
            createCluster(locations, clusterSize, topLeftX, topLeftY);
        }
    }

    private void createCluster(Set<Point> locations, int clusterSize, int topLeftX, int topLeftY) {
        for (int i = 0; i < clusterSize; ++i) for (int j = 0; j < clusterSize; ++j) {
            locations.add(new Point(topLeftX+i, topLeftY+j));
        }
    }
}
