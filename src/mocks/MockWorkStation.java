package mocks;

import engine.agent.Glass;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import interfaces.Popup;
import interfaces.WorkStation;

public class MockWorkStation implements WorkStation, TReceiver {
	Transducer t;
	String name;
	public EventLog log = new EventLog();

	public MockWorkStation(String name, Transducer t) {
		this.t = t;
		this.name = name;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {

		log.add(new LoggedEvent(channel + " fired an event: " + event
				+ " with argument: " + args[0]));
	}

	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		log.add(new LoggedEvent("I know that there is glass coming to "
				+ this.name));

	}

	@Override
	public void msgIAmLowered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmRaised() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
