import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

/** 
 *  John Lynch 
 *  CAP 4630
 *  Glinos
 *  Progam 1
 */

public class PacSimRNNA implements PacAction {
   
   private List<Point> path; // path to next node
   private int simTime;
   private ArrayList<RNNAPath> rnnaPaths; // Ordered list of nodes generated by the RNNA algorithm.

	public PacSimRNNA (String fname) {
		PacSim sim = new PacSim(fname);
		sim.init(this);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("\nTSP using Repititive Neareste Neighbor Algorithm by John Lynch:      ");
		System.out.println("\nMaze : " + args[0] + "\n");
		new PacSimRNNA( args[0] );
	}
	
	@Override
	public void init() {
		simTime = 0;
		path = new ArrayList<Point>();
	}
	
	@Override
	public PacFace action(Object state) {
		PacCell[][] grid = (PacCell[][]) state;
		PacmanCell pc = PacUtils.findPacman ( grid );
      List<Point> food = PacUtils.findFood(grid);

      if (rnnaPaths == null || rnnaPaths.isEmpty()){
         System.out.println("RNNA Path is empty");

         //Generate Cost table
         List<Point> dest = new ArrayList<Point>(food); //list of needed destinations.
         dest.add(0,pc.getLoc());
         int [][] costTable = new int [dest.size()][dest.size()];
         for (int i = 0; i < costTable.length; i++){
            for (int o = 0; o < costTable[i].length; o++){
               costTable[i][o] = BFSPath.getPath( grid, dest.get(i), dest.get(o) ).size();
            }
         }

         //Print our cost table
         System.out.println("Cost Table :");
         for (int i = 0; i < costTable.length; i++){
            for(int o = 0; o < costTable[i].length; o++){
               System.out.printf("%3d", costTable[i][o] );
            }
            System.out.println();
         }
         System.out.println();

         System.out.println("Food Array :");
         for (int i = 0; i < food.size(); i++){
            System.out.printf("%d : (%d,%d)\n", i, food.get(i).x, food.get(i).y);
         }
         System.out.println();

         // Begin developing our RNNA path for the first step
         // use List food to create a first step
         rnnaPaths = new ArrayList<RNNAPath>();
         for (int i = 1; i < dest.size(); i++) {
            rnnaPaths.add(new RNNAPath (dest.get(i), costTable, 0, i, dest.size()) );
         }
            
         int step = 1;
         printPopulation(step, rnnaPaths);

         while ( step < PacUtils.numFood(grid) ) { // perform population steps until we've done enough steps for each food pellet.
         // Note to self: make sure you mark used pellets.
            //TODO: iterate through steps of creating an rnna path
            for (int i = 0; i < rnnaPaths.size(); i++) {
               List<Integer> nearestFoods = rnnaPaths.get(i).findNextNearestPellets();


               System.out.println("Next points : " + nearestFoods.size());

               
               if ( nearestFoods.size() > 1 ) { // Here we check if we have more than 1 path; if so iterate a copy for each possible path.
                  for (int j = 1; j < nearestFoods.size(); j++){ //start at 1 since we're going to do the first path anyway.
                     RNNAPath tempCopy = rnnaPaths.get(i);
		                // TODO: Create a shallow copy of the current rnna path instead of this.
                     
                     // tempCopy.addPointToPath(dest.get(nearestFoods.get(j)), nearestFoods.get(j));
                     System.out.println(tempCopy);
                     // rnnaPaths.add(0, tempCopy);
                  }
               } else if ( nearestFoods.size() == 0 ) { // path already completed, no need to push further
                  continue; 
               }
               rnnaPaths.get(i).addPointToPath(dest.get(nearestFoods.get(0)), nearestFoods.get(0));
               System.out.println(rnnaPaths.get(i));

            }
            step++;
            printPopulation(step, rnnaPaths);
         }
         printPopulation(step, rnnaPaths);


      }
      
      //make sure pacman is in the game.
      if ( pc == null ) return null;


      if ( path.isEmpty() ) {
         Point tgt = PacUtils.nearestFood ( pc.getLoc(), grid);
         path = BFSPath.getPath(grid, pc.getLoc(), tgt);
         
        /* System.out.println("Pac-Man is currently at : [ " + pc.getLoc().x + 
               " , " + pc.getLoc().y + " ] ");
         System.out.println("Setting new target : " + tgt.x + " , " + tgt.y + " ]");*/
      }
      
      // move the pacman.

      Point next = path.remove(0);
		PacFace face = PacUtils.direction( pc.getLoc(), next);
      // System.out.printf("%5d : From [%2d, %2d] go %s%n,", 
      //       ++simTime, pc.getLoc().x, pc.getLoc().y, face);
      return face;
      
   }

