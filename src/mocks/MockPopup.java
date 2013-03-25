package mocks;

import engine.agent.Glass;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import interfaces.Conveyor;
import interfaces.Popup;

public class MockPopup implements Popup, TReceiver {
	Transducer t;
	ConveyorFamily cf;
	String name;
	public EventLog log = new EventLog();

	public MockPopup(String name, Transducer transducer,
			ConveyorFamily conveyorFamily1) {
		this.name = name;
		// TODO Auto-generated constructor stub
		this.t = transducer;
		t.register(this, TChannel.SENSOR);
		this.cf = conveyorFamily1;
	}

	@Override
	public void msgHereIsGlass(Conveyor conveyor, Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Popup received glass " + glass.getName()
				+ " from agent " + conveyor));
	}

	@Override
	public void msgGlassIsWaiting(Conveyor conveyor) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent(
				"Popup received message from conveyor that there is glass waiting on the conveoyor  "
						+ " from agent " + conveyor));
	}

	@Override
	public void msgIAmOccupied() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgIAmEmpty() {
		// TODO Auto-generated method stub

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
