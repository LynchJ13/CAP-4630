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
	private static int depthCount;
	private static int fearCounter;

	public PacSimMinimax( int depth, String fname, int te, int gran, int max ){

		//optional variables
		PacSim sim = new PacSim( fname, te, gran, max );
		sim.init(this);
	}

	public static void main(String[] args) { 
		String fname = args[0];
		int depth = Integer.parseInt(args[1]);
		depthCount = depth;
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
		List<PacCell> currPacCells = new ArrayList<PacCell>();

		List<Point> ghosts = PacUtils.findGhosts ( grid );
		GhostCell blinky = new BlinkyCell(ghosts.get(0).x, ghosts.get(0).y) ;
		GhostCell inky = new InkyCell(ghosts.get(1).x, ghosts.get(1).y) ;

		// System.out.println( pc.getLoc() );
		currPacCells.add( pc.clone() ); // add pacman
		currPacCells.add( blinky.clone() ); // blinky
		currPacCells.add( inky.clone() ); // inky

		// currPoints.forEach( (n) -> System.out.println( n ) );

		List<PacCell> nextPacCells = calcMinimax( grid, 0, 0, currPacCells );
		System.out.println("Cycle Complete");

		
		if ( pc == null ) return null;
		//magic goes here

		return newFace;
	}

	//recursive function that runs in 3-tick intervals for pacman & ghosts
	// every 3rd tick results in 1 move,
/*	PacCell calcMinimax ( PacCell[][] grid , int depth, int tick, PacCell cell ){
	 	// If we've hit the depth, just return the points we already have
		if ( depth == depthCount ){
			// System.out.println(evaluateBoard(grid, cells));

			return cell;
		}
		
		if (tick > 0 ){
			List<Point> ghosts = PacUtils.findGhosts ( grid );
			cell = grid[ghosts.get(tick-1).x][ghosts.get(tick-1).y];
		} else {
			cell = PacUtils.findPacman( grid );
		}
		System.out.println(cell.getClass());

		if ( tick == 2 )
			return calcMinimax(grid, depth+1, 0, cell);
		return calcMinimax( grid, depth, tick+1, cell );

	}*/

	//TODO: work on the return type for this? COULD PROBABLY MAKE THIS x1000000 EASIER
	List<PacCell> calcMinimax ( PacCell[][] grid , int depth, int tick, List<PacCell> cells ){
	 	// If we've hit the depth, just return the list of cells that has the best score given the current depth.
		if ( depth == depthCount || tick == 3  ){
			System.out.println(evaluateBoard(grid, cells));

			return cells;
		}
		
		System.out.printf("Depth: %d\n", depth);
		// cells.forEach( (n) -> System.out.println("\t"+n + " : " + n.getLoc()));
		

		// iterate through our wanted targets
		// TODO: The below method doesn't properly move BOTH of the ghosts on their turns
		// Change the 
		//
		PacCell[][] newGrid = new PacCell[grid.length][grid[0].length];
		List<PacCell> nextTgts = new ArrayList<PacCell>(cells);

		for ( PacFace c : PacFace.values() ) { //Iterate through each cardinal direction
			PacCell neighbor = PacUtils.neighbor( c, cells.get(tick), grid );

			// skips wallcells as ghosts, but also skip house cells if pacman.
			System.out.printf("   Testing neighbor(%d): %s...", tick, c.toString() );
			if ( neighbor instanceof pacsim.WallCell || (cells.get(tick) instanceof pacsim.PacmanCell && neighbor instanceof pacsim.HouseCell) ){
				System.out.println("Fail");
				continue;
			}
			System.out.println("Success");
			if ( cells.get(tick) instanceof pacsim.PacmanCell ){
				// System.out.println( PacUtils.findPacman(PacUtils.movePacman( cells.get(t).getLoc(), neighbor.getLoc(), newGrid ) ).getLoc() ) ;
				newGrid = PacUtils.movePacman( cells.get(tick).getLoc(), neighbor.getLoc(), grid );
				nextTgts.set(tick, PacUtils.findPacman( newGrid ) );
				nextTgts = calcMinimax( newGrid, depth, tick+1, nextTgts);

			} else { // ghost move
				newGrid = PacUtils.moveGhost( cells.get(tick).getLoc(), neighbor.getLoc(), grid );
				if ( tick == 1 ) { //blinky 
					nextTgts.set(tick, new BlinkyCell(neighbor.getLoc().x, neighbor.getLoc().y) );
					nextTgts = calcMinimax( newGrid, depth, tick+1, nextTgts);
				} else { // inky
					nextTgts.set(tick, new InkyCell(neighbor.getLoc().x, neighbor.getLoc().y) );
					nextTgts = calcMinimax( newGrid, depth+1, tick+1, nextTgts);
				}
			}
		}			

		// return calcMinimax( newGrid, depth+1, 0, nextTgts);
		return cells;

	}

	public int evaluateBoard( PacCell[][] grid, List<PacCell> cells ) {
		System.out.println(cells.get(0).getLoc() );
		System.out.println(cells.get(1).getLoc() );
		System.out.println(cells.get(2).getLoc() );
		int distPellet = BFSPath.getPath( grid, cells.get(0).getLoc(), PacUtils.nearestGoody(cells.get(0).getLoc(), grid) ).size();
		int distBlinky = BFSPath.getPath( grid, cells.get(1).getLoc(), cells.get(0).getLoc()  ).size();
		int distInky = BFSPath.getPath( grid, cells.get(2).getLoc(), cells.get(0).getLoc()  ).size();
		System.out.printf("Pellet: %d\nBlinky: %d\nInky: %d\n", distPellet, distBlinky, distInky);
		return (WIN_VALUE - distPellet) + (LOSS_VALUE + distBlinky + distInky);
	}

	/* Current eval procedure
	 *	V = (winValue - distPellet)	+ (lossReward + (distBlinky + distInky))
	 */


}