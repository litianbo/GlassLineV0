package interfaces;

import transducer.Transducer;
import engine.agent.Glass;

public interface WorkStation {
	
	/**
	 * sent from popup, when it raised, pass glass to empty work station
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Popup popup,Glass glass);
	/**
	 * popup is lowered
	 */
	public abstract void msgIAmLowered();
	/**
	 * popup is raised
	 */
	public abstract void msgIAmRaised();
	public abstract String getName();
	
}
