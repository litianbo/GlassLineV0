package engine.util;

import engine.agent.ConveyorAgent;
import engine.agent.PopupAgent;
import engine.agent.SensorAgent;
import interfaces.Conveyor;
import interfaces.Popup;
import interfaces.Sensor;
import transducer.Transducer;

/**
 * The implementation of conveyor family idea for this Factory Project
 * @author lenovo
 *
 */
public class ConveyorFamily {
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
}
