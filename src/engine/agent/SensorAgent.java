package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;

import transducer.*;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;

public class SensorAgent extends Agent implements Sensor {
	// Data:
	ConveyorFamily cf;
	SensorState state;
	List<Object> args = Collections.synchronizedList(new ArrayList<Object>());
	private List<Glass> glasses = Collections
			.synchronizedList(new ArrayList<Glass>());

	private enum SensorState {
		NULL, EMPTY, OCCUPIED
	}

	/**
	 * constructor for name of the sensor
	 * 
	 * @param name
	 */
	public SensorAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		transducer.register(this, TChannel.ALL_AGENTS);
		this.cf = cf;
		state = SensorState.EMPTY;
	}

	// messages
	/**
	 * sent from conveyor 
	 */
	@Override
	public void msgStopSendingGlassToConveyor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * glass is waiting on the conveyor
	 */
	public void msgGlassIsWaiting(Conveyor conveyor) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * sent from popup, needs to clear the sensor ASAP!
	 */
	@Override
	public void msgCanISendGlass() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgHereIsGlass(Conveyor conveyor, Glass glass) {
		// here, sensor suppose to know every glass passed to it.
		glasses.add(glass);
		stateChanged();
	}

	@Override
	public void msgIReceivedGlass(Conveyor conveyor, Glass glass) {
		// TODO Auto-generated method stub

	}

	// schduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (state == SensorState.OCCUPIED) {
			// notify the conveyor/popup that I am occupied. can I send a glass?
			if (name == "Sensor2") {
				// sensor2 is the back end sensor which connect to the
				// workstation/popup directly, then notify popups
				cf.popup.msgGlassIsWaiting(this);
			} else if (name == "Sensor1") {// if it is sensor1, which send glass
											// to conveyor directly, then notify
											// conveyor
				cf.conveyor1.msgGlassIsWaiting(this);

			}
		}
		return false;
	}

	@Override
	public synchronized void eventFired(TChannel channel, TEvent event,
			Object[] args) {
		// TODO Auto-generated method stub
		// Fired when a glass has moved onto the sensor, args[0] is the index of
		// the sensor, args[1] is the index of the glass that pressed it
		if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED) {

			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED,
					args);
			state = SensorState.OCCUPIED;
		}
		// react when a glass has moved off of the sensor
		if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED) {
			if (name == "Sensor1")
				// if it is sensor1, fire event on conveyor 1 that a glass just
				// passed
				transducer.fireEvent(TChannel.CONVEYOR,
						TEvent.CONVEYOR_DO_START, args);
			else if (name == "Sensor2")// if it is sensor2 (back end sensor),
										// call next conveyor family a glass is
										// coming
				// fire which event to notify next conveyor family?
				transducer.fireEvent(TChannel.CONVEYOR,
						TEvent.CONVEYOR_DO_START, args);
			// for whatever sensor it is, change the state to empty
			state = SensorState.EMPTY;
		}

	}

	public String getName() {
		return name;
	}


	

}
