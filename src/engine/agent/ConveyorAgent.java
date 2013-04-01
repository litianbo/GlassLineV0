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
		STOPED, GLASS_ARRIVED, WAITING_FOR_SENSOR, BACK_SENSOR_CAN_SEND_GLASS, SENDING_GLASS_TO_SENSOR, FRONT_SENSOR_CAN_SEND_GLASS, NULL
	}

	List<Glass> glasses = Collections.synchronizedList(new ArrayList<Glass>());
	List<Glass> waitingGlasses = Collections
			.synchronizedList(new ArrayList<Glass>());
	ConveyorState state = ConveyorState.NULL;
	ConveyorFamily cf;
	boolean backSensorOccupied = false;
	boolean stopConveyor = false;
	boolean running = false;

	/**
	 * constructor for conveyor agent
	 * 
	 * @param name
	 * @param t
	 */
	public ConveyorAgent(String name, Transducer t, ConveyorFamily cf) {
		super(name, t);
		this.cf = cf;
		transducer.register(this, TChannel.SENSOR);
		// register at conveyor and react
		transducer.register(this, TChannel.CONVEYOR);
	}

	// messages:
	public void msgStart() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		print("conveyor started!");
		if (waitingGlasses.size() > 0)
			state = ConveyorState.FRONT_SENSOR_CAN_SEND_GLASS;
		else if (glasses.size() > 0)
			state = ConveyorState.BACK_SENSOR_CAN_SEND_GLASS;
		else
			// if no glass is currently on the conveyor, don't start it until
			// one is coming to save power
			state = ConveyorState.NULL;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		running = true;
		stopConveyor = false;
		stateChanged();
	}

	/**
	 * conveyor needs to stop
	 */
	public void msgStop() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		state = ConveyorState.STOPED;

		running = false;

		stateChanged();
	}

	/**
	 * sent from front front sensor
	 */
	@Override
	public void msgCanISendGlass(Sensor sensor, Glass glass) {
		if (!stopConveyor) {
			// if the conveyor is not stop, continue sent glass to conveyor
			state = ConveyorState.FRONT_SENSOR_CAN_SEND_GLASS;

		} else {
			print("conveyor stoped due to operation");
		}
		stateChanged();

	}

	/**
	 * sent from front front sensor
	 */
	@Override
	public void msgHereIsGlass(Sensor sensor, Glass glass) {
		print("Recieved glass from: " + sensor.getName());
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

		if (glasses.size() > 0 && !backSensorOccupied)// if there is glass exist
														// in the conveyor, send
			// it to the back end sensor
			state = ConveyorState.SENDING_GLASS_TO_SENSOR;
		else if (backSensorOccupied) {
			Object[] args = new Object[1];
			args[0] = new Long(0);
			backSensorOccupied = false;
			stopConveyor = false;
			transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START,
					args);
			state = ConveyorState.GLASS_ARRIVED;
		}
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (state != ConveyorState.STOPED) {

			// only if the sensor is not occupied;
			if (state == ConveyorState.GLASS_ARRIVED && !backSensorOccupied
					|| name == "Conveyor2") {
				print("glass " + glasses.get(0).getName()
						+ " arrived to conveyor " + this
						+ " it will be passing to sensor now");
				glassArrived(glasses.get(0));
				return true;
			}
			if (state == ConveyorState.GLASS_ARRIVED && backSensorOccupied) {
				// ToDo: enhance the throughput here
				state = ConveyorState.WAITING_FOR_SENSOR;
				return true;
			}
			if (state == ConveyorState.WAITING_FOR_SENSOR
					&& waitingGlasses.size() == 0) {
				Object[] args = new Object[1];
				args[0] = new Long(0);
				transducer.fireEvent(TChannel.CONVEYOR,
						TEvent.CONVEYOR_DO_STOP, args);
				notifySensorGlassIsWaiting();
				return true;
			}
			if (state == ConveyorState.WAITING_FOR_SENSOR
					&& waitingGlasses.size() > 0 && !backSensorOccupied) {

				pushGlassToSensor(waitingGlasses.remove(0));
				notifySensorToSendGlass();
				return true;
			}
			if (state == ConveyorState.FRONT_SENSOR_CAN_SEND_GLASS) {
				// print("TEST");

				notifySensorToSendGlass();
				
				return true;
			}
			if(state == ConveyorState.BACK_SENSOR_CAN_SEND_GLASS){
				if (glasses.size() > 0) {
					
					pushGlassToSensor(glasses.remove(0));
				}
				return true;
			}
			if (state == ConveyorState.SENDING_GLASS_TO_SENSOR) {
				sendGlassToSensor();
				return true;
			}
		} else {
			stopConveyor();
			return true;
		}
		return false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	// methods:
	public void sendGlassToSensor() {
		print("Pushing glass to back end sensor");

		cf.sensor2.msgHereIsGlass(this, glasses.remove(0));
		if (glasses.size() > 0)
			state = ConveyorState.GLASS_ARRIVED;
		else
			state = ConveyorState.NULL;
		stateChanged();
	}

	public void notifySensorToSendGlass() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		if (!running) {
			transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START,
					args);
			running = true;
		}

		state = ConveyorState.NULL;
		cf.sensor1.msgIAmEmpty(this);
		stateChanged();
	}

	public void pushGlassToSensor(Glass glass) {
		print("Pushing glass to back end sensor");
		Object[] args = new Object[1];
		args[0] = new Long(0);
		// transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START,
		// args);
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
		waitingGlasses.add(glasses.get(0));
		stateChanged();
	}

	public void stopConveyor() {
		// print("conveyor stoped");
		Object[] args = new Object[1];
		args[0] = new Long(0);
		stopConveyor = true;
		cf.sensor1.msgIAmOccupied(this);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		stateChanged();
	}

	public void glassArrived(Glass glass) {

		cf.sensor2.msgCanISendGlass(this);

		stateChanged();
	}

	public String getName() {
		return name;
	}

}
