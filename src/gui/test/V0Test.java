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
	 * test the conveyor in the front of popup, the main goal for the front
	 * conveyor is to pass the glass to the popup if it is empty, or wait until
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
		// create a popupagent for testing purpose
		MockPopup popup = new MockPopup("Popup2", transducer, conveyorFamily1);
		// create a mock sensor
		MockSensor sensor = new MockSensor("Sensor1", transducer,
				conveyorFamily1);
		PopupAgent popupTest = new PopupAgent("PopupTest", transducer,
				conveyorFamily1);
		// replace the agents with the mocks for the testing
		conveyorFamily1.setPopup(popup);
		conveyorFamily1.setSensor1(sensor);
		// This will check that you're not messaging the customer in the
		// waiter's message reception.
		assertEquals(
				"Mock popup should have an empty event log now. Instead, the mock popup's event log reads: "
						+ popup.log.toString(), 0, popup.log.size());
		// now, give the conveyor the right state in order to test,
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass1"));
		// This will check that you're not messaging the customer in the
		// waiter's message reception.
		assertEquals(
				"Mock popup should have an empty event log now. Instead, the mock popup's event log reads: "
						+ popup.log.toString(), 0, popup.log.size());
		// after I run the scheduler, popup will receive the msg:
		// msgHereIsGlass;
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("Popup received glass"));
		// now we want to prove that when the popup is currently working, the
		// conveyor stop giving the glass to the popup

		// suppose the popup sent a msgIAmOccupied to conveyor (Test the working
		// of popup later, now, just assume the popup work and test conveyor)
		conveyor.msgIAmOccupied();
		// give a new glass named glass2
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass2"));
		// I need to make the state of conveyor to WAITING_FOR_POP by calling
		// the scheudler
		conveyor.pickAndExecuteAnAction();
		// call it again to send the msgGlassIsWaiting to the popup
		conveyor.pickAndExecuteAnAction();
		// now the mockpopup received the msgGlassIsWaiting, let's test it!!!
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log
						.containsString("Popup received message from conveyor that there is glass waiting on the conveoyor "));
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
	 * TODO: test the conveyor behind the popup
	 */
	public void testConveyor2() {

	}

	/**
	 * TODO: test the popup
	 */
	public void testPopup() {

	}

	/**
	 * TODO: test the sensors
	 */
	public void testSensor() {

	}
}
