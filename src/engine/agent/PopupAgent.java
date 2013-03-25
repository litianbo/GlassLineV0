package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;
import transducer.*;

public class PopupAgent extends Agent implements Popup {
	// Data:
	ConveyorFamily cf;
	List<Object> glasses = Collections
			.synchronizedList(new ArrayList<Object>());
	String name;
	boolean raise = false;
	boolean sensorOccupied = false;
	public PopupAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		this.cf = cf;
		// transducer.register(this, TChannel.ALL_AGENTS);
		transducer.register(this, TChannel.POPUP);
	}

	// message
	/**
	 * back end sensor is occupied
	 */
	@Override
	public void msgIAmOccupied() {
		// TODO Auto-generated method stub
		print("back end sensor is occupied, don't pass glass");
		sensorOccupied = true;
		stateChanged();
	}

	/**
	 * back end sensor is empty, glass can pass
	 */
	@Override
	public void msgIAmEmpty() {
		// TODO Auto-generated method stub
		print("back end sensor is occupied, pass glass");
		sensorOccupied = false;
		stateChanged();
	}

	/**
	 * glass is waiting in the conveyor
	 */
	@Override
	public void msgGlassIsWaiting(Conveyor conveyor) {
		// TODO Auto-generated method stub
		// do nothing here
	}

	/**
	 * message that receive glass from the sensor or another conveyer family
	 */
	@Override
	public void msgHereIsGlass(Conveyor conveyor, Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		print("received message from " + conveyor + " to work on "
				+ glass.getName());
		raise = true;
		stateChanged();
	}

	// scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (raise && glasses.size() > 0) {

			DoRaisePopup();
		}
		if (!raise) {
			DoLowerPopup();
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
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED,
					args);
		}
	}

	// methods:
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	/**
	 * ToDo: add some detail
	 */
	public void DoRaisePopup() {
		glasses.remove(0);
		cf.conveyor1.msgIAmOccupied();
		raise = false;
	}

	public void DoLowerPopup() {
		cf.conveyor1.msgIAmEmpty();
	}

}
