package scout.g6;

import scout.sim.*;

import java.util.*;


//Read scout.sim.Player for more information!
public class Player extends scout.sim.Player {
    List<Point> enemyLocations;
    List<Point> safeLocations;
    Random gen;
    int t,n,s;
    int x = -1;
    int y = -1;
    int dx = 0, dy = 0;
    int seed;
    boolean OP_NOT_FOUND = true;
    int playerId;
    boolean goRight = true;
    boolean goDown = true;
    boolean at_OP = true;
    int temp_sum = 0;
    int x_adjust = 1;
    int y_adjust = 1;
    boolean oriented = false;
    List<Point> snake;
    int snake_num_players = 0;
    int startLocation = 0;
    int endLocation = 0;
    int t_original;
    int t_converge;
    HashMap<Player,Integer> do_not_communicate;
    /**
     * better to use init instead of constructor, don't modify ID or simulator will error
     */
    public Player(int id) {
        super(id);
        seed=id;
        playerId = id;
    }

    /**
     *   Called at the start
     */
    @Override
    public void init(String id, int s, int n, int t, List<Point> landmarkLocations) {
        enemyLocations = new ArrayList<>();
        safeLocations = new ArrayList<>();
        do_not_communicate = new HashMap<>();
        gen = new Random(seed);
        this.t = t;
        this.t_original = t;
        this.n = n;
        this.s = s;
        snake = new ArrayList<>();
        initSnake();
        //printSnake();
        getPath();
        initConverge();

    }

    /**
     * nearby IDs is a 3 x 3 grid of nearby IDs with you in the center (1,1) position. A position is null if it is off the board.
     * Enemy IDs start with 'E', Player start with 'P', Outpost with 'O' and landmark with 'L'.
     *
     */
    @Override
    public Point move(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        for(int i = 0 ; i < 3; ++ i) {
            for(int j = 0 ; j < 3 ; ++ j) {
                boolean safe = true;
                if(nearbyIds.get(i).get(j) == null) continue;
                for(String ID : nearbyIds.get(i).get(j)) {
                    if(ID.charAt(0) == 'E') {
                        safe = false;
                    }
                }
                //if (true){
                if(true) {
                    Point consideredLocation = new Point(x + i - 1, y + j - 1);
                    if(safe) {
                        if(!safeLocations.contains(consideredLocation)) {
                            safeLocations.add(consideredLocation);
                        }
                    } else {
                        if(!enemyLocations.contains(consideredLocation)) {
                            enemyLocations.add(consideredLocation);
                        }
                    }
                }
            }
        }
        for(CellObject obj : concurrentObjects) {
            if (obj instanceof Player) {
                ((Player) obj).stub();
            } else if (obj instanceof Enemy) {

            } else if (obj instanceof Landmark) {
                if (!oriented){
                    x = ((Landmark) obj).getLocation().x;
                    y = ((Landmark) obj).getLocation().y;  
                    oriented = true;
                    adjustPoints();
                }
                
            } else if (obj instanceof Outpost) {
                if (!oriented){
                    x = ((Outpost) obj).getLocation().x;
                    y = ((Outpost) obj).getLocation().y;
                    oriented = true;
                    adjustPoints();
                }
                Object data = ((Outpost) obj).getData();
                if(data == null) {
                    ((Outpost) obj).setData((Object)"yay!!");
                }
                for(Point safe : safeLocations) {
                    ((Outpost) obj).addSafeLocation(safe);
                }
                for(Point unsafe : enemyLocations) {
                    ((Outpost) obj).addEnemyLocation(unsafe);
                }
                
            }
        }



        return getPoint(nearbyIds, concurrentObjects);
    }

    public void stub() {
        ;
    }

