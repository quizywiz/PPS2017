package scout.g2;

import scout.sim.*;
import scout.g2.*;
import java.util.*;

//Read scout.sim.Player for more information!
public class Player extends scout.sim.Player {
    public PlayerHelper ph;
    private int t,n,s,id;
    private int currentTerritoryIndex = 0;
    private List<Point> landmarkLocations;
    LinkedList<String> cycle;
    String defaultDirection = "";
    int start_X_for_less_then_3_case;
    int start_Y_for_less_then_3_case;
    private List<Point> territory;
    boolean goAroundCircle = false;
    boolean stage1 = true;
    boolean stage2 = false;
    boolean stage3 = false;
    boolean stage4 = false;
    boolean stage5 = false;

    // for stage1 turning
    int diagonalTurn = 0;
    List<String> helperIds;
    boolean foundLandmarks = false;
    // boolean foundPlayer = false;

    // for stage4
    int wait = 0;

    /**
    * better to use init instead of constructor, don't modify ID or simulator will error
    */
    public Player(int id) {
        super(id);
        this.id=id;
    }

    private boolean atTRBoundary(int x, int y) {
        return x==0 || y==n+1;
    }

    private boolean atLBBoundary(int x, int y) {
        return x==n+1 || y==0;
    }

    private boolean atOutpost(int x, int y) {
        return  (x==0 && y==n+1) || (x==n+1 && y==0) || (x==n+1 && y==n+1);
    }

    private void printSnake(List<Point> snake){
        if(true) return;
        for (Point p: snake) {
            System.out.println("("+p.x + "," +p.y + ")");
        }
    }

