package engine.util;

import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.agent.PopupAgent;
import engine.agent.SensorAgent;
import interfaces.Conveyor;
import interfaces.ConveyorFamilyInterface;
import interfaces.Popup;
import interfaces.Sensor;
import transducer.Transducer;

/**
 * The implementation of conveyor family idea for this Factory Project
 * @author lenovo
 *
 */
public class ConveyorFamily implements ConveyorFamilyInterface{
	//transducer for the communication in GUI
	Transducer t = new Transducer();
	/**
	 * back/front end sensor, popup, back/front conveyor
	 */
	public Sensor sensor1 = new SensorAgent("Sensor1",t,this);
	public Conveyor conveyor1 = new ConveyorAgent("Conveyor1",t,this);
	public Popup popup = new PopupAgent("Default popup",t,this);
	public Conveyor conveyor2 = new ConveyorAgent("Conveyor2",t,this);
	public Sensor sensor2 = new SensorAgent("Sensor2",t,this);
	
	
	//below is for testing purpose
	public void setPopup(Popup popup){
		this.popup = popup;
	}
	public void setSensor1(Sensor sensor1){
		this.sensor1 = sensor1;
	}
	public void setSensor2(Sensor sensor2){
		this.sensor2 = sensor2;
	}
	public void setConveyor1(Conveyor conveyor){
		this.conveyor1 = conveyor;
	}
	@Override
	public void msgHereIsGlass(Popup popup, Glass glass) {
		// give glass to sensor1 on next conveyor family 
		sensor1.msgHereIsGlass(popup, glass);
		
		
	}
	@Override
	public void msgStartConveyor() {
		conveyor1.msgStart();
		
	}
	@Override
	public void msgStopConveyor() {
		conveyor2.msgStop();
		
	}
}
