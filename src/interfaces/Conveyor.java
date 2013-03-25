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
	 * notify conveyor that there is glass waiting,react to it!
	 * @param sensor
	 */
	public abstract void msgGlassIsWaiting(Sensor sensor);

	

	/**
	 * sent from popup
	 */
	public abstract void msgIAmOccupied();

	/**
	 * sent from popup
	 */
	public abstract void msgIAmEmpty();

}
