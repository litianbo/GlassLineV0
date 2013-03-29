package interfaces;

import engine.agent.Glass;

public interface ConveyorFamilyInterface {
    /** Message from previous conveyor family handing glass over.
	* @param glass the glass
    */
	public abstract void msgHereIsGlass(Popup popup, Glass glass);
	
    /** Message from next conveyor family saying it has stopped
    */
	public abstract void msgStartConveyor();

    /** Message from next conveyor family saying it has restarted
    */
	public abstract void msgStopConveyor();
}
