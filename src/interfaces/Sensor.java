package interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Glass;

public interface Sensor {

	/**
	 * sent from conveyor
	 * 
	 * @param conveyor
	 */
	public abstract void msgCanISendGlass(Conveyor conveyor);

	/**
	 * sent from conveyor
	 */
	public abstract void msgIAmOccupied(Conveyor conveyor);

	/**
	 * sent from conveyor
	 */
	public abstract void msgIAmEmpty(Conveyor conveyor);

	/**
	 * sent from popup as as response to msgCanISendGlass
	 */
	public abstract void msgIAmEmpty();

	/**
	 * sent from popup
	 */
	public abstract void msgIAmOccupied();

	/**
	 * sent from conveyor there is glass waiting on the sensor, only for test purpose
	 * 
	 * @param conveyor
	 */
	public abstract void msgGlassIsWaiting(Conveyor conveyor);

	/**
	 * pass glass to the sensor, sent from conveyor
	 * 
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Conveyor conveyor, Glass glass);

	/**
	 * pass glass to the sensor, sent from popup
	 * 
	 * @param popup
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Popup popup, Glass glass);

	/**
	 * sent from popup he wants to send a glass to sensor
	 */
	public abstract void msgCanISendGlass();

	public abstract String getName();

	/**
	 * fire an event
	 * 
	 * @param channel
	 * @param event
	 * @param args
	 */
	public abstract void eventFired(TChannel channel, TEvent event,
			Object[] args);

}
