package engine.agent;

public class Recipe {
	// Data
	boolean needBreakout = true;
	boolean needCrossseam = true;
	boolean needCutting = true;
	boolean needDrilling = true;
	boolean needGrinding = true;
	boolean needBaking = true;
	boolean needPainting = true;
	boolean needUV = true;
	boolean needWashing = true;
	/**
	 * default constructor for the glass that need everything
	 */
	public Recipe(){
		
	}
	/**
	 * constructor for recipe in detail
	 * @param b1
	 * @param b2
	 * @param b3
	 * @param b4
	 * @param b5
	 * @param b6
	 * @param b7
	 * @param b8
	 */
	public Recipe(boolean b1, boolean b2, boolean b3, boolean b4, boolean b5,
			boolean b6,boolean b7,boolean b8) {
		needBreakout = b1;
		
	}

}