   public void printPopulation (int step, List<RNNAPath> rp){
      List<RNNAPath> copy = rp;
      Collections.sort(copy);
      System.out.printf("Population at step: %d\n", step);
      for (int i = 0; i < copy.size(); i++){
         System.out.printf( "%d : %s\n", i, copy.get(i).toString() );
      }
      return;
   }

}

class RNNAPath implements Comparable<RNNAPath> {
   private List<Integer> costBreakup;   // only used for toString, just used to show the costs between each pellet 
   private int[][] costTable;
   private int lastIndex;
   private List<Point> path;            // ordered list of food pellets
   private int totalCost = 0;           // total cost of the current path
   private boolean[] usedIndex;

   public RNNAPath (Point pt, int[][] costTable, int ind, int j, int size) {
      this.path = new ArrayList<Point>(); 
      this.costTable = costTable;
      this.costBreakup = new ArrayList<Integer>(); 
      this.usedIndex = new boolean[size];
      this.usedIndex[ind] = true; //Obviously set our start point as visited in terms of cost table analysis
      this.lastIndex = ind;
      addPointToPath(pt, j );
   }

   // function to add path and it's cost to the current class
   public void addPointToPath (Point a, int newIndex){
      this.path.add(a);
      System.out.printf("\n( %d , %d )\n", lastIndex, newIndex);
      this.costBreakup.add(costTable[this.lastIndex][newIndex]);
      // this.costBreakup.add(costTable[newIndex][this.lastIndex]);
      this.totalCost += costTable[this.lastIndex][newIndex];
      // this.totalCost += costTable[newIndex][this.lastIndex];
      this.usedIndex[newIndex] = true; //since we've used this index row/col, mark it in array.
      this.lastIndex = newIndex;
      return;
   }

   public List<Integer> findNextNearestPellets () { 
      //TODO: Loop through size of used array (skipping where it's true) while looking at the cost table to find the lowest costs
      //      if it's less than min, clear list and set new min; if equal add it to the list.
      // System.out.println(costTable[lastIndex][0]);
      List<Integer> pellets = new ArrayList<Integer>();
      int min = Integer.MAX_VALUE;
      for (int i = 0; i < usedIndex.length; i++) {
         if ( usedIndex[i] == true || costTable[lastIndex][i] == 0) continue;     //skipped if we've already used this point & avoid using our current pos
         if ( costTable[lastIndex][i] < min ) { 
            pellets.clear();
            min = costTable[lastIndex][i];
            pellets.add(i);
         }else if ( costTable[lastIndex][i] == min)
            pellets.add(i);
      }
      return pellets;   //returns list indexes for use in dest
   }

   @Override
   public int compareTo( RNNAPath a ) {
      // *MAY NEED TO ADD A CHECK TO MAKE SURE THE PATHS ARE THE SAME LENGTH*
      if ( a.totalCost <  this.totalCost ) return 1;// if return 1 if this has higher cost
      if ( a.totalCost == this.totalCost) return 0;
      if ( a.totalCost >  this.totalCost ) return -1;// if return -1 if a has higher cost.
      return 0;
   }

   @Override
   public String toString(){
      String pts = "";
      for (int i = 0; i < path.size(); i++){
         pts += "[(" + path.get(i).x + "," + path.get(i).y + ")," + costBreakup.get(i) + "]";
      }
      return "cost=" + this.totalCost + " : " + pts;
   }
   
}



