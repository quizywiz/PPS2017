package scout.g1;

import scout.sim.*;
import java.io.Serializable;
import java.util.*;
import java.lang.Math;

public class Player extends scout.sim.Player implements Serializable {
    List<Point> enemyLocations;
    List<Point> safeLocations;
    Random gen;
    int t,n,s,seed;
    int x = -1;
    int y = -1;
    int dx = 0, dy = 0;
    int startX = 0, startY = 0; // Position relative to start
    
    int orientX, orientY; // Denotes corner scout should move to during orientation
    int orientTime; // Deadline for scout to orient
    
    int segmentX, segmentY; // Denotes starting point if scouts are segmented
    boolean containedToSegment;
    
    int meetingX, meetingY; // Location for meeting
    int meetingTime; // Time to meet
    boolean goToMeeting; // Denotes if player is heading to meeting

    int[][] notVisited; // Value in each cell is number of adjacent cells that have not been visited
    int[][] notVisitedUnknownLocation; // Value for each cell if x is not oriented

    int[][] pointMap; // Integer map: 0 means not visited, 1 means enemy, 2 means safe 
    int[][] pointMapUnknownLocation;

    List<Point> unknownEnemyLocations;
    List<Point> unknownSafeLocations;
    
    int playerSeen = s; // Last scout communicated with
    int playerSeenEnemySize = 0; // Size of enemy list after last communication

    int topBorder, bottomBorder, rightBorder, leftBorder; // Borders of map

    public Player(int id) {
        super(id);
        seed=id;
    }

    @Override
    public void init(String id, int s, int n, int t, List<Point> landmarkLocations) {
        enemyLocations = new ArrayList<>();
        safeLocations = new ArrayList<>();
        gen = new Random(seed);
        this.t = t;
        this.n = n;
        this.s = s;
      
        this.segmentX = n+2;
        this.segmentY = n+2;
        
        this.meetingX = n/2 + 1;
        this.meetingY = n/2 + 1;
        this.goToMeeting = false;

        this.orientX = gen.nextInt(2)==0 ? 1 : -1;
        this.orientY = gen.nextInt(2)==0 ? 1 : -1;
        
        if (s>=8 && seed<4) {
           this.meetingTime = 3*n;
           this.orientTime = 6*n;
        } else {
            this.meetingTime = 7*n/4;
            this.orientTime = 5*n/2;
        }

        this.notVisited = new int[n+2][n+2];
        this.notVisitedUnknownLocation = new int[2*n+2][2*n+2];

        this.pointMap = new int[n+2][n+2];
        this.pointMapUnknownLocation = new int[2*n+2][2*n+2];

        for(int i=1; i<n+1; i++) {
            for(int j=1; j<n+1; j++) {
                notVisited[i][j] = 8;
                if(i==1 || i==n) {
                    notVisited[i][j] -= 3;
                    if(j==1 || j==n) {
                        notVisited[i][j] -= 2;
                    }
                } else if(j==1 || j==n) {
                    notVisited[i][j] -=3;
                }
            }
        }

        // Initializing count for unoriented scout
        for(int i=1; i<2*n+1; i++) {
            for(int j=1; j<2*n+1; j++) {
                notVisitedUnknownLocation[i][j] = 8;
                if(i==1 || i==n) {
                    notVisitedUnknownLocation[i][j] -= 3;
                    if(j==1 || j==n) {
                        notVisitedUnknownLocation[i][j] -= 2;
                    }
                } else if(j==1 || j==n) {
                    notVisitedUnknownLocation[i][j] -= 3;
                }
            }
        }

        this.topBorder = 0;
        this.bottomBorder = n+1;
        this.rightBorder = n+1;
        this.leftBorder = 0;

        /* If the parameters of a game are below a threshold (explained in detail in the report),
         * the scouts will begin the game in isolated segments
         */
        int rows = (int) Math.floor(Math.sqrt(s));
        if((double) n*n/t/s < 1.1 && seed<rows*rows) {
            containedToSegment = true;
            segmentX = (seed%rows)*(n/rows)+n/(2*rows);
            segmentY = (seed/rows)*(n/rows)+n/(2*rows);
        }
        
        unknownEnemyLocations = new ArrayList<>();
        unknownSafeLocations = new ArrayList<>();
    }

