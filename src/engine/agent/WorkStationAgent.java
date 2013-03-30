package engine.agent;

import java.util.Timer;
import java.util.TimerTask;

import interfaces.Popup;
import interfaces.WorkStation;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * base agent for the workstation
 * 
 * @author lenovo
 * 
 */
public class WorkStationAgent extends Agent implements WorkStation {
	// data:
	// each workstation can hold one glass
	Glass glass;
	// popup
	Popup popup;
	boolean popupLowered = false;
	Timer timer = new Timer();

	/**
	 * constructor
	 * 
	 * @param name
	 * @param t
	 */
	public WorkStationAgent(String name, Transducer t) {
		super(name, t);
		transducer.register(this, TChannel.WASHER);
	}

	// messages:
	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		this.glass = glass;
		this.popup = popup;
	}

	public void msgIAmLowered() {
		popupLowered = true;
		stateChanged();
	}

	public void msgIAmRaised() {
		popupLowered = false;
		stateChanged();
	}

	// scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		if (glass != null) {
			processGlass();
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.WASHER
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {
			transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_DO_ACTION,
					args);
		}
		if (channel == TChannel.WASHER
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			transducer.fireEvent(TChannel.WASHER,
					TEvent.WORKSTATION_RELEASE_GLASS, args);
		}
		if (channel == TChannel.WASHER
				&& event == TEvent.WORKSTATION_RELEASE_GLASS) {
			timer.schedule(new TimerTask() {
				public void run() {
					
				}
			}, 200);
			transducer.fireEvent(TChannel.WASHER,
					TEvent.WORKSTATION_RELEASE_FINISHED, args);
		}
	}

	// methods:
	public void processGlass() {
		// fire event to notify popup, this station is occupied;
		Object[] args = new Object[1];
		args[0] = new Long(0);
		transducer.fireEvent(TChannel.POPUP,
				TEvent.WORKSTATION_GUI_ACTION_FINISHED, args);
		if (name.contains("Top")) {// top workstation

		}
	}
	public String getName(){
		return name;
	}
}
