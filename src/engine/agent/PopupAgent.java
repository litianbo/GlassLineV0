package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;
import transducer.*;

public class PopupAgent extends Agent implements Popup {
	// Data:
	ConveyorFamily cf, cf2;
	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass>());
	String name;
	boolean raise = false;
	boolean sensorOccupied = false;
	PopupState state = PopupState.NULL;

	enum PopupState {
		NULL, WORKING_ON_GLASS, GLASS_ARRIVED, SENDING_GLASS_TO_SENSOR, WAITING_EMPTY, WAITING_OCCUPIED
	}

	public PopupAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		this.cf = cf;
		// transducer.register(this, TChannel.ALL_AGENTS);
		transducer.register(this, TChannel.POPUP);

	}

	/**
	 * 2nd constructor for PopupAgent, I think the popup is at the end of each
	 * conveyor, hence, it has the responsibility to connect with the next
	 * conveyor
	 * 
	 * @param name
	 * @param t
	 * @param cf1
	 * @param cf2
	 */
	public PopupAgent(String name, Transducer t, ConveyorFamily cf1,
			ConveyorFamily cf2) {

		super(name, t);
		this.cf = cf1;
		this.cf2 = cf2;
		// transducer.register(this, TChannel.ALL_AGENTS);
		transducer.register(this, TChannel.POPUP);
	}

	// message
	/**
	 * sent from sensor there is a glass waiting
	 */
	@Override
	public void msgGlassIsWaiting(Sensor sensor) {
		// TODO Auto-generated method stub

	}

	/**
	 * next conveyor family sensor is occupied
	 */
	@Override
	public void msgIAmOccupied() {
		// TODO Auto-generated method stub
		print("front end sensor is occupied, don't pass glass");
		sensorOccupied = true;
		state = PopupState.WAITING_OCCUPIED;
		stateChanged();
	}

	/**
	 * next conveyor family is empty, glass can pass
	 */
	@Override
	public void msgIAmEmpty() {
		// TODO Auto-generated method stub
		print("next conveyor family is empty, pass glass");
		sensorOccupied = false;
		state = PopupState.WAITING_EMPTY;
		stateChanged();
	}

	/**
	 * message that receive glass from the sensor or another conveyer family
	 */
	@Override
	public void msgHereIsGlass(Sensor sensor, Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		print("received message from " + sensor.getName() + " to work on "
				+ glass.getName());
		state = PopupState.GLASS_ARRIVED;
		stateChanged();
	}

	// scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (state == PopupState.GLASS_ARRIVED) {
			glassArrived();
			return true;
		}
		if (state == PopupState.WORKING_ON_GLASS && raise && glasses.size() > 0) {

			DoRaisePopup();
			return true;
		}

		if (state == PopupState.SENDING_GLASS_TO_SENSOR) {// sent by GUI popup
			notifyNextFamily();
			return true;
		}
		if(state == PopupState.WAITING_EMPTY){
			//next conveyor family is empty in first sensor
			passGlassToNextFamily();
			return true;
		}
		if(state == PopupState.WAITING_OCCUPIED){
			//TODO: what to do here?
			return true;
		}
		return false;
	}

	@Override
	public synchronized void eventFired(TChannel channel, TEvent event,
			Object[] args) {
		// TODO Auto-generated method stub
		// do something when the GUI fire the moved up event, here move this
		// down, fix this later
		if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_MOVED_UP) {

			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN,
					args);
		}
		// Fired when the popup should push its glass onto the next conveyor
		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_MOVED_DOWN) {

			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS,
					args);
		}
		// do something when load finished, here, always raise this up fix this
		// later
		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_LOAD_FINISHED) {
			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
		}
		// do something when release finished, fire an event to sensor
		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_RELEASE_FINISHED) {
			state = PopupState.SENDING_GLASS_TO_SENSOR;
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED,
					args);
		}
		stateChanged();
	}

	// methods:
	
	public void passGlassToNextFamily(){
		cf2.sensor1.msgHereIsGlass(this,glasses.remove(0));
		stateChanged();
	}
	public void glassArrived() {
		// TODO add more implementation later, needs to check recipe here
		// if(glass.recipe.XXX)
		state = PopupState.WORKING_ON_GLASS;
		raise = true;
		stateChanged();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	/**
	 * ToDo: add some detail
	 */
	public void DoRaisePopup() {
		// glasses.remove(0);
		cf.sensor2.msgIAmOccupied();
		raise = false;
		stateChanged();
	}

	/**
	 * notify next conveyor family that I am going to send the glass to you!!
	 */
	public void notifyNextFamily() {
		print("I want to send glass to next conveyor family");
		cf2.sensor1.msgCanISendGlass();
		stateChanged();
	}

}
