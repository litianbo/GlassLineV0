package mocks;

import engine.agent.Glass;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;
import interfaces.WorkStation;

public class MockPopup implements Popup, TReceiver {
	Transducer t;
	ConveyorFamily cf, cf2;
	String name;
	public EventLog log = new EventLog();

	public MockPopup(String name, Transducer transducer,
			ConveyorFamily conveyorFamily1) {
		this.name = name;

		this.t = transducer;
		t.register(this, TChannel.SENSOR);
		this.cf = conveyorFamily1;
	}

	public MockPopup(String name, Transducer transducer,
			ConveyorFamily conveyorFamily1, ConveyorFamily conveyorFamily2) {
		this.name = name;
		this.t = transducer;
		t.register(this, TChannel.SENSOR);
		this.cf = conveyorFamily1;
		this.cf2 = conveyorFamily2;
	}

	@Override
	public void msgHereIsGlass(Sensor sensor, Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Popup received glass " + glass.getName()
				+ " from agent " + sensor));
	}

	@Override
	public void msgCanISendGlass(Sensor sensor, Glass glass) {
		log.add(new LoggedEvent(
				"I know that sensor is going to send glass from "
						+ sensor.getName()));

	}

	@Override
	public void msgGlassDone(WorkStation work,Glass glass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgIAmOccupied(Sensor sensor) {
		log.add(new LoggedEvent("I know that sensor is occupied from "
				+ sensor.getName()));

	}

	@Override
	public void msgIAmEmpty(Sensor sensor) {
		log.add(new LoggedEvent("I know that sensor is empty from "
				+ sensor.getName()));

	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

}
