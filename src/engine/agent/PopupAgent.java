package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;
import interfaces.WorkStation;
import transducer.*;

public class PopupAgent extends Agent implements Popup {
	// Data:
	ConveyorFamily cf, cf2;
	// top and bottom workstation
	WorkStation top, bot;
	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass>());
	List<Glass> waitingGlasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	List<Glass> doneGlasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	List<Glass> processGlasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	String name;
	boolean raise = false;
	boolean sensorOccupied = false;
	PopupState popupState = PopupState.NULL;
	WorkStationState wState = WorkStationState.EMPTY;
	Timer timer = new Timer();

	enum PopupState {
		NULL, WORKING_ON_GLASS, RAISED, GLASS_ARRIVED, SENDING_GLASS_TO_SENSOR, SENSOR_EMPTY, SENSOR_OCCUPIED
	}

	enum WorkStationState {
		NULL, TOP_WORKSTATION_OCCUPIED, BOTH_WORKSTATION_OCCUPIED, BOT_WORKSTATION_OCCUPIED, EMPTY
	}

	public PopupAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		this.cf = cf;
		// transducer.register(this, TChannel.);
		transducer.register(this, TChannel.POPUP);

	}

	/**
	 * 2nd constructor for PopupAgent, I think the popup is at the end of each
	 * conveyor, hence, it has the responsibility to connect with the next
	 * conveyor
	 * 
	 * @param name
	 * @param t
	 * @param cf1
	 * @param cf2
	 */
	public PopupAgent(String name, Transducer t, ConveyorFamily cf1,
			ConveyorFamily cf2) {

		super(name, t);
		this.cf = cf1;
		this.cf2 = cf2;
		// transducer.register(this, TChannel.ALL_AGENTS);
		transducer.register(this, TChannel.POPUP);
	}

	/**
	 * 3rd consturctor for the workstation
	 * 
	 * @param name
	 * @param t
	 * @param cf1
	 * @param cf2
	 * @param top
	 * @param bot
	 */
	public PopupAgent(String name, Transducer t, ConveyorFamily cf1,
			ConveyorFamily cf2, WorkStation top, WorkStation bot) {
		super(name, t);
		this.cf = cf1;
		this.cf2 = cf2;
		transducer.register(this, TChannel.POPUP);
		this.top = top;
		this.bot = bot;
	}

	// message
	/**
	 * sent from workstation, glass is done
	 */
	@Override
	public void msgGlassDone(WorkStation work, Glass glass) {
		if (work.getName() == "Top"
				&& wState == WorkStationState.BOTH_WORKSTATION_OCCUPIED) {
			wState = WorkStationState.BOT_WORKSTATION_OCCUPIED;
		} else if (work.getName() == "Top"
				&& wState == WorkStationState.TOP_WORKSTATION_OCCUPIED) {
			wState = WorkStationState.EMPTY;
		} else if (work.getName() == "Bot"
				&&wState == WorkStationState.BOTH_WORKSTATION_OCCUPIED) {
			wState = WorkStationState.TOP_WORKSTATION_OCCUPIED;
		}else if (work.getName() == "Bot"
				&&wState == WorkStationState.BOT_WORKSTATION_OCCUPIED) {
			wState = WorkStationState.EMPTY;
		}
		else {
			print("wrong workstate!!!!!!!!!!!!!!!");
		}
		doneGlasses.add(glass);
		glasses.remove(0);
		stateChanged();

	}

	/**
	 * before sensor pass glass to popup, he needs to check with popup if there
	 * is space for it
	 */
	public void msgCanISendGlass(Sensor sensor, Glass glass) {
		waitingGlasses.add(glass);
		print("back end sensor want to send glass to me from "
				+ sensor.getName());
		stateChanged();
	}

	/**
	 * next conveyor family sensor is occupied
	 */
	@Override
	public void msgIAmOccupied(Sensor sensor) {
		// TODO Auto-generated method stub
		print("front end sensor is occupied, don't pass glass from "
				+ sensor.getName());
		sensorOccupied = true;
		popupState = PopupState.SENSOR_OCCUPIED;

		stateChanged();
	}

	/**
	 * next conveyor family is empty, glass can pass
	 */
	@Override
	public void msgIAmEmpty(Sensor sensor) {
		// TODO Auto-generated method stub
		print("next conveyor family is empty, pass glass from "
				+ sensor.getName());
		sensorOccupied = false;
		popupState = PopupState.SENSOR_EMPTY;
		stateChanged();
	}

	/**
	 * message that receive glass from the sensor or another conveyer family
	 */
	@Override
	public void msgHereIsGlass(Sensor sensor, Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		print("received message from " + sensor.getName() + " to work on "
				+ glass.getName());
		popupState = PopupState.GLASS_ARRIVED;
		stateChanged();
	}

	// scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		if (popupState == PopupState.GLASS_ARRIVED
				&& glasses.get(glasses.size() - 1).recipe.getNeedWashing()) {

			glassArrived();
			return true;
		}
		if (popupState == PopupState.GLASS_ARRIVED
				&& !glasses.get(glasses.size() - 1).recipe.getNeedWashing()) {

			passToNextConveyorFamily();
			return true;
		}
		if (popupState == PopupState.WORKING_ON_GLASS && raise
				&& glasses.size() > 0) {

			DoRaisePopup();
			return true;
		}

		if (popupState == PopupState.SENDING_GLASS_TO_SENSOR) {// sent by GUI
																// popup
			notifyNextFamily();
			return true;
		}
		if (popupState == PopupState.SENSOR_EMPTY) {
			// next conveyor family is empty in first sensor
			passGlassToNextFamily();
			return true;
		}
		if (popupState == PopupState.SENSOR_OCCUPIED) {
			popupState = PopupState.NULL;// here just do whatever, because we
											// are doing something usefully as
											// well as waiting for the sensor
			return true;
		}
		if (waitingGlasses.size() > 0 && popupState == PopupState.RAISED
				&& wState != WorkStationState.BOTH_WORKSTATION_OCCUPIED) {
			// when popup is raised, and there is another glass coming in!!!
			doLowerPopup();
			return true;
		}
		if (waitingGlasses.size() > 0 && popupState != PopupState.RAISED
				|| waitingGlasses.size() > 0 && popupState == PopupState.RAISED
				&& wState == WorkStationState.BOTH_WORKSTATION_OCCUPIED) {// when
			// popup
			// is
			// lowered || the pop is raised and both workstation occupied
			checkStationState();
			return true;
		}
		/*
		 * if (doneGlasses.size() > 0 && popupState == PopupState.RAISED) {
		 * passGlassToNextFamily(); return true; }
		 */
		return false;
	}

	@Override
	public synchronized void eventFired(TChannel channel, TEvent event,
			Object[] args) {
		// TODO Auto-generated method stub
		// do something when the GUI fire the moved up event, here move this
		// down, fix this later
		if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_MOVED_UP) {
			transducer.fireEvent(TChannel.WASHER,
					TEvent.WORKSTATION_DO_LOAD_GLASS, args);
			// transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN,
			// args);
		}
		// Fired when the popup should push its glass onto the next conveyor
		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_MOVED_DOWN) {
			/*
			 * transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS,
			 * args);
			 */
		} else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_LOAD_FINISHED) {// if load
																// finished, do
																// move up
			// transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP,
			// args);
		}
		// do something when release finished, fire an event to sensor
		else if (channel == TChannel.POPUP
				&& event == TEvent.WORKSTATION_RELEASE_FINISHED) {
			popupState = PopupState.SENDING_GLASS_TO_SENSOR;
			if (wState == WorkStationState.BOTH_WORKSTATION_OCCUPIED)
				wState = WorkStationState.BOT_WORKSTATION_OCCUPIED;
			else if (wState == WorkStationState.BOT_WORKSTATION_OCCUPIED
					|| wState == WorkStationState.TOP_WORKSTATION_OCCUPIED)
				wState = WorkStationState.EMPTY;
			// transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED,
			// args);
		} else if (channel == TChannel.POPUP
				&& event == TEvent.WORKSTATION_DO_LOAD_GLASS) {
			// if workstation starts to load glass, change the popup state
			if (wState == WorkStationState.TOP_WORKSTATION_OCCUPIED
					&& wState == WorkStationState.BOT_WORKSTATION_OCCUPIED)
				wState = WorkStationState.BOTH_WORKSTATION_OCCUPIED;
			else if (wState != WorkStationState.TOP_WORKSTATION_OCCUPIED) {
				wState = WorkStationState.TOP_WORKSTATION_OCCUPIED;
			} else {
				if (wState == WorkStationState.TOP_WORKSTATION_OCCUPIED)
					wState = WorkStationState.BOT_WORKSTATION_OCCUPIED;
				else
					wState = WorkStationState.TOP_WORKSTATION_OCCUPIED;
			}

		}
		if (channel == TChannel.POPUP
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			if (!raise)
				transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP,
						args);
			transducer.fireEvent(TChannel.POPUP,
					TEvent.WORKSTATION_RELEASE_GLASS, args);

		}
		if (channel == TChannel.POPUP
				&& event == TEvent.WORKSTATION_RELEASE_GLASS) {
			// add some timer here
			transducer.fireEvent(TChannel.WASHER,
					TEvent.WORKSTATION_RELEASE_FINISHED, args);
			transducer.fireEvent(TChannel.POPUP,
					TEvent.WORKSTATION_RELEASE_FINISHED, args);
		}
		if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_RELEASE_FINISHED) {
			// messgae
		}
		stateChanged();
	}

	// methods:

	public void passToNextConveyorFamily() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		popupState = PopupState.SENDING_GLASS_TO_SENSOR;
		if (raise)
			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN,
					args);

		stateChanged();
	}

	public void doLowerPopup() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		popupState = PopupState.WORKING_ON_GLASS;
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
		// I checked in the scheduler, if there bot one is empty, just send the
		// sensor the msgIAmEmpty()
		cf.sensor2.msgIAmEmpty();
		top.msgIAmLowered();
		bot.msgIAmLowered();
		raise = false;
		stateChanged();
	}

	public void checkStationState() {
		if (wState != WorkStationState.BOTH_WORKSTATION_OCCUPIED) {
			cf.sensor2.msgIAmEmpty();
			glasses.add(waitingGlasses.remove(0));
		} else {
			cf.sensor2.msgIAmOccupied();// tell sensor2 to wait
			waitingGlasses.remove(0);
		}
		stateChanged();
	}

	public void passGlassToNextFamily() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		if (doneGlasses.size() > 0)
			cf2.sensor1.msgHereIsGlass(this, doneGlasses.remove(0));
		else if (glasses.size() > 0)
			cf2.sensor1.msgHereIsGlass(this, glasses.remove(0));
		popupState = PopupState.NULL;
		stateChanged();
	}

	public void glassArrived() {
		// TODO add more implementation later, needs to check recipe here
		// if(glass.recipe.XXX)
		popupState = PopupState.WORKING_ON_GLASS;
		if (wState != WorkStationState.BOTH_WORKSTATION_OCCUPIED)// if there is
																	// at least
																	// one
																	// workstation
																	// empty,
			raise = true;
		Object[] args = new Object[1];
		args[0] = new Long(0);
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
		stateChanged();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	/**
	 * ToDo: add some detail
	 */
	public void DoRaisePopup() {
		if (wState == WorkStationState.EMPTY) {

			top.msgHereIsGlass(this, glasses.get(glasses.size() - 1));
			wState = WorkStationState.TOP_WORKSTATION_OCCUPIED;
		} else if (wState == WorkStationState.TOP_WORKSTATION_OCCUPIED) {
			bot.msgHereIsGlass(this, glasses.get(glasses.size() - 1));
			wState = WorkStationState.BOTH_WORKSTATION_OCCUPIED;
		} else if (wState == WorkStationState.BOT_WORKSTATION_OCCUPIED) {
			bot.msgHereIsGlass(this, glasses.get(glasses.size() - 1));
			wState = WorkStationState.BOTH_WORKSTATION_OCCUPIED;
		}
		cf.sensor2.msgIAmOccupied();
		popupState = PopupState.RAISED;
		top.msgIAmRaised();
		bot.msgIAmRaised();
		print("Now, the workstation is at: " + wState);
		stateChanged();
	}

	/**
	 * notify next conveyor family that I am going to send the glass to you!!
	 */
	public void notifyNextFamily() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		if (raise) {
			raise = false;
			transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN,
					args);
			// move down the popup is it is still up
		}

		print("I want to send glass to next conveyor family");
		cf2.sensor1.msgCanISendGlass();
		stateChanged();
	}

}
