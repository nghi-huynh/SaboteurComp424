                                                package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;

public class Opening{

	//this is the class to handle the first 4 to 5 steps by hard coding priority
	//by assigning a value to the position and tile cards
	//I will include the table of values in the PDF file so it is easier to read
	
	//initialize the arrays we need
	//the 2d array for position scores
	int[][] assignedPosValues = {{300,400,0,400,300},
								{600,700,800,700,600},
								{700,800,900,800,700},
								{800,900,1000,900,800}};

	//the three sets of values for tiles
	int[] tileValSet1 = {20,60,0,0,70,0,60,100,90,90,80};//The middle
	int[] tileValSet2 = {50,70,0,60,80,40,0,100,0,90,60};//if the pos is to the left of the objective and needs to go right
	int[] tileValSet3 = {50,0,40,60,80,0,70,100,90,0,60};//if the pos is to the right of the objective and needs to go left
	
	String[] tileIDX = {"0","5","5-f","6","6-f","7","7-f","8","9","9-f","10"};//The string array to hold the corresponding idx for each tile
	
	int[] finalValues;
	int[] obj = {0,0,0};//if the hidden objective is revealed
	
	ArrayList<SaboteurMove> legalMoves = new ArrayList<SaboteurMove>();//the array list holding all legal moves
	
	
	
	public Opening() {
		//empty constructor
	}
	
	//the main method to return the best move 
	public SaboteurMove getBestMove(SaboteurBoardState boardState) {
	
		String s = boardState.toString();
//		System.out.println(s);
				
		SaboteurMove bestMove = null;
		
		int numOfLegalMoves = 0;
		
		//getting all legal moves
		legalMoves = boardState.getAllLegalMoves();
		numOfLegalMoves = legalMoves.size();
		
		//if we have legal bonus card, play it
		SaboteurCard bonus = SaboteurCard.copyACard("Bonus");
		SaboteurMove bonusMove = new SaboteurMove(bonus,0,0,260785873);
		if(boardState.isLegal(bonusMove)) {
			return bonusMove;
		}
		
		//Initialize all the arrays
		initializeFinalValueArray(numOfLegalMoves);
		hiddenObjs(boardState);
		
//		System.out.println(obj[0]+" " + obj[1] + " "+obj[2]);
		
		//initialize two integers to determine conditions in the for loop
		int posVal;
		int tileVal;
		int finalVal;
		
		//iterate through all legal moves
		for(int i = 0; i < legalMoves.size();i++) {
			SaboteurMove currMove = legalMoves.get(i);
			int[] currMovePos = currMove.getPosPlayed();
			posVal = -1;
			tileVal = -1;
			finalVal = -1;
			
			
			//get the type of card first. We only wish to consider tile, if else, continue
			SaboteurCard currCard = currMove.getCardPlayed();
			String cardType = currCard.getName();

				
			if(cardType.equals("Destroy")||cardType.equals("Malus")) {
				continue;
			}
			//if a map is available, unless we already know where the gold is, use it
			else if (cardType.equals("Map")){
				if(obj[0]==1||obj[1]==1||obj[2]==1) {
					//do nothing if gold is found
				}
				else if(obj[0] == 0) {
					bestMove = new SaboteurMove(currCard,12,3,260785873);
					return bestMove;
				}else if(obj[2] == 0) {
					bestMove = new SaboteurMove(currCard,12,7,260785873);
					return bestMove;
				}else if(obj[1] == 0){
					bestMove = new SaboteurMove(currCard,12,5,260785873);
					return bestMove;
				}
			}
			//after making sure that we are working with tiles, we can start checking for the preset values			
			else {
				//now we compute position value
				//if the move is outside of our 5x4 grid, we disregard it
				if(currMovePos[0]<5||currMovePos[0]>8||currMovePos[1]<3||currMovePos[1]>7) {

				}
				else {
					
					posVal = assignedPosValues[currMovePos[0]-5][currMovePos[1]-3];
					
					//now move on to the tile card values
					//we first check if the tile is in our function tile set
					//if it is, we proceed to determine the value
					int x = currMovePos[0];
					tileVal = getCardVal(cardType,x);
					//if not in the set, disregard
					if(tileVal == -1) {
						continue;
					}
					//if it is , update all value
					else {
						finalVal = posVal+tileVal;
					}
				}
			}
			
			//Now we update the finalValues array to prepare for final comparison
			finalValues[i] = finalVal;
		}
		
		int bestPos = findBestPos();
		//int[] bestPosVal = legalMoves.get(bestPos).getPosPlayed();
	
		//if there is no best move, we drop a card
		if(bestPos == -1) {
			SaboteurCard drop = SaboteurCard.copyACard("Drop");
			bestMove = new SaboteurMove(drop, 1, 0, 260785873);
		}
		//else we get the best move form legal moves arraylist
		else {
			bestMove = legalMoves.get(bestPos);
		}
				
		return bestMove;
	}
	
	
	//initialize the final values array with the length of the number of legal moves to -1
	public void initializeFinalValueArray( int numOfLegalMoves) {
		finalValues = new int[numOfLegalMoves];
		for(int i = 0; i < numOfLegalMoves; i++) {
			finalValues[i] = -1;
		}
	}
	
