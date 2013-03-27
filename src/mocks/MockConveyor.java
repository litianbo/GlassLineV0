package mocks;

import engine.agent.Glass;
import engine.util.ConveyorFamily;
import interfaces.Conveyor;
import interfaces.Sensor;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class MockConveyor implements TReceiver, Conveyor {
	Transducer t;
	String name;
	ConveyorFamily cf;
	public EventLog log = new EventLog();

	public MockConveyor(String name, Transducer t) {
		this.t = t;
		this.name = name;
	}

	public MockConveyor(String name, Transducer transducer,
			ConveyorFamily conveyorFamily1) {
		this.t = transducer;
		this.name = name;
		this.cf = conveyorFamily1;
	}

	@Override
	public void msgCanISendGlass(Sensor sensor, Glass glass) {
		log.add(new LoggedEvent(
				"I know that sensor is going to send glass from "
						+ sensor.getName()));

	}

	@Override
	public void msgHereIsGlass(Sensor sensor, Glass glass) {
		log.add(new LoggedEvent("I know that there is glass coming "
				+ glass.getName() + " from " + sensor.getName()));

	}

	@Override
	public void msgGlassIsWaiting(Sensor sensor) {
		log.add(new LoggedEvent("I know that there is glass waiting "
				+ sensor.getName()));

	}

	@Override
	public void msgIAmOccupied() {
		log.add(new LoggedEvent("I know that sensor is occupied "));

	}

	@Override
	public void msgIAmEmpty() {
		log.add(new LoggedEvent("I know that sensor is empty"));

	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgStop() {
		log.add(new LoggedEvent("conveyor stopped"));
		
	}

	@Override
	public void msgStart() {
		log.add(new LoggedEvent("conveyor started"));
		
	}

}
