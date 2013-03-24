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
	ConveyorFamily cf;
	String name;
	public EventLog log = new EventLog();
	public MockSensor(String name, Transducer t,ConveyorFamily cf) {
		this.name = name;
		// TODO Auto-generated constructor stub
		this.t = t;
		t.register(this, TChannel.SENSOR);
		this.cf = cf;
	}

	

	@Override
	public void msgHereIsGlass(Conveyor conveyor,Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIReceivedGlass(Conveyor conveyor, Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Sensor received glass "
				+ glass.getName() + " from agent " + conveyor));
	}

	@Override
	public void msgGlassPassed(Glass glass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
		if(event == TEvent.SENSOR_GUI_PRESSED){
		log.add(new LoggedEvent("Sensor received event " + event
				+ " from channel " + channel + " with arguement: " + args[0]));
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}
