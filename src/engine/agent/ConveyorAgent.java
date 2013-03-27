package engine.agent;

import java.util.*;

import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import interfaces.Conveyor;
import interfaces.Sensor;

public class ConveyorAgent extends Agent implements Conveyor {
	private enum ConveyorState {
		STOPED,GLASS_ARRIVED, WAITING_FOR_SENSOR, SENDING_GLASS_TO_SENSOR, FRONT_SENSOR_CAN_SEND_GLASS, NULL
	}

	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass>());
	List<Glass> waitingGlasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	ConveyorState state = ConveyorState.NULL;
	ConveyorFamily cf;
	boolean backSensorOccupied = false;
	boolean stopConveyor = false;
	/**
	 * constructor for conveyor agent
	 * 
	 * @param name
	 * @param t
	 */
	public ConveyorAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		this.cf = cf;
		// register at conveyor and react
		transducer.register(this, TChannel.CONVEYOR);
	}

	// messages:
	public void msgStart(){
		state = ConveyorState.NULL;
		stateChanged();
	}
	/**
	 * conveyor needs to stop
	 */
	public void msgStop(){//TODO: do something here
		state = ConveyorState.STOPED;
		stateChanged();
	}
	/**
	 * sent from front end sensor
	 */
	@Override
	public void msgCanISendGlass(Sensor sensor, Glass glass) {
		if (glasses.size() < 4) {
			// not exceed 3 glasses in one conveyor, notify front end sensor to
			// send glass
			state = ConveyorState.FRONT_SENSOR_CAN_SEND_GLASS;

		}

		stateChanged();

	}

	/**
	 * sent from front end sensor
	 */
	@Override
	public void msgHereIsGlass(Sensor sensr,Glass glass) {
		// TODO Auto-generated method stub
		glasses.add(glass);
		state = ConveyorState.GLASS_ARRIVED;
		stateChanged();
	}

	/**
	 * sent from the sensor that it is working on a glass;
	 */
	@Override
	public void msgIAmOccupied() {
		stopConveyor = true;
		backSensorOccupied = true;
		stateChanged();
	}

	/**
	 * sent from the sensor that it is free;
	 */
	@Override
	public void msgIAmEmpty() {
		backSensorOccupied = false;
		stopConveyor = false;
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (glasses.size() >= 4) {
			print("Too many glasses at this conveyor!!!, stop " + this);
			// tell front end sensor stop sending glass
			cf.sensor1.msgStopSendingGlassToConveyor();
			stopConveyor();
			return true;
		}
		// only if the sensor is not occupied;
		if (state == ConveyorState.GLASS_ARRIVED && !backSensorOccupied
				|| name == "Conveyor2") {
			print("glass " + glasses.get(0).getName() + " arrived to conveyor "
					+ this + " it will be passing to sensor now");
			// add more implementation here further
			glassArrived(glasses.remove(0));
			return true;
		}
		if (state == ConveyorState.GLASS_ARRIVED && backSensorOccupied) {
			// ToDo: enhance the throughput here
			state = ConveyorState.WAITING_FOR_SENSOR;
			return true;
		}
		if (state == ConveyorState.WAITING_FOR_SENSOR
				&& waitingGlasses.size() == 0) {
			notifySensorGlassIsWaiting();
			return true;
		}
		if (state == ConveyorState.WAITING_FOR_SENSOR
				&& waitingGlasses.size() > 0 && !backSensorOccupied) {
			pushGlassToSensor(waitingGlasses.remove(0));
			notifySensorToSendGlass();
			return true;
		}
		if(state == ConveyorState.FRONT_SENSOR_CAN_SEND_GLASS){
			notifySensorToSendGlass();
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	// methods:
	public void notifySensorToSendGlass(){
		cf.sensor1.msgIAmEmpty(this);
		stateChanged();
	}
	
	public void pushGlassToSensor(Glass glass) {
		cf.sensor2.msgHereIsGlass(this, glass);
		stateChanged();
	}

	/**
	 * notify msg to sensor and put the glass to waiting list
	 */
	public void notifySensorGlassIsWaiting() {
		print("glass " + glasses.get(0).getName()
				+ " is waiting on the conveyor " + this);
		cf.sensor2.msgGlassIsWaiting(this);
		waitingGlasses.add(glasses.remove(0));
		stateChanged();
	}

	public void stopConveyor() {

	}

	public void glassArrived(Glass glass) {
		// if this is front end conveyor, pass the glass to the sensor if
		// possible
		if (name == "Conveyor1") {
			cf.sensor2.msgHereIsGlass(this, glass);
		} else if (name == "Conveyor2") {// do nothing now.
			cf.sensor2.msgHereIsGlass(this, glass);
		}
		stateChanged();
	}

	public String getName() {
		return name;
	}

	@Override
	public void msgGlassIsWaiting(Sensor sensor) {
		// TODO Auto-generated method stub

	}

}
