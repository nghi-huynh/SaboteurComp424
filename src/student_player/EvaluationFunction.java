package student_player;


import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurTile;

//this class is used in the MCTS-IC-E to evaluate the leaf node
//similar to the opening, many parts are the same
public class EvaluationFunction {

	//all the evaluation criteria
	//position values
	double[][] posValueM = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0.5,1,2,3,3.5,-1,3.5,3,2,1,0.5,0,0,0},
							{1,2,3,4,4.5,5,4.5,4,3,2,1,0.5,0,0,0},
							{2,3,4,5,5.5,6,5.5,5,4,3,2,1,0,0,0},
							{3,4,5,6,6.5,7,6.5,6,5,4,3,2,1,0,0},
							{4,5,6,7,7.5,8,7.5,7,6,5,4,3,2,1,0},
							{5,6,7,8,8.5,9,8.5,8,7,6,5,4,3,2,1},
							{6,7,8,9,9.5,10,9.5,9,8,7,6,5,4,3,2},
							{7,8,9,-1,10,-1,10,-1,9,8,7,6,5,4,3},
							{6,7,8,8.5,9,10,9,8.5,8,7,6,5,4,3,2},
							{5,6,7,7.5,8,9,8,7.5,7,6,5,4,3,2,1}};
	
	double[][] posValueL = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{1,2,3,4,3,-1,1.5,1,0.2,0.1,0,0,0,0,0},
							{2,3,4,5,4,3.5,3,1,0.5,0.3,0.1,0,0,0,0},
							{3,4,5,6,5,4.5,3.5,3,1,0.5,0.3,0.1,0,0,0},
							{4,5,6,7,6,5.5,4.5,4,2,1,0.5,0.3,0.1,0,0},
							{5,6,7,8,7,6.5,5.5,5,3,2,1,0.5,0,0,0},
							{6,7,8,9,8,7.5,6.5,6,4,3,2,1,0,0,0},
							{7,8,9,10,9,8.5,7.5,7,5,4,3,2,1,0,0},
							{8,9,10,-1,10,-1,8.5,-1,6,5,4,3,2,1,0},
							{7,8,9,10,9,8.5,7.5,7,5,4,3,2,1,0,0},
							{6,7,8,9,8,7.5,6.5,6,4,3,2,1,0,0,0}};
	
	double[][] posValueR = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0.1,0.1,0.2,1,1.5,-1,3,4,3,2,1,0.5,0.2,0,0},
							{0.2,0.4,0.5,2,2.5,3.5,4,5,4,3,2,1,0.5,0,0},
							{0.4,0.6,1,3,3.5,4.5,5,6,5,4,3,2,1,0,0},
							{0.6,1,2,4,4.5,5.5,6,7,6,5,4,3,2,1,0},
							{1,2,3,5,5.5,6.5,7,8,7,6,5,4,3,2,1},
							{2,3,4,6,6.5,7.5,8,9,8,7,6,5,4,3,2},
							{3,4,5,7,7.5,8.5,9,10,9,8,7,6,5,4,3},
							{4,5,6,-1,8.5,-1,10,-1,10,9,8,7,6,5,4},
							{3,4,5,7,7.5,8.5,9,10,9,8,7,6,5,4,3},
							{2,3,4,6,6.5,7.5,8,9,8,7,6,5,4,3,2}};
	
	//tile values
	int[] tileValSet1 = {2,6,0,0,7,0,6,10,9,9,8};//The middle
	int[] tileValSet2 = {5,7,0,6,8,4,0,10,0,9,6};//if the pos is to the left of the objective and needs to go right
	int[] tileValSet3 = {5,0,4,6,8,0,7,10,9,0,6};//if the pos is to the right of the objective and needs to go left
	
	//hidden objectives
	int[] obj = {0,0,0};
	
	//The string array to hold the corresponding idx for each tile
	String[] tileIDX = {"0","5","5-f","6","6-f","7","7-f","8","9","9-f","10"};
	
	//all possible legal moves
	ArrayList<SaboteurMove> legalMoves = new ArrayList<SaboteurMove>();//the array list holding all legal moves
	
	//empty constructor
	public EvaluationFunction() {
		
	}
	
	//main method to evaluate
	public double evaluate(SimulatedBoardState boardState) {
		
		//initialize legal  moves
		legalMoves = boardState.getAllLegalMoves();
		
		hiddenObjs(boardState,obj);
		//update hidden objective status
		
		//Number of useful cards
		int numOfCards = 0;
		double totalVal = 0;
		double avgVal = 0;
		
		int objPos = -1;

		//loop through all legal moves to find all useful moves and their corresponding values
		for(int i = 0; i < legalMoves.size(); i++ ) {
			SaboteurMove currMove = legalMoves.get(i);
			int[] currMovePos = currMove.getPosPlayed();
			SaboteurCard currCard = currMove.getCardPlayed();
			
			if(currCard instanceof SaboteurDestroy) {
				//do nothing, not a useful card
				continue;
			}
			else if(currCard instanceof SaboteurMalus) {
				//System.out.println("MALUS");
				numOfCards++;
				int count = calcDist(boardState);
				
				if(count <= 10) {
					count = 0;
				}
				//System.out.println("Count: "+ (count-10));
				
				totalVal = totalVal + count;
				
			}else if(currCard instanceof SaboteurTile) {
				//first check if it is one of the useful tiles and find the corresponding pos
				int pos = getTilePos(currCard.getName());
				double value = -1;
				//if the tile exists in the list, we proceed
				if(pos != -1) {
					//we count it since it is functional
					numOfCards ++; 
					//find the pos of the obj, we will refer to them later
					if (obj[0] == 1) {//nugget is the left obj, we make that the direction
						objPos = 0;
					}else if(obj[2]==1) {
						objPos = 2;
					}else {//otherwise if we haven't found the nugget, or the middle one is the nugget, that's the dir
						objPos = 1;
					}
					
					value = getValforTile(objPos,pos,currMovePos);
					
					totalVal = totalVal + value;
				}
				
			}
		}

		if (numOfCards == 0) {
			avgVal = 0;
		}else {
			avgVal = totalVal/numOfCards;
		}
		return avgVal;
	}
	
	public int getTilePos(String cardName) {
		//get the IDX of the current card 
		String IDX = cardName.split(":")[1];
		int pos = -1;
		
		//iterate the tileIDX array to find the position
		//if not found return 0;
		
		for(int i = 0; i < tileIDX.length; i++) {
			String curr = tileIDX[i];
			if(curr.equals(IDX)) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	public void hiddenObjs(SimulatedBoardState boardState, int[] obj) {
  		
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


	public double getValforTile(int objPos, int tilePos, int[] movePos) {
		double value = 0;
		double posVal = 0;
		double tileVal = 0;
		
		int i = movePos[0];
		int j = movePos[1];
		
		int diff;
		//nugget at left, at (12,3)
		if(objPos == 0) {
			posVal = posValueL[i][j];
			//check which direction we need to go (L or R) and get value from corresponding tile value set
			diff = j-3;
			if (diff == 0) {//go down
				tileVal = tileValSet1[tilePos];
			}else if (diff > 0) {
				tileVal = tileValSet3[tilePos];
			}else {
				tileVal = tileValSet2[tilePos];
			}
					
		}
		//nugget at right
		if(objPos == 2) {
			posVal = posValueR[i][j];
			
			diff = j-7;
			if (diff == 0) {//go down
				tileVal = tileValSet1[tilePos];
			}else if (diff > 0) {
				tileVal = tileValSet3[tilePos];
			}else {
				tileVal = tileValSet2[tilePos];
			}
		}
		else {
			posVal = posValueM[i][j];
			
			diff = j-5;
			if (diff == 0) {//go down
				tileVal = tileValSet1[tilePos];
			}else if (diff > 0) {
				tileVal = tileValSet3[tilePos];
			}else {
				tileVal = tileValSet2[tilePos];
			}
		}

		value = posVal + tileVal;
		return value;
	}
	
	public int calcDist(SimulatedBoardState boardState) {
		int count = 0;
		count = boardState.getTurnNumber();
		return count;
	}
}