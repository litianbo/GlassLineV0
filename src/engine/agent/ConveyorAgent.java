package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import interfaces.Conveyor;

public class ConveyorAgent extends Agent implements Conveyor {
	private enum ConveyorState {
		GLASS_ARRIVED, WAIT_FOR_POPUP,SENDING_GLASS_TO_POPUP, SENDING_GLASS_TO_END_SENSOR, NULL
	}

	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass>());
	List<Glass> waitingGlasses = Collections.synchronizedList(new ArrayList<Glass>());
	ConveyorState state = ConveyorState.NULL;
	ConveyorFamily cf;
	boolean popupOccupied =false;
	/**
	 * constructor for conveyor agent
	 * @param name
	 * @param t
	 */
	public ConveyorAgent(String name, Transducer t, ConveyorFamily cf){
		super(name,t);
		this.cf = cf;
		//register at conveyor and react
		transducer.register(this, TChannel.CONVEYOR);
	}
	/**
	 * sent from front end sensor
	 */
	@Override
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		state = ConveyorState.GLASS_ARRIVED;
	}

	@Override
	public void msgCanITakeThisGlass(Glass glass) {
		// TODO Auto-generated method stub

	}
	/**
	 * sent from the popup that it is working on a glass;
	 */
	@Override
	public void msgIAmOccupied(){
		popupOccupied = true;
	}
	/**
	 * sent from the popup that it is free;
	 */
	@Override
	public void msgIAmEmpty(){
		popupOccupied = false;
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(glasses.size()>=2){
			print("Too many glasses at this conveyor!!!, stop " + this);
			stopConveyor();
			return true;
		}
		//only if the popup is not occupied;
		if(state == ConveyorState.GLASS_ARRIVED && !popupOccupied || name =="Conveyor2"){
			print("glass " + glasses.get(0).getName()+ " arrived to conveyor " + this + " it will be passing to popup now");
			//add more implementation here further
			glassArrived(glasses.remove(0));
			return true;
		}
		if(state == ConveyorState.GLASS_ARRIVED && popupOccupied){
			//ToDo: enhance the throughput here
			state = ConveyorState.WAIT_FOR_POPUP;
			return true;
		}
		if(state == ConveyorState.WAIT_FOR_POPUP && waitingGlasses.size()==0){
			notifyPopupGlassIsWaiting();
			return true;
		}
		if(state == ConveyorState.WAIT_FOR_POPUP && waitingGlasses.size()>0 && !popupOccupied){
			pushGlassToPopup(waitingGlasses.remove(0));
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
			
	}
	
	//methods:
	public void pushGlassToPopup(Glass glass){
		cf.popup.msgHereIsGlass(this, glass);
	}
	/**
	 * notify msg to popup and put the glass to waiting list
	 */
	public void notifyPopupGlassIsWaiting(){
		print("glass " + glasses.get(0).getName() +  "is waiting in the conveyor " + this);
		cf.popup.msgGlassIsWaiting(this);
		waitingGlasses.add(glasses.remove(0));
	}
	public void stopConveyor(){
		
	}
	public void glassArrived(Glass glass){
		//if this is front end conveyor, pass the glass to the popup if possible
		if(name =="Conveyor1"){
			cf.popup.msgHereIsGlass(this, glass);
		}
		else if(name == "Conveyor2"){
			cf.sensor2.msgHereIsGlass(this, glass);
		}
	}
	public String getName(){
		return name;
	}
}
