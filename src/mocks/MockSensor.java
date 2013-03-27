package mocks;

import mocks.LoggedEvent;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.util.ConveyorFamily;
import mocks.EventLog;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;

public class MockSensor implements Sensor, TReceiver {
	Transducer t;
	ConveyorFamily cf, pcf;
	String name;
	public EventLog log = new EventLog();

	public MockSensor(String name, Transducer t, ConveyorFamily cf) {
		this.name = name;
		// TODO Auto-generated constructor stub
		this.t = t;
		t.register(this, TChannel.SENSOR);
		this.cf = cf;
	}

	public MockSensor(String name, Transducer t, ConveyorFamily cf,
			ConveyorFamily pcf) {
		this.name = name;
		this.t = t;
		t.register(this, TChannel.SENSOR);
		this.cf = cf;
		this.pcf = pcf;
	}

	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		log.add(new LoggedEvent("I know that there is glass incoming"));
	}

	@Override
	public void msgIAmOccupied() {

		log.add(new LoggedEvent("I know that popup is occupied"));
	}

	@Override
	public void msgStopSendingGlassToConveyor() {

		log.add(new LoggedEvent("I know that I should stop sending glass "));
	}

	@Override
	public void msgGlassIsWaiting(Conveyor conveyor) {

		log.add(new LoggedEvent(
				"I know that there is glass waiting on the conveyor "
						+ " from agent " + conveyor));
	}

	@Override
	public void msgCanISendGlass() {

		log.add(new LoggedEvent(
				"I know that the popup from previous conveyor family are going to send glass "));
	}

	@Override
	public void msgHereIsGlass(Conveyor conveyor, Glass glass) {

		log.add(new LoggedEvent("Sensor2 received glass " + glass.getName()
				+ " from agent " + conveyor));
	}

	@Override
	public void msgIReceivedGlass(Conveyor conveyor, Glass glass) {

		log.add(new LoggedEvent("msgIReceivedGlass, Sensor2 received glass "
				+ glass.getName() + " from agent " + conveyor));
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (event == TEvent.SENSOR_GUI_PRESSED) {
			log.add(new LoggedEvent("Sensor received event " + event
					+ " from channel " + channel + " with arguement: "
					+ args[0]));
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void msgIAmEmpty() {

		log.add(new LoggedEvent("I know that popup is empty "));
	}

	@Override
	public void msgIAmEmpty(Conveyor conveyor) {
		log.add(new LoggedEvent("I know that conveyor is empty "));

	}

	@Override
	public void msgIAmOccupied(Conveyor conveyor) {
		log.add(new LoggedEvent("I know that conveyor is occupied "));

	}

	@Override
	public void msgCanISendGlass(Conveyor conveyor) {
		log.add(new LoggedEvent("I know that conveyor is going to send glass "));
		
	}

}
