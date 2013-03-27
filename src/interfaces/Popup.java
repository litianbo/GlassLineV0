package interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Glass;
import engine.agent.SensorAgent;

public interface Popup {
	/**
	 * sent from workstation
	 * @param glass
	 */
	public abstract void msgGlassDone(Glass glass);
	/**
	 * 
	 * @param glass
	 *            the glass that pass to popup agent
	 */
	public abstract void msgHereIsGlass(Sensor sensor, Glass glass);

	/**
	 * glass is waiting on the sensor
	 * 
	 * @param sensor
	 */
	public abstract void msgGlassIsWaiting(Sensor sensor);

	/**
	 * sent from front end sensor, check to see if two workstations are both
	 * occupied
	 * 
	 */
	public abstract void msgCanISendGlass(Sensor sensor,Glass glass);

	public abstract void setName(String name);

	/**
	 * sent from sensor, as a response to popup's msgCanISendGlass
	 */
	public abstract void msgIAmOccupied(Sensor sensor);

	/**
	 * sent from sensor, as a response to popup's msgCanISendGlass
	 */
	public abstract void msgIAmEmpty(Sensor sensor);

	/**
	 * return the name of this popup
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * react to the event fired by other agents
	 * 
	 * @param channel
	 * @param event
	 * @param args
	 */

	public abstract void eventFired(TChannel channel, TEvent event,
			Object[] args);

}
