package interfaces;

import engine.agent.Glass;

public interface Conveyor {
	/**
	 * sent from back end sensor
	 */
	public abstract void msgStart();
	/**
	 * sent from back end sensor
	 */
	public abstract void msgStop();
	/**
	 * pass the glass to this conveyor
	 * 
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Sensor sensor,Glass glass);

	/**
	 * notify conveyor that there is glass waiting,react to it!
	 * @param sensor
	 */
	public abstract void msgGlassIsWaiting(Sensor sensor);

	public abstract void msgCanISendGlass(Sensor sensor,Glass glass);

	/**
	 * sent from sensor2
	 */
	public abstract void msgIAmOccupied();

	/**
	 * sent from sensor2
	 */
	public abstract void msgIAmEmpty();

	
}
