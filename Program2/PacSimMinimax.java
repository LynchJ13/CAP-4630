//import statments

public class PacSimMinimax implements PacAction {

	//optional classes

	public PacSimMinimax( int depth, String fname, int te, int gran, int max ){

		//optional variables
		PacSim sim = new PacSim( fname, te, gran, max );
		sim.init(this);
	}

	public static void main(String[] args) { 
		String fname = args[0];
		int depth = Integer.parseInt(args[1]);

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

		//magic goes here
		return newFace;
	}
}