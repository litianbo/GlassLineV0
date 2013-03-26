package engine.agent;

import interfaces.Popup;
import interfaces.WorkStation;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * base agent for the workstation
 * @author lenovo
 *
 */
public class WorkStationAgent extends Agent implements WorkStation{
	//data:
	//each workstation can hold one glass
	Glass glass;
	//popup
	Popup popup;
	/**
	 * constructor
	 * @param name
	 * @param t
	 */
	public WorkStationAgent (String name, Transducer t){
		super(name,t);
	}
	
	//messages:
	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		this.glass = glass;
		this.popup = popup;
	}

	
	//scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		if(glass != null){
			processGlass();
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	//methods:
	public void processGlass(){
		//fire event to notify popup, this station is occupied;
		Object[] args = new Object[1];
		args[0] = new Long(0);
		transducer.fireEvent(TChannel.POPUP, TEvent.WORKSTATION_DO_LOAD_GLASS, args);
		if(name.contains("Top")){//top workstation
			
		}
	}
	
}
