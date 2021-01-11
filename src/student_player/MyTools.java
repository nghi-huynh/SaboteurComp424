package student_player;

import java.util.ArrayList;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;


public class MyTools {
	static int nMapUsed=1;
	static int nMalusUsed=0;
	
	
	
    public static double getSomething() {
        return Math.random();
    }
    public static SaboteurMove selectAction(SaboteurBoardState boardState) {
    	ArrayList<SaboteurMove> actions=boardState.getAllLegalMoves();
    	ArrayList<SaboteurMove> potentialMove=new ArrayList<SaboteurMove>();
    	ArrayList<SaboteurMove> dropCard=new ArrayList<SaboteurMove>();
    	ArrayList<SaboteurMove>	playMap=new ArrayList<SaboteurMove>();
    	ArrayList<SaboteurMove> playMalus=new ArrayList<SaboteurMove>();
    	SaboteurTile[][] hiddenBoard=boardState.getHiddenBoard();//get hidden board as SaboteurTile
    	SaboteurTile[] hidden_obj= {hiddenBoard[12][3],hiddenBoard[12][5],hiddenBoard[12][7]};
//    	System.out.println(hidden_obj[0].getName()+" "+hidden_obj[1].getName()+" "+hidden_obj[2].getName());
    	//check hidden obj after revealing
    	int[] hidden_revealed= {0,0,0};//0: not revealed yet, 1: nugget, -1: revealed, not obj
    	int h=0;
    	int b=-1;//index of the nugget
    	for(SaboteurTile t: hidden_obj) {
    		if(t.getIdx().equals("nugget")) {
    			b=h;
    			hidden_revealed[b]=1;
    		}else if(t.getIdx().equals("hidden1")||t.getIdx().equals("hidden2")) {
    			hidden_revealed[h]=-1;
    		}
    		h++;
    	}
    	int nTiles=0;
    	int nMalus=0;
    	int nMap=0;
    	for(SaboteurMove a:actions) {
    		if(a.getCardPlayed().getName().compareTo("Tile:0")==0
    		||a.getCardPlayed().getName().compareTo("Tile:6")==0||a.getCardPlayed().getName().compareTo("Tile:6_flip")==0
    		||a.getCardPlayed().getName().compareTo("Tile:7")==0||a.getCardPlayed().getName().compareTo("Tile:7_flip")==0
    		||a.getCardPlayed().getName().compareTo("Tile:9")==0||a.getCardPlayed().getName().compareTo("Tile:9_flip")==0
    		||a.getCardPlayed().getName().compareTo("Tile:5")==0||a.getCardPlayed().getName().compareTo("Tile:5_flip")==0
    		||a.getCardPlayed().getName().compareTo("Tile:8")==0||a.getCardPlayed().getName().compareTo("Tile:10")==0
    		||a.getCardPlayed() instanceof SaboteurDestroy) {
    			nTiles++;
    			potentialMove.add(a);
    		}else if(a.getCardPlayed()instanceof SaboteurMalus) {//play malus as a strategy to block opponent's moves
    			nMalus++;
    			playMalus.add(a);
    		}else if(a.getCardPlayed() instanceof SaboteurBonus) {
    			potentialMove.add(a);
    		}else if(a.getCardPlayed() instanceof SaboteurMap) {
    			nMap++;
    			playMap.add(a); 	
    		}else if(a.getCardPlayed() instanceof SaboteurDrop) {//drop used cards
    			dropCard.add(a);
    		}
    	}
    	if(nMalus>0) {//check for numberMalus
    		nMalusUsed++;
    		if(nMalusUsed<=2) {
    			return playMalus.get(0);//return the first malus
    		}
    	}else if(nMap>0) {
    		for(SaboteurMove m: playMap) {
    			int[] pos=m.getPosPlayed();
    			if(pos[0]==12&&(pos[1]==3||pos[1]==7)) {
    				nMapUsed++;
    				if(nMapUsed<=2) {
    					return m;
    				}
    			}
    		}
    	}
    	int bonus=0;
    	int ind=0;
    	int i=0;
    	for(SaboteurMove m:potentialMove) {
    		if(m.getCardPlayed() instanceof SaboteurBonus) {
    			bonus++;
    			ind=i;
    			
    		}
    		i++;
    	}
    	Random rand1=new Random();
    	if(nTiles==0) {//there is no SaboteurTile in potential moves since our opponent plays Malus
    		if(bonus>0) {//play bonus
    			return  potentialMove.get(ind);
    		}else {//drop card in dropCard
    			return dropCard.get(rand1.nextInt(dropCard.size()));
    			
    		}
    	}
    	//default cases, always aim for the middle objective first
		    		int x_coor=100;//update later
		    		int y_coor=100;
		    		int index_x=0;
		    		int t=0;
		    			for(SaboteurMove m: potentialMove) {
		    				if(m.getCardPlayed() instanceof SaboteurTile) {
		    					int[] distance=m.getPosPlayed();
		    					if(distance[0]>5&&distance[0]<=12) {
		    						distance[0]=12-distance[0];
		    						if(x_coor>distance[0]) {//find the best distance
		    							x_coor=distance[0];//update min distance to the hidden obj
		    							index_x=t;
		    						}
		    						if(x_coor<=3) {
		    							distance[1]=Math.abs(5-distance[1]);
		    							if(y_coor>distance[1]) {
		    								y_coor=distance[1];
		    								index_x=t;
		    							}
		    						
		    						}
		    					}
		    				}
		    				t++;
		    			}
		    			return potentialMove.get(index_x);
    }
    
 
    
    public static SaboteurCard bestTile(ArrayList<SaboteurMove> actions) {
    	
    	return actions.get(0).getCardPlayed();
    }
    
    
    
    
    
}