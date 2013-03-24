package interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Glass;

public interface Sensor {
	/**
	 * pass glass to the sensor
	 * @param glass 
	 */
	public abstract void msgHereIsGlass(Conveyor conveyor, Glass glass);
	/**
	 * print on the console that the sensor is currently holding a glass
	 * @param glass
	 */
	public abstract void msgIReceivedGlass(Conveyor conveyor,Glass glass);
	/**
	 * glass passed this sensor
	 * @param glass
	 */
	public abstract void msgGlassPassed(Glass glass);
	
	public abstract String getName();
	/**
	 * fire an event 
	 * @param channel
	 * @param event
	 * @param args
	 */
	public abstract void eventFired(TChannel channel, TEvent event,
			Object[] args);
}
