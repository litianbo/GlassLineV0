package mocks;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class MockAnimation implements TReceiver {
	Transducer t;
	public EventLog log = new EventLog();

	public MockAnimation(Transducer t) {
		this.t = t;
		this.t.register(this, TChannel.CONVEYOR);
		this.t.register(this, TChannel.CUTTER);
		this.t.register(this, TChannel.SENSOR);
		this.t.register(this, TChannel.BREAKOUT);
		this.t.register(this, TChannel.MANUAL_BREAKOUT);
		this.t.register(this, TChannel.POPUP);
		this.t.register(this, TChannel.DRILL);
		this.t.register(this, TChannel.UV_LAMP);
		this.t.register(this, TChannel.WASHER);
		this.t.register(this, TChannel.OVEN);
		this.t.register(this, TChannel.PAINTER);
		this.t.register(this, TChannel.TRUCK);
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// add anything you want to print here

		log.add(new LoggedEvent("received " + channel + " " + event
				+ " with argument: " + args[0]));
	}
}
