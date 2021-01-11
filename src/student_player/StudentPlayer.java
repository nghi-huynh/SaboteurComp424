package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260632588");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
    	ArrayList<SaboteurMove> legalMoves = boardState.getAllLegalMoves();
    	
    	//we check if we need to play a bonus, if so and we have a bonus, play it
    	SaboteurCard bonus = SaboteurCard.copyACard("Bonus");
		SaboteurMove bonusMove = new SaboteurMove(bonus,0,0,260785873);
		if(boardState.isLegal(bonusMove)) {
			return bonusMove;
		}
    	
    	//we check for the necessity of map card
		int[] obj = {0,0,0};//if the hidden objective is revealed
    	hiddenObjs(boardState,obj);
    	//check if we need to play map
    	for(int i = 0; i < legalMoves.size();i++) {
    		SaboteurMove currMove = legalMoves.get(i);
			SaboteurCard currCard = currMove.getCardPlayed();
			String cardType = currCard.getName();
			
			 if (cardType.equals("Map")){
					if(obj[0]==1||obj[1]==1||obj[2]==1) {
						//do nothing if gold is found
					}
					else if(obj[0] == 0) {
						SaboteurMove bestMove = new SaboteurMove(currCard,12,3,260785873);
						return bestMove;
					}else if(obj[2] == 0) {
						SaboteurMove bestMove = new SaboteurMove(currCard,12,7,260785873);
						return bestMove;
					}else if(obj[1] == 0){
						SaboteurMove bestMove = new SaboteurMove(currCard,12,5,260785873);
						return bestMove;
					}
				}
    	}
    	
    	int turnNum = boardState.getTurnNumber();
    	int boundary = 12;
    	//IMPORTANT!!!
    	//if turn number is smaller than 8 , we use opening, otherwise we use MCTS to find the best move
    	if( turnNum <= boundary) {
    		Opening newOpen = new Opening();
    		Move myMove = newOpen.getBestMove(boardState);
    		return myMove;
    	}
    	else {
    		Tree t0=new Tree();
    		SimulatedBoardState s=new SimulatedBoardState(boardState);
    		t0.root=new Node(0,s,boardState.getRandomMove(),0.0,0.0,0);
    		MTCS game=new MTCS();
            //testing the simulation
            //for the MCTS
    		
            Move myMove=game.MCTS(boardState,t0);
           // Move myMove=MyTools.selectAction(boardState);
            return myMove;
    	}
       
    }
    
    //update the hiddenObj situations
  	public void hiddenObjs(SaboteurBoardState boardState, int[] obj) {
  		
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