    @Override
    public Point move(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        trackLocations(nearbyIds);
       
        // Checks if scout should start heading to meeting
        int currentDistance = (Math.abs(x-meetingX)+Math.abs(y-meetingY));
        int leaveTime = 0;
        if(currentDistance > 50) {
            leaveTime = (11*currentDistance)/4;
        } else if(currentDistance <= 50) {
            leaveTime = (10*currentDistance)/3;
        } else if(segmentX != n+2) {
            leaveTime = (11*currentDistance)/3;
        }
        
        if(t-meetingTime < leaveTime) {
            goToMeeting = true;
            
            // Adjust back to original borders
            topBorder = 0;
            bottomBorder = n+1;
            rightBorder = n+1;
            leftBorder = 0;
        }

        for(CellObject obj : concurrentObjects) {
            if (obj instanceof Landmark) {
                x = ((Landmark) obj).getLocation().x;
                y = ((Landmark) obj).getLocation().y;
                locationFound();
            } else if (obj instanceof Outpost) {
                x = ((Outpost) obj).getLocation().x;
                y = ((Outpost) obj).getLocation().y;
                locationFound();

                /*
                 * Leave information at outpost telling future scouts
                 * which direction you will be heading.
                 */
                @SuppressWarnings("unchecked")
                List<Point> data = (List<Point>) ((Outpost) obj).getData();
                if(data == null) {
                    data = new ArrayList<>();
                } else {
                    for(Point pt : data) {
                        for(int i=-1;i<=1;i++) {
                            for(int j=-1;j<=1;j++) {
                                updateNotVisited(pt.x+i,pt.y+j);
                            }
                        }
                    }
                }
                for(Point pt : getNextMoves(nearbyIds)) {
                    data.add(pt);
                }
                ((Outpost) obj).setData((Object) data);

                // Update scout map with outpost information
                List<List<Integer>> enemyMap = ((Outpost) obj).getEnemyMap();
                for(int i = 0; i < n+2; i++) {
                    for(int j = 0; j < n+2; j++) {
                        Integer enemyStatus = enemyMap.get(i).get(j);
                        Point consideredLocation = new Point(i, j);

                        if(enemyStatus == 1 && pointMap[consideredLocation.x][consideredLocation.y] == 0) {
                            updateNotVisited(consideredLocation.x, consideredLocation.y);
                            notVisited[consideredLocation.x][consideredLocation.y] = 0;
                            enemyLocations.add(consideredLocation);
                            pointMap[consideredLocation.x][consideredLocation.y] = 1;
                        } else if(enemyStatus == 2 && pointMap[consideredLocation.x][consideredLocation.y] == 0) {
                            updateNotVisited(consideredLocation.x, consideredLocation.y);
                            safeLocations.add(consideredLocation);
                            pointMap[consideredLocation.x][consideredLocation.y] = 2;
                        }
                    }
                }

                for(Point safe : safeLocations) {
                    ((Outpost) obj).addSafeLocation(safe);
                }
                for(Point unsafe : enemyLocations) {
                    ((Outpost) obj).addEnemyLocation(unsafe);
                }
            }
        }
       
        /*
         * If a scout or landmark is adjacent, the scout will make
         * an effort to communicate next move. If your seed is higher than the seed
         * of the adjacent scout, remain at your current spot.
         */ 
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                if(x==-1 && itemAtPoint(i,j,nearbyIds,'L')) {
                    dx = i-1;
                    dy = j-1;
                    return new Point(dx, dy);
                } else if(itemAtPoint(i,j,nearbyIds,'S')) {
                    ArrayList<String> ids = nearbyIds.get(i).get(j);
                    if(ids != null) {
                        for(String id : ids) {
                            if(id.charAt(0) == 'S' && playerSeen!=Integer.parseInt(id.substring(1))) {
                                playerSeen = Integer.parseInt(id.substring(1));
                                if(seed < Integer.parseInt(id.substring(1))) {
                                    dx = i-1;
                                    dy = j-1;
                                } else {
                                    dx = 0;
                                    dy = 0;
                                }
                                return new Point(dx,dy);
                            }
                        }
                    }
                }
            }
        }
        if(x != -1 && containedToSegment==true) { // Move to segment position
            return moveToPoint(segmentX, segmentY, nearbyIds);
        }
        
        if (x != -1 && goToMeeting) { // Continue Heading to meeting
            return moveToPoint(meetingX, meetingY, nearbyIds);
        } else if (x != -1) { // Oriented Movement
            Point pt = moveToLeastVisited(x, y, nearbyIds, 1);
            dx = pt.x;
            dy = pt.y;
            return pt;
        } else if (t > orientTime) { // Unoriented movement
            Point pt = moveToLeastVisited(startX, startY, nearbyIds, 1);
            dx = pt.x;
            dy = pt.y;
            return pt;
        }

        return moveToOutpost(nearbyIds);
    }

    @Override
    public void communicate(ArrayList<ArrayList<ArrayList<String>>> nearbyIds, List<CellObject> concurrentObjects) {
        --t;
        int idRank = -1; //Used to determine which outpost to go to after meetingTime
        
        if(x==segmentX && y==segmentY) {
            containedToSegment=false;

            int rows = (int) Math.floor(Math.sqrt(s));
            topBorder = segmentX - n/(2*rows);
            bottomBorder = segmentX + n/(2*rows);
            leftBorder = segmentY - n/(2*rows);
            rightBorder = segmentY + n/(2*rows);
            if(seed%rows==rows-1) bottomBorder=n;
            if(seed>=rows*(rows-1)) rightBorder=n;
            
            segmentX = n+2;
            segmentY = n+2;
        }

        for(CellObject obj : concurrentObjects) {
            if (obj instanceof Player) {
                Player concurrentPlayer = (Player) obj;
                if (concurrentPlayer.x != -1) {
                    x = concurrentPlayer.x;
                    y = concurrentPlayer.y;
                    locationFound();
                }

                for(Point safe : concurrentPlayer.safeLocations) {
                    if(pointMap[safe.x][safe.y] == 0) {
                        updateNotVisited(safe.x, safe.y);
                        safeLocations.add(safe);
                        pointMap[safe.x][safe.y] = 2;
                    }
                }

                for(Point unsafe : concurrentPlayer.enemyLocations) {
                    if(pointMap[unsafe.x][unsafe.y] == 0) {
                        updateNotVisited(unsafe.x, unsafe.y);
                        notVisited[unsafe.x][unsafe.y] = 0;
                        enemyLocations.add(unsafe);
                        pointMap[unsafe.x][unsafe.y] = 1;
                    }
                }
               
                /*
                 * If you are the higher seed, obtain the next moves from the seen scout.
                 * The higher seed scout will mark these moves as seen, in order to avoid 
                 * duplicate movements.
                 */ 
                if(idRank==-1) idRank++;
                if(concurrentPlayer.seed < seed && x!=-1) {
                    if(goToMeeting==false) {
                        for(Point pt : getNextMoves(nearbyIds)) {
                            for(int i=-1;i<=1;i++) {
                                for(int j=-1;j<=1;j++) {
                                    updateNotVisited(pt.x+i,pt.y+j);
                                }
                            }
                        }
                    }
                    idRank++;
                }
                playerSeenEnemySize = enemyLocations.size();
            }
        }

        /*
         * When a scout is not oriented and hits and edge, it will adjust its map
         * to avoid attempting to move off the map.
         */
        if(x == -1) {
            if(nearbyIds.get(0).get(1)==null) {
                for(int i=0;i<=startX;i++) {
                    notVisitedUnknownLocation[i] = new int[2*n+2];
                }
            } else if(nearbyIds.get(2).get(1)==null) {
                for(int i=startX;i<notVisitedUnknownLocation.length;i++) {
                    notVisitedUnknownLocation[i] = new int[2*n+2];
                }
            } else if(nearbyIds.get(1).get(0)==null) {
                for(int i=0;i<notVisitedUnknownLocation.length;i++) {
                    for(int j=0;j<=startY;j++) {
                        notVisitedUnknownLocation[i][j] = 0;
                    }
                }
            } else if(nearbyIds.get(1).get(2)==null) {
                for(int i=0;i<notVisitedUnknownLocation.length;i++) {
                    for(int j=startY;j<notVisitedUnknownLocation.length;j++) {
                        notVisitedUnknownLocation[i][j] = 0;
                    }
                }
            }
        }

        // Choosing outpost to move to after meetTime based on idRank
        if(t == meetingTime) {
            if(idRank%4==0) {
                meetingX = 0;
                meetingY = 0;
            } else if(idRank%3==0) {
                meetingX = 0;
                meetingY = n+1;
            } else if(idRank%2==0) {
                meetingX = n+1;
                meetingY = 0;
            } else if(idRank%1==0) {
                meetingX = n+1;
                meetingY = n+1;
            } else if(idRank == -1 && x != -1) {
                meetingX = x<n/2 ? 0 : n+1;
                meetingY = y<n/2 ? 0 : n+1;
            }
        } else if(t<meetingTime && x==meetingX && y==meetingY) {
            if(meetingX==0 && meetingY==0) {
                meetingX=n+1;
            } else if(meetingX==n+1 && meetingY==0) {
                meetingY=n+1;
            } else if(meetingX==n+1 && meetingY==n+1) {
                meetingX=0;
            } else {
                meetingY=0;
            }
        }
    }

    @Override
    public void moveFinished() {
        if(x != -1) {
            x += dx;
            y += dy;
        }

        startX += dx;
        startY += dy;
        
        dx = dy = 0;
    }

    /*
     * After each move, all data structures must be updated to keep track of what has been seen. The cells of adjacent
     * points must be updated because you see all neighboring information on every move. 
     */
    public void trackLocations(ArrayList<ArrayList<ArrayList<String>>> nearbyIds) {
        for(int i = 0 ; i < 3; ++ i) {
            for(int j = 0 ; j < 3 ; ++ j) {
                if(nearbyIds.get(i).get(j) == null) continue;
                boolean safe = itemAtPoint(i, j, nearbyIds,'E') ? false : true;
                
                if(x != -1) {
                    if(x+i-1<0 || x+i-1>n+1 || y+j-1<0 || y+j-1>n+1) continue; 
                    Point consideredLocation = new Point(x + i - 1, y + j - 1);
                    if(notVisited[x+i-1][y+j-1] > 0) {
                        updateNotVisited(x + i - 1, y + j -1);
                    }

                    if(pointMap[consideredLocation.x][consideredLocation.y] == 0) {
                        if(safe) {
                            safeLocations.add(consideredLocation);
                            pointMap[consideredLocation.x][consideredLocation.y] = 2;
                        } else {
                            notVisited[x + i -1][y + j -1] = 0;
                            enemyLocations.add(consideredLocation);
                            pointMap[consideredLocation.x][consideredLocation.y] = 1;
                        }
                    }
                } else {
                    if(startX+n+i-1<0 || startX+n+i-1>2*n+1 || startY+n+j-1<0 || startY+n+j-1>2*n+1) continue; 
                    Point consideredLocation = new Point(startX + i - 1, startY + j - 1);
                    
                    if(notVisitedUnknownLocation[startX+n+i-1][startY+n+j-1] > 0) {
                        updateNotVisited(startX + n + i - 1, startY + n + j -1);
                    }
                   
                    if(pointMapUnknownLocation[consideredLocation.x + n][consideredLocation.y + n] == 0) { 
                        if(safe) {
                            unknownSafeLocations.add(consideredLocation);
                            pointMapUnknownLocation[consideredLocation.x + n][consideredLocation.y + n] = 2; 
                        } else {
                            notVisitedUnknownLocation[startX+i+n-1][startY+j+n-1] = 0;
                            unknownEnemyLocations.add(consideredLocation);
                            pointMapUnknownLocation[consideredLocation.x + n][consideredLocation.y + n] = 1; 
                        }
                    }
                } 
            }
        }
    }

    /*
     * After becoming oriented, the scout will transfer its 'non-oriented' information to the 'oriented' data structures.
     */
    public void locationFound() {
        if (x != -1) { return; }
        int offsetX = x - startX;
        int offsetY = y - startY;

        for(int i = 0; i < unknownSafeLocations.size(); i++) {
            Point temp = unknownSafeLocations.get(i);
            Point adjustedPoint = new Point(temp.x + offsetX, temp.y + offsetY);

            if(pointMap[adjustedPoint.x][adjustedPoint.y] == 0) {
                updateNotVisited(adjustedPoint.x, adjustedPoint.y);
                safeLocations.add(adjustedPoint);
            }
        }

        for(int i = 0; i < unknownEnemyLocations.size(); i++) {
            Point temp = unknownEnemyLocations.get(i);
            Point adjustedPoint = new Point(temp.x + offsetX, temp.y + offsetY);

            if(pointMap[adjustedPoint.x][adjustedPoint.y] == 0) {
                updateNotVisited(adjustedPoint.x, adjustedPoint.y);
                notVisited[adjustedPoint.x][adjustedPoint.y] = 0;
                enemyLocations.add(adjustedPoint);
            }
        }

        unknownSafeLocations.clear();
        unknownEnemyLocations.clear();
    }

    /*
     * Move scout towards designated point.
     */
    public Point moveToPoint(int targetX, int targetY, ArrayList<ArrayList<ArrayList<String>>> nearbyIds) {
        int xMovement = 0;
        int yMovement = 0;

        if(x > targetX) {
            xMovement = -1;
        } else if(x < targetX) {
            xMovement = 1;
        }

        if(y > targetY) {
            yMovement = -1;
        } else if(y < targetY) {
            yMovement = 1;
        }

        if(itemAtPoint(1+xMovement,1+yMovement,nearbyIds,'E')) {
            if(!itemAtPoint(1,1+yMovement,nearbyIds,'E') && yMovement!=0) {
                xMovement = 0;
            } else if(!itemAtPoint(1+xMovement,1,nearbyIds,'E') && xMovement!=0) {
                yMovement=0;
            }
        }
        
        dx = xMovement;
        dy = yMovement;
        
        return new Point(xMovement, yMovement);
    }

    /*
     * Decrease count of all seen points after a movement or information update.
     */
    public void updateNotVisited(int targetX, int targetY) {
        for(int i=-1;i<=1;i++) {
            for(int j=-1;j<=1;j++) {
                if (x != -1 && targetX+i>0 && targetY+j>0 && targetX+i<n+2 && targetY+j<n+2) {
                    notVisited[targetX+i][targetY+j]--;
                } else if (x == -1 && targetX+i>0 && targetY+j>0 && targetX+i<2*n+2 && targetY+j<2*n+2) {
                    notVisitedUnknownLocation[targetX+i][targetY+j]--;
                }
            }
        }
    }

    /*
     * Before the meetingTime, choose your next movement based on points you have not seen. 
     */
    public Point moveToLeastVisited(int xPoint, int yPoint, ArrayList<ArrayList<ArrayList<String>>> nearbyIds, int distance) {
        int xMovement = 0;
        int yMovement = 0;
        int maxNotVisited = 0;
        int origDist = distance;
        if(x != -1) {
            /*
             * The scout will first attempt to make local movements. If a scout has seen all local cells, it will broaden its scope in a loop
             * until there is a cell it has not seen. 
             */
            while(distance<bottomBorder) {
                for(int i=distance;i>=distance*-1;i-=distance) {
                    for(int j=distance;j>=distance*-1;j-=distance) {
                        if(movePastBorder(xPoint, yPoint, i, j, nearbyIds)) { continue; }
                        if(maxNotVisited<notVisited[xPoint+i][yPoint+j] || (maxNotVisited==notVisited[xPoint+i][yPoint+j] && gen.nextInt(2)==0)) {
                            xMovement = i/distance;
                            yMovement = j/distance;
                            maxNotVisited = notVisited[xPoint+i][yPoint+j];
                        }
                    }
                }
                
                if(maxNotVisited >0) { break; }
                distance++;
            }
        } else {
            for(int i=distance;i>=distance*-1;i-=distance) {
                for(int j=distance;j>=distance*-1;j-=distance) {
                    if(movePastBorder(x, y, i, j, nearbyIds)) { 
                        orientTime = t;
                        continue; 
                    }
                    
                    if(maxNotVisited<notVisitedUnknownLocation[xPoint+i+n][yPoint+j+n] || 
                        (maxNotVisited==notVisitedUnknownLocation[xPoint+i+n][yPoint+j+n] && gen.nextInt(2)==0)) {
                        xMovement = i/distance;
                        yMovement = j/distance;
                        maxNotVisited = notVisitedUnknownLocation[xPoint+i+n][yPoint+j+n];
                    }
                }
            }
        }
        
        // Choose a new point to prevent an attempt to move past the border.
        if(maxNotVisited <= 0) {
            boolean pastBorder = true;
            while(pastBorder || (xMovement==0 && yMovement==0)) {
                pastBorder = true;
                xMovement = gen.nextInt(3)-1;
                yMovement = gen.nextInt(3)-1;
                if(!movePastBorder(x, y, xMovement, yMovement, nearbyIds)) {
                    pastBorder = false;
                }
            }
        }
        
        return new Point(xMovement, yMovement);
    }

    // Checks if a scout attempts to move past the border
    public boolean movePastBorder(int xPoint, int yPoint, int xMotion, int yMotion, ArrayList<ArrayList<ArrayList<String>>> nearbyIds) {
        if((xMotion==-1 && nearbyIds.get(0).get(1)==null) ||
           (xMotion==1 && nearbyIds.get(2).get(1)==null) ||
           (yMotion==-1 && nearbyIds.get(1).get(0)==null) ||
           (yMotion==1 && nearbyIds.get(1).get(2)==null)) {
                return true;
        }
       
        if(xPoint!=-1 && (xPoint+xMotion>bottomBorder || xPoint+xMotion<topBorder || yPoint+yMotion>rightBorder|| yPoint+yMotion<leftBorder)) {
            return true;
        }

        return false;
    }

   // Moves a scout towards the designated outpost in an attempt to orient
   public Point moveToOutpost(ArrayList<ArrayList<ArrayList<String>>> nearbyIds) {
       int xMovement = 0;
       int yMovement = 0;
       
       if(!movePastBorder(x, y, orientX, orientY, nearbyIds)) {
           xMovement = orientX;
           yMovement = orientY;
       } else if(!movePastBorder(x, y, orientX, 0, nearbyIds)) {
           xMovement = orientX;
           yMovement = 0;
       } else if(!movePastBorder(x, y, 0, orientY, nearbyIds)) {
           xMovement = 0;
           yMovement = orientY;
       }

       dx = xMovement;
       dy = yMovement;
       return new Point(xMovement,yMovement);
   }

   /*
    * Predict next moves based on current notVisited map
    */
   public List<Point> getNextMoves(ArrayList<ArrayList<ArrayList<String>>> nearbyIds) {
        int xPoint = x;
        int yPoint = y;
        int distance = 1;
        List<Point> nextMoves = new ArrayList<>();

        for(int i=1;i<=32;i++) {
            Point pt = moveToLeastVisited(xPoint, yPoint, nearbyIds, distance);
            if(xPoint+pt.x>0 && xPoint+pt.x<n+2) {
                xPoint += pt.x;
            }
            if(yPoint+pt.y>0 & yPoint+pt.y<n+2) {
                yPoint += pt.y;
            }
            nextMoves.add(new Point(xPoint, yPoint));
            distance++;
        }
        return nextMoves;
   }

   /*
    * Check if an item type exists at a given point.
    */
   public boolean itemAtPoint(int xTarget, int yTarget, ArrayList<ArrayList<ArrayList<String>>> nearbyIds, char id) {
       ArrayList<String> ids = nearbyIds.get(xTarget).get(yTarget);
       if(ids==null) return false;
       for(String ID : ids) {
           if(ID.charAt(0) == id) {
               return true;
           }
       }
       return false;
   }
}