	//check if this tile is in our set of functional tile and get the value in the corresponding set
	//returns -1 if not found
	public int getCardVal(String cardName, int x) {
		//get the IDX of the current card 
		String IDX = cardName.split(":")[1];
		
		//iterate the tileIDX array to find the position
		//if not found return 0;
		int pos = -1;
		for(int i = 0; i < tileIDX.length; i++) {
			String curr = tileIDX[i];
			if(curr.equals(IDX)) {
				pos = i;
				break;
			}
		}
		//if not found, return -1;
		if(pos == -1) {
			return -1;
		}
	
		//then check for the current card's position to Middle objective
		//return the value for position (-1, 0 ,1)
		//get the value from corresponding value set and return it

		//if in the middle, use set 1
		if(x == 5) {
			return tileValSet1[pos];
		}
		//to da left of M
		else if(x < 5) {
			return tileValSet2[pos];
		}
		//to da right of M
		else {
			return tileValSet3[pos];
		}
	}
	
	//get the best final value and return the position for the best move
	public int findBestPos() {
		int bestPos = -1;
		int bestValue = -1;
		
		for(int i = 0; i < finalValues.length; i++) {
			if(finalValues[i]>bestValue) {
				bestValue = finalValues[i];
			}
		}
//		System.out.println("best value: "+ bestValue);
		for(int i = 0; i < finalValues.length;i++) {
			if(finalValues[i] == bestValue) {
				bestPos = i;
				break;
			}
		}
		
		return bestPos;
	}
	
	//update the hiddenObj situations
	public void hiddenObjs(SaboteurBoardState boardState) {
		
		//check if any objective is revealed

			SaboteurTile[][] objBoard = boardState.getHiddenBoard();
			//left
			if(!objBoard[12][3].getName().equals("Tile:8")) {
				//if it is the gold, update to 1
				if(objBoard[12][3].getName().equals("Tile:nugget")){
					obj[0] = 1;
				}//if it is not the gold, update to -1
				else {
					obj[0] = -1;
				}
			}
			//middle
			if(!objBoard[12][5].getName().equals("Tile:8")) {
				//if it is the gold, update to 1
				if(objBoard[12][5].getName().equals("Tile:nugget")){
					obj[1] = 1;
				}//if it is not the gold, update to -1
				else {
					obj[1] = -1;
					}
			}
			//right
			if(!objBoard[12][7].getName().equals("Tile:8")) {
				//if it is the gold, update to 1
				if(objBoard[12][7].getName().equals("Tile:nugget")){
					obj[2] = 1;
				}//if it is not the gold, update to -1
				else {
					obj[2] = -1;
				}
			}

			//if there are two objectives revealed and they are not gold
			//we know the gold is in the third one
			//if(obj[0]==-1&&obj[1]==-1 || obj[0]==-1&&obj[2]==-1 || obj[1]==-1&&obj[2]==-1) {
				
			if(obj[0]==-1&&obj[1]==-1) {
				obj[2] = 1;
			}else if( obj[0]==-1&&obj[2]==-1 ) {
				obj[1] = 1;
			}else if( obj[1]==-1&&obj[2]==-1 ) {
				obj[0] = 1;
			}
	
	}
}





