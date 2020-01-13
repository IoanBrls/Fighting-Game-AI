import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;

import aiinterface.AIInterface;
import aiinterface.CommandCenter;
import enumerate.Action;
import enumerate.State;
import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.MotionData;
import struct.ScreenData;

public class FightingAI implements AIInterface {

	
	private Key key;
	private CommandCenter commandCenter;
	private boolean playerNumber;
	private FrameData currentFrame;
	private FrameData prevFrame;
	
	private CharacterData myCharacter;
	private CharacterData oppCharacter;

	private Action[] actions;
	
	private Random RNGesus;
	
	private float a = 0.8f;
	private float g = 0.5f;
	private int statesCount = 8000;
	private State[] possibleStates = new State[] {State.STAND, State.AIR, State.CROUCH, State.DOWN};
	private RLState[] states;
	private float[][] Q_values;
	private int currentRLstate;
	private int prevRLstate;
	
	
	private boolean firstRun;
	
	private State myCurrentState;
	private State oppCurrentState;
	private int myCurrentHp;
	private int myCurrentHpState;
	private int myCurrentEnergy;
	private int myCurrentEnergyState;
	private int oppCurrentHp;
	private int oppCurrentHpState = -1;
	private int oppCurrentEnergy;
	private int oppCurrentEnergyState;
	private int currentDistanceX;
	private int currentDistance;
	
	
//	private int cumulativeReward;
//	private int rewardCount;
//	private PrintWriter writer;
	
	@Override
	public void close() {
//		try (PrintWriter writer2 = new PrintWriter(new File("data/aiData/QLearning_Load/Qvalues_Load_Final.csv"))) {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < statesCount; i++) {
//				for (int j = 0; j < actions.length; j++) {
//					sb.append(Q_values[i][j]);
//					sb.append(',');
//					if (j == actions.length - 1) {
//						sb.append('\n');
//					}
//				}
//			}
//
//			writer2.write(sb.toString());
//			writer2.close();
//			
//		}catch(FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		}
//		writer.close();
	}
 
	@Override
	public void getInformation(FrameData frameData, boolean isControl) {
		this.currentFrame = frameData;
		this.commandCenter.setFrameData(this.currentFrame, playerNumber);
		
		myCharacter = frameData.getCharacter(playerNumber);
		oppCharacter = frameData.getCharacter(!playerNumber);
		
	}

	
	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		
		this.playerNumber = playerNumber;
		 
		this.key = new Key();
		this.currentFrame = new FrameData();
		this.commandCenter = new CommandCenter();
		
		
		//The 10 possible actions thet the agent can perform.
		actions = new Action[] {Action.JUMP, Action.FORWARD_WALK, Action.FOR_JUMP, Action.BACK_STEP, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA
								, Action.STAND_FB};
		
		states = new RLState[statesCount]; 
		InitializeQLearning();
//		cumulativeReward = 0;
//		rewardCount = 0;
		RNGesus = new Random();
		
