package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;

import transducer.*;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;

public class SensorAgent extends Agent implements Sensor {
	// Data:
	ConveyorFamily cf, pcf;
	SensorState state;
	List<Object> args = Collections.synchronizedList(new ArrayList<Object>());
	private List<Glass> glasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	boolean pcfIsWaiting = false;
	boolean stopConveyor = false;

	private enum SensorState {
		NULL, STOP_CONVEYOR, EMPTY, TELL_CONVEYOR_TO_SEND_GLASS, OCCUPIED, WAIT_CONVEYOR_TO_START, SENDING_GLASS_TO_CONVEYOR, TELL_POPUP_WAITING_FOR_CLEAR, WAITING_FOR_POPUP_SENDING_GLASS, OCCUPIED_BUT_POPUP_IS_NOT_OCCUPIED, OCCUPIED_AND_SO_DOES_POPUP, EMPTY_BUT_POPUP_IS_NOT_EMPTY, EMPTY_AND_SO_DOES_POPUP
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

	/**
	 * 2nd constructor for sensor
	 * 
	 * @param name
	 */
	public SensorAgent(String name, Transducer t, ConveyorFamily cf,
			ConveyorFamily pcf) {
		super(name, t);
		transducer.register(this, TChannel.ALL_AGENTS);
		this.cf = cf;
		this.pcf = pcf;
		state = SensorState.EMPTY;
	}

	// messages
	/**
	 * sent from conveyor
	 */
	public void msgCanISendGlass(Conveyor conveyor) {
		print("State: " + state);
		if (state == SensorState.EMPTY
				|| state == SensorState.EMPTY_AND_SO_DOES_POPUP
				|| state == SensorState.EMPTY_BUT_POPUP_IS_NOT_EMPTY)
			state = SensorState.TELL_CONVEYOR_TO_SEND_GLASS;
		if (state == SensorState.OCCUPIED_AND_SO_DOES_POPUP
				|| state == SensorState.OCCUPIED
				|| state == SensorState.OCCUPIED_BUT_POPUP_IS_NOT_OCCUPIED) {
			state = SensorState.STOP_CONVEYOR;
		}
		stateChanged();
	}

	/**
	 * sent from conveyor, I can't send glass to it
	 */
	@Override
	public void msgIAmOccupied(Conveyor conveyor) {

		state = SensorState.WAIT_CONVEYOR_TO_START;
		stateChanged();

	}

	/**
	 * sent from conveyor, I can send glass to it.
	 */
	@Override
	public void msgIAmEmpty(Conveyor conveyor) {
		if (glasses.size() > 0)
			state = SensorState.SENDING_GLASS_TO_CONVEYOR;
		stateChanged();

	}

	/**
	 * can pass glass to popup
	 */
	public void msgIAmEmpty() {

		if (state == SensorState.OCCUPIED
				|| state == SensorState.OCCUPIED_AND_SO_DOES_POPUP
				|| state == SensorState.OCCUPIED_BUT_POPUP_IS_NOT_OCCUPIED) {

			state = SensorState.OCCUPIED_BUT_POPUP_IS_NOT_OCCUPIED;

		} else
			state = SensorState.EMPTY_AND_SO_DOES_POPUP;
		stateChanged();
	}

	/**
	 * sent from popup to next family
	 */
	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		state = SensorState.OCCUPIED;
		stateChanged();
	}

	/**
	 * popup is occupied
	 */
	@Override
	public void msgIAmOccupied() {
		if (state == SensorState.EMPTY) {
			state = SensorState.EMPTY_BUT_POPUP_IS_NOT_EMPTY;
		} else if (state == SensorState.OCCUPIED) {
			state = SensorState.OCCUPIED_AND_SO_DOES_POPUP;
		}
		stateChanged();
	}

	

	@Override
	/**
	 * glass is waiting on the conveyor
	 */
	public void msgGlassIsWaiting(Conveyor conveyor) {
		// do nothing, this is test msg for conveyor
		stateChanged();

	}

	/**
	 * sent from popup, needs to clear the sensor ASAP!
	 */
	@Override
	public void msgCanISendGlass() {
		// TODO Auto-generated method stub
		if (state == SensorState.EMPTY) {// if sensor is cleared
			state = SensorState.WAITING_FOR_POPUP_SENDING_GLASS;
		} else if (state == SensorState.OCCUPIED) {// if sensor is occupied due
			state = SensorState.TELL_POPUP_WAITING_FOR_CLEAR; // to too many
																// glasses on
			// conveyor

		}
		stateChanged();
	}

	@Override
	public void msgHereIsGlass(Conveyor conveyor, Glass glass) {
		// here, sensor suppose to know every glass passed to it.
		glasses.add(glass);
		state = SensorState.OCCUPIED;
		stateChanged();
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
				cf.popup.msgCanISendGlass(this, glasses.get(0));
			} else if (name == "Sensor1") {// if it is sensor1, which send glass
											// to conveyor directly, then notify
											// conveyor
				cf.conveyor1.msgCanISendGlass(this, glasses.get(0));

			}
			return true;
		}
		if (state == SensorState.TELL_POPUP_WAITING_FOR_CLEAR) {
			// tell popup to wait
			notifyPopupToWait();
			return true;
		}
		if (state == SensorState.WAITING_FOR_POPUP_SENDING_GLASS) {
			notifyPopupToSend();
			return true;
		}
		if (state == SensorState.OCCUPIED_BUT_POPUP_IS_NOT_OCCUPIED) {

			passGlassToPopup();
			if (stopConveyor)
				activeConveyor();
			return true;
		}
		if (state == SensorState.SENDING_GLASS_TO_CONVEYOR) {
			sendGlassToConveyor();
			return true;
		}
		if (state == SensorState.WAIT_CONVEYOR_TO_START) {
			notifyPopupToWait();
			return true;
		}
		if (state == SensorState.TELL_CONVEYOR_TO_SEND_GLASS) {
			notifyConveyorToSend();
			return true;
		}
		if (state == SensorState.STOP_CONVEYOR) {
			tellConveyorToStop();
			return true;
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

	// methods:
	public void activeConveyor() {
		print("Conveyor needs to work!");
		cf.conveyor1.msgStart();
		stopConveyor = false;
		stateChanged();
	}

	public void tellConveyorToStop() {
		print("Conveyor needs to stop!");
		cf.conveyor1.msgStop();
		stopConveyor = true;
		state = SensorState.OCCUPIED_AND_SO_DOES_POPUP;// switch it back to full
														// occupied state
		stateChanged();
	}

	public void notifyConveyorToSend() {
		print("Notify conveyor to send glass because I am empty");
		cf.conveyor1.msgIAmEmpty();
		stateChanged();
	}

	public void sendGlassToConveyor() {
		cf.conveyor1.msgHereIsGlass(this, glasses.remove(0));
		state = SensorState.EMPTY;
		stateChanged();
	}

	public void passGlassToPopup() {
		cf.popup.msgHereIsGlass(this, glasses.remove(0));
		state = SensorState.EMPTY;
		stateChanged();
	}

	public void notifyPopupToSend() {
		if (pcf != null) {
			pcf.popup.msgIAmEmpty(this);
			pcfIsWaiting = false;
		} else
			print("previous conveyor family is null, check it out!!!");

		stateChanged();
	}

	public void notifyPopupToWait() {
		if (pcf != null) {
			pcf.popup.msgIAmOccupied(this);
			pcfIsWaiting = true;
		} else
			print("previous conveyor family is null, check it out!!!");
		state = SensorState.OCCUPIED;// change the state back to occupied
		stateChanged();
	}

	public String getName() {
		return name;
	}

}