    public Point getPoint(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects){
        if (!oriented){
            if (playerId%4 == 0){
                if (nearbyIds.get(0).get(0) != null){ //northwest
                    x -= 1; y -= 1;
                    x_adjust += 1; y_adjust += 1;
                    return new Point(-1, -1);
                }
                if (nearbyIds.get(1).get(0) != null){ //west
                    y -= 1;
                    y_adjust += 1;
                    return new Point(0, -1);
                }
                if (nearbyIds.get(0).get(1) != null){ //north
                    x -= 1;
                    x_adjust += 1;
                    return new Point(-1, 0);
                }
                return new Point(0, 0);
            }

            else if (playerId%4 == 1){
                if (nearbyIds.get(0).get(2) != null){ //northeast
                    x -= 1; y += 1;
                    x_adjust += 1; y_adjust -= 1;
                    return new Point(-1, +1);
                }
                if (nearbyIds.get(1).get(2) != null){ //east
                    y += 1;
                    y_adjust -= 1;
                    return new Point(0, +1);
                }
                if (nearbyIds.get(0).get(1) != null){ //north
                    x -= 1;
                    x_adjust += 1;
                    return new Point(-1, 0);
                }
                return new Point(0, 0);
            }

            else if (playerId%4 == 2){
                if (nearbyIds.get(2).get(0) != null){ //southwest
                    x += 1; y -= 1;
                    x_adjust -= 1; y_adjust += 1;
                    return new Point(+1, -1);
                }
                if (nearbyIds.get(1).get(0) != null){ //west
                    y -= 1;
                    y_adjust += 1;
                    return new Point(0, -1);
                }
                if (nearbyIds.get(2).get(1) != null){ //south
                    x += 1;
                    x_adjust -= 1;
                    return new Point(+1, 0);
                }
                return new Point(0, 0);
            }

            else if (playerId%4 == 3){
                if (nearbyIds.get(2).get(2) != null){ //southeast
                    x += 1; y += 1;
                    x_adjust -= 1; y_adjust -= 1;
                    return new Point(+1, +1);
                }
                if (nearbyIds.get(1).get(2) != null){ //east
                    y += 1;
                    y_adjust -= 1;
                    return new Point(0, +1);
                }
                if (nearbyIds.get(2).get(1) != null){ //south
                    x += 1;
                    x_adjust -= 1;
                    return new Point(+1, 0);
                }
                return new Point(0, 0);
            }
            
            else
                return new Point(0,0);

        }
        else{
            Point pathPoint = followPath(nearbyIds, concurrentObjects);
            x += pathPoint.x;
            y += pathPoint.y;
            //System.out.println(pathPoint.x + ", " + pathPoint.y + " id" + playerId);
            return pathPoint;
        }
    }


    public void printPoints(){
        if(true) return;
        System.out.println("safe:\n");
        for (Point safe : safeLocations) {
            System.out.println(safe.x + ", " + safe.y);
        }  
        System.out.println("unsafe:\n");
        for (Point unsafe : enemyLocations){
            System.out.println(unsafe.x + ", " + unsafe.y);
        }
    }

    public void adjustPoints(){
        for (Point safe : safeLocations) {
            safe.x += x_adjust+x;
            safe.y += y_adjust+y;
        }  
        for (Point unsafe : enemyLocations){
            unsafe.x += x_adjust+x;
            unsafe.y += y_adjust+y;
        }
    }

    public void initSnake(){
        int endColumn = this.n-2;
        int endRow = this.n + 1;
        int column = 2;
        int row = 2;
        boolean snake_right = true;
        boolean snake_down = false;
        snake.add(new Point(row, column));

        while(row <= endRow){
            if ((row+3 > endRow || row==this.n-1) && snake_down){
                break;
            }

            if (column<endColumn  && snake_right){
                for (int i=0; i<this.n-4; i++){
                    snake.add(new Point(row, ++column));
                } 
                snake_down = true;
            }
            else if (row+3 <= this.n+1 && snake_down){
                if (snake_right){
                    ++column;
                    snake_right = false;
                }
                else{
                    --column;
                    snake_right = true;
                }
                for (int i=0; i<4; i++){
                    snake.add(new Point(row++, column));    
                }
                row--;
                snake_down = false;
            }
            else if (column > 2 && !snake_right){
                for (int i=0; i<this.n-4; i++){
                    snake.add(new Point(row, --column));    
                }
                snake_down = true;
            }

        }
        if(snake_right)
            ++column;
        else
            --column;
        snake.add(new Point(row, column));
        if (this.s/2 < 5)
            snake_num_players = Math.min(this.s, 5);
        else
            snake_num_players = (int) Math.ceil(this.s/2.0);

    }

