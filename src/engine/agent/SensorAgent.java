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
	List<Object> args = Collections.synchronizedList(new ArrayList<Object>());
	private List<Glass> glasses = Collections
			.synchronizedList(new ArrayList<Glass>());

	/**
	 * constructor for name of the sensor
	 * 
	 * @param name
	 */
	public SensorAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		transducer.register(this, TChannel.ALL_AGENTS);
		this.cf = cf;
	}

	// messages
	@Override
	public void msgHereIsGlass(Conveyor conveyor,Glass glass) {
		// here, sensor suppose to know every glass passed to it.
		glasses.add(glass);
		
	}

	@Override
	public void msgIReceivedGlass(Conveyor conveyor,Glass glass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgGlassPassed(Glass glass) {
		// TODO Auto-generated method stub

	}

	// schduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
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

		}
		// react when a glass has moved off of the sensor
		if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED) {

			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
		}

	}

	public String getName() {
		return name;
	}
}
