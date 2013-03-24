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
	public abstract void msgGlassIsWaiting(Conveyor conveyor);
	public abstract void setName(String name);
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
