package interfaces;

import engine.agent.Glass;

public interface WorkStation {
	/**
	 * sent from popup, when it raised, pass glass to empty work station
	 * @param glass
	 */
	public abstract void msgHereIsGlass(Popup popup,Glass glass);
	
	
	
	
}