    private List<Point> snake() {
        int n = this.n;
        List<Point> snakeList = new ArrayList<Point>();
        int currentX = 0;
        int currentY = 0;
        int counter = 0;
        boolean phase1 = true;
        boolean rotated = false;
        snakeList.add(new Point(currentX, currentY));
        while (phase1) {
            counter = 0;
            while (!atOutpost(currentX, currentY) && counter < 5) {
                currentX += 1;
                counter += 1;
                snakeList.add(new Point(currentX, currentY));
            }
            if (!atOutpost(currentX, currentY)) {
                while(!atTRBoundary(currentX, currentY)) {
                    currentX -= 1;
                    currentY += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                counter = 0;
                while (!atOutpost(currentX, currentY) && counter < 5) {
                    currentY += 1;
                    counter += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                if (!atOutpost(currentX, currentY)) {
                    while(!atLBBoundary(currentX, currentY)) {
                        currentY -= 1;
                        currentX += 1;
                        snakeList.add(new Point(currentX, currentY));
                    }
                }
                else {
                    // you are at the upper right outpost
                    phase1 = false;
                    rotated = true;

                }
            }
            else {
                // you are at the bottom left outpost
                phase1 = false;
                rotated = false;
            }
        }

        while(!phase1 && !rotated) {
            // you are at the bottom left outpost
            while(!atTRBoundary(currentX, currentY)) {
                currentX -= 1;
                currentY += 1;
                snakeList.add(new Point(currentX, currentY));
            }
            counter = 0;
            while (!(currentX==n+1 && currentY==n+1) && counter < 5) {
                currentX += 1;
                counter += 1;
                snakeList.add(new Point(currentX, currentY));
            }
            if (!(currentX==n+1 && currentY==n+1)) {
                while(!atLBBoundary(currentX, currentY)) {
                    currentY -= 1;
                    currentX += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                counter = 0;
                while (!(currentX==n+1 && currentY==n+1) && counter < 5) {
                    currentY += 1;
                    counter += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                if (atOutpost(currentX, currentY)) {
                    return snakeList;
                }
            }
            else {
                return snakeList;
            }
        }

        while(!phase1 && rotated) {
            // you are at the top right outpost
            while(!atLBBoundary(currentX, currentY)) {
                currentX += 1;
                currentY -= 1;
                snakeList.add(new Point(currentX, currentY));
            }
            counter = 0;
            while (!(currentX==n+1 && currentY==n+1) && counter < 5) {
                currentY += 1;
                counter += 1;
                snakeList.add(new Point(currentX, currentY));
            }
            if (!(currentX==n+1 && currentY==n+1)) {
                while(!atTRBoundary(currentX, currentY)) {
                    currentX -= 1;
                    currentY += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                counter = 0;
                while (!(currentX==n+1 && currentY==n+1) && counter < 5) {
                    currentX += 1;
                    counter += 1;
                    snakeList.add(new Point(currentX, currentY));
                }
                if ((currentX==n+1 && currentY==n+1)) {
                    return snakeList;
                }
            }
            else {
                return snakeList;
            }
        }
        return snakeList;
    }

    private int getMoveTime(List<Point> allMoves) {
        int totalTime = 0;
        for (int i=1; i<allMoves.size(); i++) {
            if (allMoves.get(i).x == allMoves.get(i-1).x || allMoves.get(i).y == allMoves.get(i-1).y) {
                totalTime += 2.0;
            }
            else {
                totalTime += 3.0;
            }
        }
        return totalTime;
    }
    private List<Point> splitSnake(List<Point> snake, int id) {
        double totalTime = 0;
        for (int i=1; i<snake.size(); i++) {
            if (snake.get(i).x == snake.get(i-1).x || snake.get(i).y == snake.get(i-1).y) {
                totalTime += 2.0;
            }
            else {
                totalTime += 3.0;
            }
        }
        double eachTime = totalTime/this.s;
        int yourPortionStart = (int)(eachTime * id);
        int yourPortionEnd = (int)Math.min(yourPortionStart+eachTime, totalTime);
        int startIndex = 0;
        int endIndex = 0;
        int totalTimeSoFar = 0;
        for (int i=1; i<snake.size(); i++) {
            if (snake.get(i).x == snake.get(i-1).x || snake.get(i).y == snake.get(i-1).y) {
                totalTimeSoFar += 2;
            }
            else {
                totalTimeSoFar += 3;
            }
            if (totalTimeSoFar >= yourPortionStart) {
                startIndex = i-1;
                break;
            }
        }
        totalTimeSoFar = 0;
        for (int i=1; i<snake.size(); i++) {
            if (snake.get(i).x == snake.get(i-1).x || snake.get(i).y == snake.get(i-1).y) {
                totalTimeSoFar += 2;
            }
            else {
                totalTimeSoFar += 3;
            }
            if (totalTimeSoFar >= yourPortionEnd) {
                endIndex = i;
                break;
            }
        }
        // System.out.println(eachTime);
        // System.out.println(endIndex-startIndex);
        return snake.subList(startIndex, endIndex+1);
    }
    /**
    *   Called at the start
    */
    @Override
    public void init(String id, int s, int n, int t, List<Point> landmarkLocations) {
        this.t = t;
        this.n = n;
        this.landmarkLocations = landmarkLocations;
        this.ph = new PlayerHelper(n);
        this.s = s;
        this.helperIds = new ArrayList<String>();
        //this.stage0 = false;

        // change for each player
        this.cycle = new LinkedList<String>();
        this.territory = splitSnake(snake(), this.id);
        Point closestOutpost = findClosestOutpost();
        if (closestOutpost.x == 0 && closestOutpost.y == 0) {
            this.defaultDirection = "NW";
        } else if (closestOutpost.x == n+1 && closestOutpost.y == 0) {
            this.defaultDirection = "SW";
        } else if (closestOutpost.x == 0 && closestOutpost.y == n+1) {
            this.defaultDirection = "NE";
        } else {
            this.defaultDirection = "SE";
        }
        // System.out.println("Player id: " +this.id);
        // System.out.println("Player id: " +this.id + " is going direction " + this.defaultDirection);
        // printSnake(this.territory);
    }

    private double timeNeeded(int x1, int y1, int x2, int y2) {
        int numDiag = Math.min(Math.abs(x2-x1), Math.abs(y2-y1));
        int numManhattan = Math.max(Math.abs(x2-x1), Math.abs(y2-y1)) - numDiag;
        double T = 0;
        T += 3*numDiag;
        T += 2*numManhattan;
        return T;
    }

    private Point findClosestOutpost(){
        int x1 = this.territory.get(0).x;
        int y1 = this.territory.get(0).y;
        int x2 = this.territory.get(this.territory.size()-1).x;
        int y2 = this.territory.get(this.territory.size()-1).y;
        double outpost1Cost = Math.min(timeNeeded(x1,y1,0,0)+timeNeeded(this.n/2,this.n/2,x2,y2),
                                       timeNeeded(x2,y2,0,0)+timeNeeded(this.n/2,this.n/2,x1,y1)
                                    );
        double outpost2Cost = Math.min(timeNeeded(x1,y1,0,n+1)+timeNeeded(this.n/2,this.n/2,x2,y2),
                                       timeNeeded(x2,y2,0,n+1)+timeNeeded(this.n/2,this.n/2,x1,y1)
                                    );
        double outpost3Cost = Math.min(timeNeeded(x1,y1,n+1,0)+timeNeeded(this.n/2,this.n/2,x2,y2),
                                       timeNeeded(x2,y2,n+1,0)+timeNeeded(this.n/2,this.n/2,x1,y1)
                                    );
        double outpost4Cost = Math.min(timeNeeded(x1,y1,n+1,n+1)+timeNeeded(this.n/2,this.n/2,x2,y2),
                                       timeNeeded(x2,y2,n+1,n+1)+timeNeeded(this.n/2,this.n/2,x1,y1)
                                    );
        double minVal = Math.min(Math.min(Math.min(outpost1Cost, outpost2Cost), outpost3Cost), outpost4Cost);
        if (outpost1Cost == minVal) {
            return new Point(0,0);
        } else if (outpost2Cost == minVal) {
            return new Point(0,n+1);
        } else if (outpost3Cost == minVal) {
            return new Point(n+1,0);
        } else {
            return new Point(n+1,n+1);
        }
    }

    private Point findClosestOutpostToCurrent() {
        int x = this.ph.getAbsoluteX();
        int y = this.ph.getAbsoluteY();
        if (x <= n/2 && y<= n/2) {
            return new Point(0,0);
        } else if (x > n/2 && y > n/2) {
            return new Point(n+1,n+1);
        } else if (x > n/2 && y <= n/2) {
            return new Point(n+1,0);
        } else {
            return new Point(0, n+1);
        }
    }


    private Point findStartPoint() {
        // only call this function when you are at outpost
        int x1 = this.territory.get(0).x;
        int y1 = this.territory.get(0).y;
        int x2 = this.territory.get(this.territory.size()-1).x;
        int y2 = this.territory.get(this.territory.size()-1).y;
        int val1 = getMoveTime(shortestPathToPoint(new Point(this.ph.getAbsoluteX(), this.ph.getAbsoluteY()), new Point(x1,y1))) +
            getMoveTime(shortestPathToPoint(new Point(x2,y2), new Point(this.n/2,this.n/2)));
        int val2 = getMoveTime(shortestPathToPoint(new Point(this.ph.getAbsoluteX(), this.ph.getAbsoluteY()), new Point(x2,y2))) +
            getMoveTime(shortestPathToPoint(new Point(x1,y1), new Point(this.n/2,this.n/2)));
        if (val1 < val2) {
            return new Point(x1,y1);
        }
        else {
            Collections.reverse(this.territory); //reversed because we actually started from the end
            return new Point(x2,y2);
        }
    }
    /**
     * nearby IDs is a 3 x 3 grid of nearby IDs with you in the center (1,1) position. A position is null if it is off the board.
     * Enemy IDs start with 'E', Player start with 'P', Outpost with 'O' and landmark with 'L'.
     *
     */


    @Override
    public Point move(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        this.ph.updatePlayerHelper(nearbyIds, concurrentObjects);
        String moveDirection = "C";
        List<String> moves = this.ph.getAllValidMoves(nearbyIds);

        // if(this.ph.isCalibrated()){
        //      List<Point>  tooutpost = shortestPathToPoint(new Point(0,0));
        //      System.out.println(tooutpost.size());
        //      for (Point curr: tooutpost){
        //         System.out.println(curr.x + ", "+curr.y);
        //      }
        //  }

        // Do your work here
        //If defaultDirection is available, move to default direction.
        if (this.stage1 == true) {
            if(moves.contains(this.defaultDirection)){
                moveDirection = this.defaultDirection;
            }
            else{//If we hit the wall, need to change direction
                if(this.defaultDirection == "NW"){
                    moveDirection = toOutpost(moves,"NW", "N","W");
                }
                else if(this.defaultDirection == "NE"){
                    moveDirection = toOutpost(moves,"NE", "N","E");
                }
                else if(this.defaultDirection == "SE"){
                    moveDirection = toOutpost(moves,"SE", "S","E");
                }
                else if(this.defaultDirection == "SW"){
                    moveDirection = toOutpost(moves,"SW", "S","W");
                }
            }

            //if there is a lankmark around, go to landmarks instead.
            String previous = moveDirection;
            if (this.ph.getNearbyLandmarks(nearbyIds).size() > 0 && this.ph.isCalibrated() == false){
                // System.out.println("found landmarks!" + this.id);
                for(List<String> direction : this.ph.getNearbyLandmarks(nearbyIds)){
                    moveDirection = direction.get(1);
                    if(moveDirection != "C" && !helperIds.contains(moveDirection)){
                        this.foundLandmarks = true;
                        helperIds.add(direction.get(0));
                        break;
                    }
                    else if (moveDirection == "C"){

                        this.foundLandmarks = true;
                        this.stage1 = false;
                        this.stage2 = true;
                        break;
                    }
                }

            }
            if(this.foundLandmarks == false) {
                moveDirection = previous;
            }
            // if(this.foundLandmarks == false && this.ph.getNearbyPlayers(nearbyIds).size() > 0){
            //     for(List<String> direction : this.ph.getNearbyLandmarks(nearbyIds)){
            //         moveDirection = direction.get(1);
            //         if(moveDirection != "C" && !helperIds.contains(moveDirection)){
            //             this.foundPlayer = true;
            //             helperIds.add(direction.get(0));
            //             break;
            //         }
            //     }
            //     if(this.foundPlayer == false) {
            //         moveDirection = previous;
            //     }
            // }

            if (moveDirection == "C" || this.ph.isCalibrated()) {
                // we are at outpost
                this.stage1 = false;
                this.stage2 = true;
            }
        }

        if (this.stage2) {//Stage 2: once calibrated, reach to the closest start of of the path and scan the territory.
            //if we reach to the startpoint of the player's path, start stage 3
            if (this.ph.getAbsoluteX() == findStartPoint().x && (this.ph.getAbsoluteY() == findStartPoint().y)) {
                this.stage2 = false;
                this.stage3 = true;
                currentTerritoryIndex = 1;
                // moveDirection = "C";
            }
            else {
                // if(this.n >= 50){//User Dijkstras algorithm to figure out the next step
                Point p = shortestPathToPoint(findStartPoint().x, findStartPoint().y).get(0);
                moveDirection = this.ph.moveToNextPoint(
                                new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                // }
                // else{//Go to the start point directly
                //     Point p = new Point(Integer.signum(findStartPoint().x-this.ph.getAbsoluteX()) , Integer.signum(findStartPoint().y - this.ph.getAbsoluteY()));
                //     System.out.println(p.x + ", "+p.y);
                //     moveDirection = this.ph.moveToNextPoint(p);

                // }
            }
        }

        if (this.stage3) {//Stage 3: start following the path of the snake
            // Point p2;
            // if (this.id % 4 == 0) {
            //         p2 = new Point(0,0);
            //     } else if (this.id % 4 == 1) {
            //         p2 = new Point(0,n+1);

            //     } else if (this.id % 4 == 2) {
            //         p2 = new Point(n+1,0);

            //     } else {
            //         p2 = new Point(n+1,n+1);

            //     }

            if (currentTerritoryIndex == this.territory.size()) {
                // we have finished
                this.stage3 = false;
                this.stage4 = true;
            } else if (this.s >1 && (getMoveTime(shortestPathToPoint(new Point(this.ph.getAbsoluteX(),this.ph.getAbsoluteY()), new Point(this.n/2,this.n/2)))
                + 3*this.n >= this.t)) {
                    this.stage3 = false;
                    this.stage4 = true;
                } else if (this.s == 1 && getMoveTime(shortestPathToPoint(new Point(this.ph.getAbsoluteX(),this.ph.getAbsoluteY()), findClosestOutpostToCurrent())) >= this.t){
                    this.stage3 = false;
                    this.stage4 = true;
                } else {
                Point p = this.territory.get(currentTerritoryIndex);
                moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                currentTerritoryIndex+=1;
            }
        }

        if (this.stage4 && this.s > 1) {
            // System.out.println("nearby players" + this.ph.getNearbyPlayers(nearbyIds).size() + " " + this.s);
            if (this.ph.getAbsoluteX() == this.n/2 && this.ph.getAbsoluteY() == this.n/2) {
                // System.out.println("nearby players" + this.ph.getNearbyPlayers(nearbyIds).size() + " " + this.s);
                // can't just wait for everyone forever

                if(this.ph.getNumOfConcurrentPlayersIncludingYou(concurrentObjects) == this.s || 3*this.n >= this.t){
                        this.stage4 = false;
                        this.stage5 = true;
                        return this.ph.move(moveDirection);
                }
                else{
                    moveDirection = "C";
                }
            }
            else {
                Point p = shortestPathToPoint(this.n/2, this.n/2).get(0);
                moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
            }
        }

        if (this.stage4 && this.s == 1) {
            if (this.ph.hitOutpost()) {
                this.stage4 = false;
                this.stage5 = true;
                goAroundCircle = true;
            }
            else {
                Point p = shortestPathToPoint(findClosestOutpostToCurrent().x,findClosestOutpostToCurrent().y).get(0);
                moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
            }
        }

        if (this.stage5 && this.s >= 3){
            if (this.ph.hitOutpost()) {
                goAroundCircle = true;
            }
            else {
                
                if (this.id % 4 == 0) {
                
                    Point p = shortestPathToPoint(0,0).get(0);
                    moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                } else if (this.id % 4 == 1) {
                
                    Point p = shortestPathToPoint(0,n+1).get(0);
                    moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                } else if (this.id % 4 == 2) {
                
                    Point p = shortestPathToPoint(n+1,0).get(0);
                    moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                } else {
                    Point p = shortestPathToPoint(n+1,n+1).get(0);
                    moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
                }
            }

        }

        if (this.stage5 && goAroundCircle){
            if (this.ph.hitOutpost()){
                start_X_for_less_then_3_case = this.ph.getAbsoluteX();
                start_Y_for_less_then_3_case = this.ph.getAbsoluteY();
            }
            if (start_X_for_less_then_3_case == 0 && start_Y_for_less_then_3_case == 0) {
                moveDirection = "S";
            }
            if (start_X_for_less_then_3_case == n+1 && start_Y_for_less_then_3_case == 0) {
                moveDirection = "E";
            }
            if (start_X_for_less_then_3_case == n+1 && start_Y_for_less_then_3_case == n+1) {
                moveDirection = "N";
            }
            if (start_X_for_less_then_3_case == 0 && start_Y_for_less_then_3_case == n+1) {
                moveDirection = "W";
            }
        }
        // else{
        //     moveDirection = "C";
        // }

        //If the estimation is close, we go back to the outpost
        // if(2*this.n >= this.t && !stage5){
        //     String direction = "";
        //     if(this.ph.getAbsoluteX() <= n/2+1 && this.ph.getAbsoluteY() <= n/2+1){
        //          //moveDirection = hitBoundary(moves,"NW", "N","W");
        //         Point p = shortestPathToPoint(0,0).get(0);
        //         moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
        //     }
        //     else if(this.ph.getAbsoluteX() > n/2+1 && this.ph.getAbsoluteY() <= n/2+1){
        //         //moveDirection = hitBoundary(moves,"SW", "S","W");
        //         Point p = shortestPathToPoint(n+1,0).get(0);
        //         moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
        //     }
        //     else if(this.ph.getAbsoluteX() <= n/2+1 && this.ph.getAbsoluteY() > n/2+1){
        //         //moveDirection = hitBoundary(moves,"NE", "N","E");
        //         Point p = shortestPathToPoint(0,n+1).get(0);
        //         moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
        //     }
        //      else {
        //         //moveDirection = hitBoundary(moves,"SE", "S","E");
        //          Point p = shortestPathToPoint(n+1,n+1).get(0);
        //          moveDirection = this.ph.moveToNextPoint(new Point(p.x-this.ph.getAbsoluteX(), p.y-this.ph.getAbsoluteY()));
        //     }
        //     if(this.ph.hitOutpost()){
        //         moveDirection = "C";
        //     }
        // }

        // if (this.ph.hitOutpost()){
        //     System.out.println("Yay!! " + this.id);
        // }
        // if(this.ph.hitOutpost()){
        //     this.ph.grid.printGrid();
        // }
        // make sure that you make your final move through PlayerHelper!
        //this.ph.grid.printGrid();
        // System.out.println(moveDirection + "goes by" + this.id);
        return this.ph.move(moveDirection);
    }


    public String toOutpost(List<String> moves, String initial, String e1, String e2){
        if(moves.contains(initial)){
            return initial;
        } else if (moves.contains(e1)){
            return e1;
        } else if (moves.contains(e2)){
            return e2;
        }
        return "C";
    }


    public String hitBoundary(List<String> moves, String a1, String a2, String a3){
         if(moves.contains(a1)){
            this.defaultDirection = a1;
            return a1;
         }
         else if(moves.contains(a2)){
             this.defaultDirection = a2;
             return a2;
         }
         else{
            return a3;
        }
    }


    public int computeDistance(){
        int dist_x = Math.min(this.ph.getAbsoluteX(), n+1-this.ph.getAbsoluteX());
        int dist_y = Math.min(this.ph.getAbsoluteY(), n+1-this.ph.getAbsoluteY());
        int firstDirection = Math.min(dist_x,dist_y);
        int secondDirection = Math.max(dist_x,dist_y);
        //System.out.println("Compute distance: " + dist_x + " " + dist_y + " " + firstDirection + " " + secondDirection);
        return firstDirection+secondDirection*6;
        //return 0;
    }


    public void stub() {
        ;
    }

    @Override
    public void communicate(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        --t;
    }

    @Override
    public void moveFinished() {
        this.ph.updateCurrentLocation();
    }

    public List<Point> shortestPathToPoint(int x, int y){
        return shortestPathToPoint(new Point(this.ph.getAbsoluteX(), this.ph.getAbsoluteY()),
                                new Point(x,y));
    }

    public List<Point> shortestPathToPoint(Point start, Point destination){
        // Handles the case when n is greater than 50
        // if (this.n >= 50) {
        //     int startX = start.x;
        //     int startY = start.y;
        //     int destinationX = destination.x;
        //     int destinationY = destination.y;
        //     List<Point> result = new ArrayList();
        //     while ((startX < destinationX) && (startY < destinationY)) {
        //         result.add(new Point(startX+1, startY+1));
        //         startX += 1;
        //         startY += 1;
        //     }
        //     while ((startY > destinationY) && (startX < destinationX)) {
        //         result.add(new Point(startX+1, startY-1));
        //         startX += 1;
        //         startY -= 1;
        //     }
        //     while ((startX > destinationX) && (startY > destinationY)) {
        //         result.add(new Point(startX-1, startY-1));
        //         startX -= 1;
        //         startY -= 1;
        //     }
        //     while ((startX > destinationX) && (startY < destinationY)) {
        //         result.add(new Point(startX-1, startY+1));
        //         startX -= 1;
        //         startY += 1;
        //     }
        //     while (startX < destinationX) {
        //         result.add(new Point(startX+1, startY));
        //         startX += 1;
        //     }
        //     while (startY < destinationY) {
        //         result.add(new Point(startX, startY+1));
        //         startY += 1;
        //     }
        //     while (startX > destinationX) {
        //         result.add(new Point(startX-1, startY));
        //         startX -= 1;
        //     }
        //     while (startY > destinationY) {
        //         result.add(new Point(startX, startY-1));
        //         startY -= 1;
        //     }
        //     result.add(new Point(destinationX, destinationY));
        //     System.out.println("Player ID: " + this.id);
        //     System.out.println("Destination X" + destinationX);
        //     System.out.println("Destination Y" + destinationY);
        //     System.out.println(result.get(0).x);
        //     System.out.println(result.get(0).y);
        //
        //     return result;
        // }

        int[][] grid = this.ph.grid.getGrid();
        int x = start.x;
        int y = start.y;
        int dx = destination.x;
        int dy = destination.y;

        if(this.ph.isCalibrated()){
            ArrayList<ArrayList<Point>> dP = new ArrayList<ArrayList<Point>>();
            for (int i = Math.min(x, dx); i<=Math.max(x, dx) ; i++){
                ArrayList<Point> row = new ArrayList<Point>();
                for (int j = Math.min(y, dy); j <= Math.max(y, dy) ; j++){
                    row.add(new Point(i,j));
                }
                dP.add(row);
            }

            Graph g = new Graph();
            //this looks at all the values in the grid
            for (int i = 0; i < dP.size(); i++){
                for (int j = 0; j < dP.get(0).size(); j++){
                    //creates nerby list along with weights
                    List<Vertex> nearbyVertex = new ArrayList<Vertex>();
                    int shiftx = Math.min(x, dx) + i;
                    int shifty = Math.min(y, dy) + j;

                    for(int p = -1 ; p < 2; p++) {
                        for(int q = -1 ; q < 2; q++){
                            //orginal position
                            if(p == 0 && q == 0) continue;
                            if((shiftx + p < 0) || (shiftx + p > n+1) || (shifty + q < 0) || (shifty + q > n+1)) continue;
                            if(( i == 0 && p == -1 ) || ( i == dP.size()-1 && p == 1) ||
                               ( j == 0 && q == -1 ) || ( j == dP.get(0).size()-1 && q == 1)) {
                                 continue;
                            }

                            //8 neighbors case, worse case unknown
                            int direction = 2;
                            int type = 2;

                            //diagonal
                            if(Math.abs(p)+Math.abs(q) == 2){
                                direction = 3;
                            }

                            if(grid[shiftx + p][shifty + q] == 1){
                                type = 3;
                            }

                            nearbyVertex.add(new Vertex(dP.get(i+p).get(j+q),direction*type));
                        }
                    }
                    // g.printGraph();

                    g.addVertex(dP.get(i).get(j), nearbyVertex);
                }
            }
            List<Point> result =  g.getShortestPath(dP.get(x-Math.min(x, dx)).get(y-Math.min(y, dy)),
                dP.get(dx-Math.min(x, dx)).get(dy-Math.min(y, dy)));
            // List<Point> result =  g.getShortestPath(dP.get(start.x).get(start.y),
            //     dP.get(destination.x).get(destination.y));
            Collections.reverse(result);
            return result;
        }
        return null;
    }

}
