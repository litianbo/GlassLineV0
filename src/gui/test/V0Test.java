package gui.test;

import org.junit.Test;

import mocks.MockPopup;
import mocks.MockSensor;
import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.agent.PopupAgent;
import engine.agent.Recipe;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import junit.framework.TestCase;

public class V0Test extends TestCase {
	/**
	 * test the conveyor in the front of sensor, the main goal for the front
	 * conveyor is to pass the glass to the sensor if it is empty, or wait until
	 * it is empty
	 */
	public void testConveyor1() {
		// create a transducer
		Transducer transducer = new Transducer();
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// create a conveyor agent for testing purpose
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor1", transducer,
				conveyorFamily1);
		conveyorFamily1.setConveyor1(conveyor);
		// create a popupagent for testing purpose

		// create a mock sensor
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily1);
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);

		// replace the agents with the mocks for the testing

		conveyorFamily1.setSensor1(sensor1);
		conveyorFamily1.setSensor2(sensor2);
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		// now, give the conveyor the right state in order to test,
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass1"));
		// now, neither sensor1 or sensor2 should still have message right now,
		// so, use 'equal' to check it
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());

		// after I run the scheduler, sensor2 will receive the msg:
		// msgHereIsGlass;
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("Sensor2 received glass"));
		// now we want to prove that when the sensor is currently working, the
		// conveyor stop giving the glass to the sensor

		// suppose the sensor sent a msgIAmOccupied to conveyor (Test the
		// working
		// of sensor later, now, just assume the sensor work and test conveyor)
		conveyor.msgIAmOccupied();
		// give a new glass named glass2
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass2"));
		// I need to make the state of conveyor to WAITING_FOR_POP by calling
		// the scheudler
		conveyor.pickAndExecuteAnAction();
		// call it again to send the msgGlassIsWaiting to the sensor
		conveyor.pickAndExecuteAnAction();
		// now the mocksensor received the msgGlassIsWaiting, let's test it!!!
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that there is "));
		// Test for interaction with front end sensor
		// if conveyor has more than three glass?, does front end sensor stop
		// sending glass?
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass3"));
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass4"));
		// now, run scheduler to see what happoen?
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that I should stop sending glass"));
		// TODO: More on conveyor implementation~!!!

		// sensor.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		// popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
		// args);
		// check if log gets the event fired from above;
		/*
		 * assertTrue(
		 * "Mock sensor should have received the event after the fire. Event log: "
		 * + sensor.log.toString(), sensor.log .containsString(
		 * "Sensor received event SENSOR_GUI_PRESSED from channel SENSOR with arguement: 0"
		 * ));
		 */

	}

	/**
	 * TODO: test the conveyor behind the popup, nothing to do now, let's see
	 * what Prof. say in class
	 */
	public void testConveyor2() {

	}

	/**
	 * TODO: test the popup
	 */
	public void testPopup() {// popup is at the end of conveyor
		// create a transducer
		Transducer transducer = new Transducer();
		// create glass
		Glass glass = new Glass(new Recipe(), "Glass1");
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// create a popup agent
		PopupAgent popup = new PopupAgent("Popup1", transducer,
				conveyorFamily1, conveyorFamily2);
		// create mocks
		// this sensor is on the front of the popup
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);
		// this sensor is behind the popup (belong to the next family)
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);
		// set mocks for the families
		conveyorFamily1.setSensor2(sensor2);
		conveyorFamily2.setSensor1(sensor1);
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		// suppose sensor2 sending glass to popup, then popup will receive
		// msgHereIsGlass, I will test sensors later, assume it works properly
		// right now
		popup.msgHereIsGlass(sensor2, glass);
		// now, can popup pass the glass to next conveyor family successfully?
		// run scheduler
		popup.pickAndExecuteAnAction();
		// now, state is changed to working, popup is ready to raise!
		popup.pickAndExecuteAnAction();
		// now, popup is raised, and sensor2(in this conveyor) should receive
		// msgIAmOccupied
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		// since the time of working on popup is totally rely on animation,
		// need to use eventfired here to set the correct state
		Object[] args = new Object[1];
		args[0] = new Long(0);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
				args);
		// now run scheduler
		popup.pickAndExecuteAnAction();
		// popup sent msgCanISendGlass to next conveyor family
		// test it!
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		// now, next conveyor family received incoming glass message
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmEmpty back to the previous popup agent
		popup.msgIAmEmpty();
		//set the correct stage, then run the scheduler
		popup.pickAndExecuteAnAction();
		//now, the glass should push to next conveyor family, Test it!!!
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		//TODO: add more tests here, such as sensor 1 is occupied?
		
	}

	/**
	 * TODO: test the sensors
	 */
	public void testSensor() {

	}
}
