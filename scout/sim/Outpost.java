package scout.sim;

import java.util.ArrayList;
import java.util.List;

public class Outpost extends CellObject {
    private final Point location;
    private Object data;
    private final List<List<Integer>> enemyMap;

    public Outpost(int id, int n, int x, int y) {
        super("O" + id);
        enemyMap = new ArrayList<>();
        for(int i = 0; i < n+2; ++i) {
            List<Integer> row = new ArrayList<>();;
            for(int j = 0 ; j < n + 2; ++ j) {
                row.add(0);
            }
            enemyMap.add(row);
        }
        location = new Point(x,y);
    }
    public void setData(Object ob) {
        data = ob;
    }
    public Point getLocation() {
        return location;
    }

    public Object getData() {
        return data;
    }

    public void addEnemyLocation(Point p) {
        //System.out.println(p.x + " " + p.y + " has enemy set!");
        enemyMap.get(p.x).set(p.y, 1);
    }

    public void addSafeLocation(Point p) {
        //System.out.println(p.x + " " + p.y + " has safe set!");
        enemyMap.get(p.x).set(p.y, 2);
    }

    public List<List<Integer>> getEnemyMap() {
        return enemyMap;
    }
}
