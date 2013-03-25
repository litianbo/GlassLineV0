package interfaces;

import engine.agent.Glass;

public interface Conveyor {
	/**
	 * pass the glass to this conveyor
	 * 
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Glass glass);

	/**
	 * sent from the back end that want to receive the glass from the conveyor
	 * 
	 * @param glass
	 */
	public abstract void msgCanITakeThisGlass(Glass glass);
	/**
	 * sent from popup
	 */
	public abstract void msgIAmOccupied();
	/**
	 * sent from popup
	 */
	public abstract void msgIAmEmpty();
	
	
}