    public void printSnake(){
        // System.out.println("snake: ");
        // for (Point pt : snake){
        //     System.out.println(pt.x + ", " + pt.y);
        // }
    }
    @Override
    public void communicate(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        --t;
        for (CellObject obj : concurrentObjects) {
            if (obj instanceof Player) {
                Player other = (Player) obj;
                if (do_not_communicate.get(other) == null){
                    do_not_communicate.put(other, 1);    
                }
                else if(do_not_communicate.get(other) >0){
                    do_not_communicate.put(other, do_not_communicate.get(other)+1);
                    continue;
                }
                else
                    do_not_communicate.put(other, 1);

                if(other.playerId==this.playerId)
                    continue;
                
                for (Point p : other.safeLocations) {
                    if(oriented && other.oriented && !safeLocations.contains(p))
                        this.safeLocations.add(p);

                }
                for (Point p : other.enemyLocations) {
                    if(oriented && other.oriented && !enemyLocations.contains(p))
                        this.enemyLocations.add(p);
                }

            }
        }

    }

    public void getPath(){
        int horizontal_chunk = snake.size()/snake_num_players;
        int vertical_chunk = this.s < 5 ? snake.size()/(5 - this.s) : snake.size()/this.s - (snake_num_players);
        if (playerId<snake_num_players){
            startLocation = playerId*horizontal_chunk;
            endLocation = startLocation + horizontal_chunk;
            if (playerId == snake_num_players-1){
                endLocation = snake.size()-1;
            }    
        }
        else{
            startLocation = (playerId-snake_num_players+1)*vertical_chunk;
            endLocation = (startLocation) + vertical_chunk;
            if (playerId == this.s-1){
                endLocation = snake.size()-1;
            }    
        }
        
        
    }

    public Point followPath(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects){
        Point moveHere = new Point(0,0);
        if (startLocation < endLocation && this.t>this.t_converge && playerId<snake_num_players){
            if(this.x == snake.get(startLocation).x && this.y == snake.get(startLocation).y)
                startLocation++;

            moveHere = moveTo(snake.get(startLocation).x, snake.get(startLocation).y);
            return moveHere;  
        }
        else if(startLocation < endLocation && this.t>this.t_converge && playerId>=snake_num_players){
            if(this.x == snake.get(startLocation).y && this.y == snake.get(startLocation).x)
                startLocation++;

            moveHere = moveTo(snake.get(startLocation).y, snake.get(startLocation).x);
            return moveHere;    
        }
        else if (this.t > this.n/2*5.0){
            if (playerId<4)
                communicate(nearbyIds, concurrentObjects);
            
            return moveTo((int) this.n/2, (int) this.n/2);
        }
        else{
            if (playerId%4 == 0){
                return moveTo(0, 0);    
            }
            else if (playerId%4 == 1){
                return moveTo(this.n+1, 0);
            }

            else if (playerId%4 == 2){
                return moveTo(0, this.n+1);
            }

            else if (playerId%4 == 3){
                return moveTo(this.n+1, this.n+1);
            }    
        }
        

        return new Point(0,0);
    }

    public Point moveTo(int x, int y){
        int x_move = 0;
        int y_move = 0;
        if (this.x != x || this.y != y){
            if (this.x < x)
                x_move = 1;
            else if (this.x > x)
                x_move = -1;
            else
                x_move = 0;

            if (this.y < y)
                y_move = 1;
            else if (this.y > y)
                y_move = -1;
            else
                y_move = 0;
        }
        return new Point(x_move, y_move);

    }

    public Point retreatNearestOutpost(){
        return new Point(0,0);
    }

    public void initConverge(){
        t_converge = (int) Math.ceil(this.n/2.0*5.0 + this.n*2);
        //System.out.println(t_converge);
    }

    @Override
    public void moveFinished() {
        x += dx;
        y += dy;
        dx = dy = 0;
    }

}
