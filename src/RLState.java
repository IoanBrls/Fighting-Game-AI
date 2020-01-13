import aiinterface.CommandCenter;
import enumerate.Action;
import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import enumerate.State;

import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;


public class RLState {
	
	
	//Distance --> 0 = Very Close / 1 = Close / 2 = Middle / 3 = Far / 4 = Very far
	public int distance;
	//Possible States: AIR,STAND,CROUCH,DOWN
	public State myState;
	//hP --> 0 = Low / 1 = Medium / 2 = High
	public int myHp;
	//energy --> 0 = Low / 1 = Medium / 2 = High
	public int myEnergy;
	//Possible States: AIR,STAND,CROUCH,DOWN
	public State oppState;
	//hP --> 0 = Low / 1 = Medium / 2 = High
	public int oppHp;
	//energy --> 0 = Low / 1 = Medium / 2 = High
	public int oppEnergy;
	

	public RLState (int distance, State myState, int myHp, int myEnergy, 
					State oppState, int oppHp, int oppEnergy ) {
		this.distance = distance;
		this.myState = myState;
		this.myHp = myHp;
		this.myEnergy = myEnergy;
		this.oppState = oppState;
		this.oppHp = oppHp;
		this.oppEnergy = oppEnergy;
	}
	

	public void print() {
		System.out.println(distance + "|" + myState.toString() + "|" + myHp + "|"
				+ myEnergy + "|" + oppState.toString() + "|" + oppHp + "|" + oppEnergy);
	}
}
