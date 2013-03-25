package interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Glass;

public interface Sensor {
	/**
	 * sent from popup
	 */
	public abstract void msgIAmOccupied();
	/**
	 * if conveyor has more than 3 glasses which is more than its capacity,
	 * sensor should stop sending glass to it
	 */
	public abstract void msgStopSendingGlassToConveyor();

	/**
	 * sent from conveyor there is glass waiting on the sensor
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
	 * @param popup
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Popup popup,Glass glass);
	
	/**
	 * print on the console that the sensor is currently holding a glass
	 * 
	 * @param glass
	 *            ,conveyor
	 */
	public abstract void msgIReceivedGlass(Conveyor conveyor, Glass glass);

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
