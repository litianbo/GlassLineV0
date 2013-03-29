package mocks;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class MockAnimation implements TReceiver{
	Transducer t;
	public MockAnimation(Transducer t){
		this.t = t;
		t.register(this, TChannel.CUTTER);
		t.register(this, TChannel.SENSOR);
		t.register(this, TChannel.BREAKOUT);
		t.register(this, TChannel.MANUAL_BREAKOUT);
		t.register(this, TChannel.POPUP);
		t.register(this, TChannel.DRILL);
		t.register(this, TChannel.UV_LAMP);
		t.register(this, TChannel.WASHER);
		t.register(this, TChannel.OVEN);
		t.register(this, TChannel.PAINTER);
		t.register(this, TChannel.TRUCK);
	}
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// add anything you want to print here
		
	}
}
