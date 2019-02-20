/* 
 * University of Central Florida
 * CAP4630 - Spring 2019
 * Author: John Lynch
 */

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
import pacsim.*;

public class PacSimMinimax implements PacAction {

	//optional classes
	private final int WIN_VALUE = 30;
	private final int LOSS_VALUE = -30;
	private final int[] dX = {  0, -1, 0, 1 }; //Transversal Order: UP, LEFT, DOWN, RIGHT
	private final int[] dY = { -1,  0, 1, 0 };
	private static int dpth;


	public PacSimMinimax( int depth, String fname, int te, int gran, int max ){

		//optional variables
		PacSim sim = new PacSim( fname, te, gran, max );
		sim.init(this);
	}

	public static void main(String[] args) { 
		String fname = args[0];
		int depth = Integer.parseInt(args[1]);
		dpth = depth;
		int te = 0;
		int gr = 0;
		int ml = 0;

		if ( args.length == 5 ) {
			te = Integer.parseInt( args[2] );
			gr = Integer.parseInt( args[3] );
			ml = Integer.parseInt( args[4] );
		}

		new PacSimMinimax( depth, fname, te, gr, ml );

		System.out.println("\nAdversarial Search using Minimax by John Lynch");
		System.out.println("\n\t\tGame Board: " + fname);
		System.out.println("\n\t\tSearch Depth : " + depth + "\n");
		if ( te > 0 ) {
			System.out.println("\tPreliminary runs: " + te
				+ "\n\tGranularity\t:" + gr
				+ "\n\tMax move limit\t:" + ml
				+ "\n\nPreliminary run results :\n");
		}
	}

	@Override
	public void init() {}

	@Override
	public PacFace action( Object state ) {
		PacCell[][] grid = (PacCell[][]) state;
		PacFace newFace = null;
		PacmanCell pc = PacUtils.findPacman ( grid );
		List<Point> ghosts = PacUtils.findGhosts ( grid );
		
		GhostCell blinky = new BlinkyCell(ghosts.get(0).x, ghosts.get(0).y) ;
		GhostCell inky = new InkyCell(ghosts.get(1).x, ghosts.get(1).y) ;
		System.out.println( blinky.getMode() == PacMode.valueOf("FEAR")  );
		System.out.println( blinky.getMode() == PacMode.valueOf("SCATTER") );
		System.out.println( blinky.getMode() == PacMode.valueOf("CHASE") );


		List<PacCell[][]> boardIterations = new ArrayList<PacCell[][]>();

		// boardIterations.add( (PacCell[][])grid.clone() ); //create a DEEP copy, since we'll be probably modifying it in the loop.

		/*for ( int d = 0; d < dpth; d++ ) {
			boardIterations = testMinimax( boardIterations.get(d) );
		}*/

		if ( pc == null ) return null;
		//magic goes here

		return newFace;
	}

	/*List<PacCell[][]> testMinimax ( List<PacCell[][]> boards ){
		List<PacCell[][]> nextDepthIteration = new ArrayList<PacCell[][]>();
		// for
		return 
	}*/
	/* Current eval procedure
	 *	V = (winValue - distPellet)	+ (lossReward + (distBlinky + distInky))
	 */
	public int evaluateBoard( PacCell[][] grid, PacmanCell pacman ) {
		int distPellet = 0;
		int distBlinky = 0;
		int distInky = 0;
		return (WIN_VALUE - distPellet) - (LOSS_VALUE + distBlinky + distInky);
	}
}