		firstRun = true;
		
//		try{
//			writer = new PrintWriter(new File("data/aiData/QLearning_Load/Reward_Load22.csv"));
//		}catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		}
		
		
		return 0;
		
	}
	
	
	//Function that creates all the possible states of the game and initializes the Q-table
	private void InitializeQLearning() {
		int countDistance = 0;
		int countMyState = 0;
		int countMyHp = 0;
		int countMyEnergy = 0;
		int countOppState = 0;
		int countOppHp = 0;
		int countOppEnergy = 0;
		
		for (int i = 0; i < statesCount; i++) {
			states[i] = new RLState(countDistance, possibleStates[countMyState], countMyHp, countMyEnergy
									, possibleStates[countOppState], countOppHp, countOppEnergy);
			countDistance++;
			if (i % 5 == 0  && i != 0) {
				countDistance = 0;
				countMyState++;
			}
			
			if (i % 20 == 0  && i != 0) {
				countMyState = 0;
				countMyHp++;
			}
			
			if(i % 100 == 0  && i != 0) {
				countMyHp = 0;
				countMyEnergy++;
			}
			
			if(i % 200 == 0  && i != 0) {
				countMyEnergy = 0;
				countOppState++;
			}
			
			if(i % 800 == 0  && i != 0) {
				countOppState = 0;
				countOppHp++;
			}
			
			if(i % 4000 == 0 && i != 0) {
				countOppHp = 0;
				countOppEnergy++;
			}
			
		}
		
		Q_values = new float[statesCount][actions.length];
//		
//		String line = "";
//		String split = ",";
//		int count = 0;
//
//		try (BufferedReader br = new BufferedReader(new FileReader("data/aiData/QLearning_Load/Qvalues_Load_Final.csv"))){
//			while((line = br.readLine()) != null) {
//				String[] temp = line.split(split);
//				for(int i = 0; i < temp.length; i++) {
//					Q_values[count][i] = Float.parseFloat(temp[i]);
//				}
//				count++;
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(count);
		
	}
	
	//Function that is used to find in which of the predefined states of the game the agent currently is.
	//This is done based on the data acquired from the current frame of the game.
	private int findState(int distance, State myState, int myHp, int myEnergy
							,State oppState, int oppHp, int oppEnergy) {
		
		for (int i = 0; i < statesCount; i++) {
			if (states[i].distance == distance && states[i].myState == myState && states[i].myHp == myHp
				&& states[i].myEnergy == myEnergy && states[i].oppState == oppState
				&& states[i].oppHp == oppHp && states[i].oppEnergy == oppEnergy) {
				return i;
			}
		} 
		return -1;
	}
	
	//The reward function of the Q-Leaning algorithm.
	//The agent gets a positive reward if it lands a hit on its opponent and a negative one when it gets hit.
	private int getReward(int initial, int next) {
		
		int reward = 0;
		int myHpDiff = currentFrame.getCharacter(playerNumber).getHp()
						- prevFrame.getCharacter(playerNumber).getHp();
		
		int oppHpDiff = currentFrame.getCharacter(!playerNumber).getHp()
						- prevFrame.getCharacter(!playerNumber).getHp();
		
		
		reward = myHpDiff - oppHpDiff;
		
//		cumulativeReward += reward;
//		rewardCount++;
//		if(rewardCount == 10) {
//			//System.out.println(currentFrame.getRemainingFramesNumber() + " " + cumulativeReward);
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(currentFrame.getRemainingFramesNumber());
//			sb.append(',');
//			sb.append(cumulativeReward);
//			sb.append('\n');
//			writer.write(sb.toString());
//			rewardCount = 0;
//		}
		
		return reward;
	}
	
	private float maxQ(int state) {
		float maxq = Float.NEGATIVE_INFINITY;
		
		for(int i = 0; i < actions.length; i++) {
			if (Q_values[state][i] > maxq) {
				maxq = Q_values[state][i];
			}
		}
		
		return maxq;
	}
	
	@Override
	public Key input() {
		return key;
	}

	@Override
	//The main function of the game and the function in which the training happens.
	//In this function I check in which state the agent is at, get the reward based on the information 
	//acquired from the current and previous frame/state and update the Q-table.
	//This is the function in which the action that the algorithm outputs is executed.
	public void processing() {
		
		if (!currentFrame.getEmptyFlag() && currentFrame.getRemainingFramesNumber() > 0) {
			//if there are unexecuted actions do them first.

			if (commandCenter.getSkillFlag()) {
				key = commandCenter.getSkillKey();
			}
			else {
				
				key.empty();
				commandCenter.skillCancel();
				int randomAction = RNGesus.nextInt(actions.length);
				
				//The first action the agent will do is random.
				if (firstRun) {
					firstRun = false;
					
					//Acquire the needed information from current frame.
					commandCenter.commandCall(actions[randomAction].name());
					myCurrentState = myCharacter.getState();
					oppCurrentState = oppCharacter.getState();
					myCurrentHp = myCharacter.getHp();
					myCurrentHpState = -1;
					myCurrentEnergy = myCharacter.getEnergy();
					myCurrentEnergyState = -1;
					oppCurrentHp = oppCharacter.getHp();
					oppCurrentHpState = -1;
					oppCurrentEnergy = oppCharacter.getEnergy();
					oppCurrentEnergyState = -1;
					currentDistanceX = currentFrame.getDistanceX();
					currentDistance = -1;
					
					//Translate them so that they fit the definition of the states' parameters.
					if (currentDistanceX < 150) {
						currentDistance = 0;
					}
					else if (150 <= currentDistanceX && currentDistanceX < 300) {
						currentDistance = 1;
					}
					else if (300 <= currentDistanceX && currentDistanceX < 400) {
						currentDistance =2;
					}
					else if (400 <= currentDistanceX && currentDistanceX < 500) {
						currentDistance =3;
					}
					else {
						currentDistance = 4;
					}
					
					if (0 <= myCurrentHp && myCurrentHp < 60) {
						myCurrentHpState = 0;
					}
					else if (60 <= myCurrentHp && myCurrentHp < 120){
						myCurrentHpState = 1;
					}
					else if (120 <= myCurrentHp && myCurrentHp < 180){
						myCurrentHpState = 2;
					}
					else if (180 <= myCurrentHp && myCurrentHp < 240){
						myCurrentHpState = 3;
					}
					else{
						myCurrentHpState = 4;
					}
					
					if (0 <= oppCurrentHp && oppCurrentHp < 60) {
						oppCurrentHpState = 0;
					}
					else if (60 <= oppCurrentHp && oppCurrentHp < 120){
						oppCurrentHpState = 1;
					}
					else if (120 <= oppCurrentHp && oppCurrentHp < 180){
						oppCurrentHpState = 2;
					}
					else if (180 <= oppCurrentHp && oppCurrentHp < 240){
						oppCurrentHpState = 3;
					}
					else{
						oppCurrentHpState = 4;
					}
					
					if (0 <= myCurrentEnergy && myCurrentEnergy < 75) {
						myCurrentEnergyState = 0;
					}
					else {
						myCurrentEnergyState = 1;
					}
					
					if (0 <= oppCurrentEnergy && oppCurrentEnergy < 75) {
						oppCurrentEnergyState = 0;
					}
					else {
						oppCurrentEnergyState = 1;
					}
					
					prevRLstate = findState(currentDistance, myCurrentState, myCurrentHpState, myCurrentEnergyState
									,oppCurrentState, oppCurrentHpState, oppCurrentEnergyState);
					prevFrame = currentFrame;
					//System.out.println(actions[randomAction].name());
				}
				else {
					
					int greedy = RNGesus.nextInt(100);
					
					myCurrentState = myCharacter.getState();
					oppCurrentState = oppCharacter.getState();
					myCurrentHp = myCharacter.getHp();
					myCurrentHpState = -1;
					myCurrentEnergy = myCharacter.getEnergy();
					myCurrentEnergyState = -1;
					oppCurrentHp = oppCharacter.getHp();
					oppCurrentHpState = -1;
					oppCurrentEnergy = oppCharacter.getEnergy();
					oppCurrentEnergyState = -1;
					currentDistanceX = currentFrame.getDistanceX();
					currentDistance = -1;
					
					if (currentDistanceX < 200) {
						currentDistance = 0;
					}
					else if (200 <= currentDistanceX && currentDistanceX < 300) {
						currentDistance = 1;
					}
					else if (300 <= currentDistanceX && currentDistanceX < 400) {
						currentDistance =2;
					}
					else if (400 <= currentDistanceX && currentDistanceX < 500) {
						currentDistance =3;
					}
					else {
						currentDistance = 4;
					}
					
					if (0 <= myCurrentHp && myCurrentHp < 60) {
						myCurrentHpState = 0;
					}
					else if (60 <= myCurrentHp && myCurrentHp < 120){
						myCurrentHpState = 1;
					}
					else if (120 <= myCurrentHp && myCurrentHp < 180){
						myCurrentHpState = 2;
					}
					else if (180 <= myCurrentHp && myCurrentHp < 240){
						myCurrentHpState = 3;
					}
					else{
						myCurrentHpState = 4;
					}
					
					if (0 <= oppCurrentHp && oppCurrentHp < 60) {
						oppCurrentHpState = 0;
					}
					else if (60 <= oppCurrentHp && oppCurrentHp < 120){
						oppCurrentHpState = 1;
					}
					else if (120 <= oppCurrentHp && oppCurrentHp < 180){
						oppCurrentHpState = 2;
					}
					else if (180 <= oppCurrentHp && oppCurrentHp < 240){
						oppCurrentHpState = 3;
					}
					else{
						oppCurrentHpState = 4;
					}
					
					if (0 <= myCurrentEnergy && myCurrentEnergy < 75) {
						myCurrentEnergyState = 0;
					}
					else {
						myCurrentEnergyState = 1;
					}
					
					if (0 <= oppCurrentEnergy && oppCurrentEnergy < 75) {
						oppCurrentEnergyState = 0;
					}
					else {
						oppCurrentEnergyState = 1;
					}
					
					if (findState(currentDistance, myCurrentState, myCurrentHpState, myCurrentEnergyState
									,oppCurrentState, oppCurrentHpState, oppCurrentEnergyState) != -1) {
						currentRLstate = findState(currentDistance, myCurrentState, myCurrentHpState, myCurrentEnergyState
								,oppCurrentState, oppCurrentHpState, oppCurrentEnergyState);
						
						//states[currentRLstate].print();
						int reward = getReward(prevRLstate,currentRLstate);
						float maxQ = maxQ(currentRLstate);
						
						//the epsilon variable decreases as the game goes on.
						//If epsilon condition is met then do a random action (explore).
						//Otherwise, do the best action based on the q-table.
						//No matter the case, get the reward and update the corresponding q-value on the table.
						if (greedy < (80 - (currentFrame.getRound() - 1) *30)) {
							float q = Q_values[prevRLstate][randomAction];
							
		
							Q_values[prevRLstate][randomAction] = (1-a) *q + a * (reward + g * maxQ);
		
							prevRLstate = currentRLstate;
							prevFrame = currentFrame;
							commandCenter.commandCall(actions[randomAction].name());
						}
						else {
							//System.out.println("EXPLOITATION");
							int bestAction = runQPolicy(prevRLstate);
							float q = Q_values[prevRLstate][bestAction];
							
		
							Q_values[prevRLstate][bestAction] = (1-a) *q + a * (reward + g * maxQ);
		
							prevRLstate = currentRLstate;
							prevFrame = currentFrame;
							commandCenter.commandCall(actions[bestAction].name());
							
						}
					}
					else {
						System.out.println(currentDistance + "|" + myCurrentState.toString() + "|" + myCurrentHpState
								+ "|" + myCurrentEnergyState + "|" + oppCurrentState.toString() + "|" + oppCurrentHpState + "|" + oppCurrentEnergyState);
					}
				}
				
				
			}
		}
	}
	
	//returns the action with the biggest q-value for this state.
	public int runQPolicy(int state) {
		
		int bestAction = -1;
		float bestQ = Float.NEGATIVE_INFINITY;
		
		for (int i=0; i < actions.length;i++) {
			if (Q_values[state][i] > bestQ) {
				bestQ = Q_values[state][i];
				bestAction = i;
			}
		}
	
		return bestAction;
	}
	
	@Override
	public void roundEnd(int p1Hp, int p2Hp, int frames) {
		
		
	}

}
