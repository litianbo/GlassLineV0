package interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Glass;
import engine.agent.SensorAgent;

public interface Popup {
	/**
	 * 
	 * @param glass the glass that pass to popup agent
	 */
	public abstract void msgHereIsGlass(Conveyor conveyor, Glass glass);
	/**
	 * glass is waiting on the sensor
	 * @param sensor
	 */
	public abstract void msgGlassIsWaiting(Sensor sensor);
	
	public abstract void setName(String name);
	/**
	 * sent from sensor, as a response to popup's msgCanISendGlass
	 */
	public abstract void msgIAmOccupied();
	/**
	 * sent from sensor, as a response to popup's msgCanISendGlass
	 */
	public abstract void msgIAmEmpty();
	/**
	 * return the name of this popup
	 * @return
	 */
	public abstract String getName();
	/**
	 * react to the event fired by other agents
	 * @param channel
	 * @param event
	 * @param args
	 */
	 
	public abstract void eventFired(TChannel channel, TEvent event,
			Object[] args);
	
}
