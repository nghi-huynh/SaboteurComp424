package student_player;




import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import boardgame.Move;


public class MTCS {
	
	private double constant=2.0;
	
	public  MTCS (){
		//empty constructor
		
	}
	public ArrayList<Double> simulation(Tree t) {
		ArrayList<Double> result=new ArrayList<Double>();
		for(Node node: t.root.children) {
			EvaluationFunction eval=new EvaluationFunction();
			Move m =node.board.getBoardMove();
			m.toPrettyString();
			double r=eval.evaluate(node.board);
			result.add(r);
			node.nVisited++;
			node.result+=r;	
	//		System.out.println("node is: "+node.nVisited+" "+node.result);
		}
		return result;
	}
	
	public void backpropagation(Tree t) {
		
		for(Node node: t.root.children) {
				t.root.nVisited++;
				t.root.result+=node.result;
	//			System.out.println("backpropagate: node is "+node.nVisited+" "+node.result);
	//			System.out.println("root is: "+t.root.nVisited+" "+t.root.result);
		}	
		return;
	}
	
	
	public void expansion(SimulatedBoardState boardState,Tree t, SaboteurBoardState state) {
		ArrayList<SimulatedBoardState> listOfSimulatedBoard=new ArrayList<SimulatedBoardState>();
		ArrayList<Node> listOfStates=new ArrayList<Node>();
		
		
			if(!state.gameOver()) {//if the boardState is not game over yet, then we need to add nodes according to each legal actions we have 
				ArrayList<SaboteurMove> actions=state.getAllLegalMoves();
				int index=0;
				for(SaboteurMove a:actions) {
					SimulatedBoardState simulatedBoard=new SimulatedBoardState(state);
					simulatedBoard.processMove(a, state);
					listOfSimulatedBoard.add(simulatedBoard);
					listOfStates.add(new Node(index,simulatedBoard,a, 0.0,0.0,0));
					index++;
				}
				if(listOfStates.size()!=t.root.children.size()) {
					t.insert(t.root, 0, listOfStates);
				}
	//			System.out.println(actions.size()+" "+listOfStates.size()+" "+t.root.children.size());	
			}else {
				listOfSimulatedBoard.add(boardState);
			}
		
		return;
	}
	
	public SimulatedBoardState selection(Tree t) {// select the best state 
		
		int totalnVisited=t.root.nVisited;//total number of visited based on the root node
		
		SimulatedBoardState bestState=t.root.board;//default boardState
		
		double maxUCB=Double.NEGATIVE_INFINITY;
		//iterate through the tree
		for(Node n:t.root.children) {
			double tmp=UCB(n.result,n.nVisited,totalnVisited);
	//		System.out.println("ucb: "+tmp);
			n.setUCB(tmp);
		}
		for(Node n: t.root.children) {
			double tmp=n.ucb;
			if(tmp>=maxUCB) {
				maxUCB=tmp;
				bestState=n.board;
			}
		}
		
		return bestState ;//return the state with the highest UCB value
	}
	
	public double UCB(double r, int numberVisited,int totalnVisited ) {//calculate the upper confidence bound 
		if(numberVisited==0) {//the numberVisited==0 means that the node is not visited so, the UCB is -infinity
			return Double.NEGATIVE_INFINITY;
		}
		return r+constant*Math.sqrt(Math.log(totalnVisited)/numberVisited);//else calculate the UCB based on the equation v+c*sqrt(logN/n)
	}
	
	public SaboteurMove bestAction(Tree t,SaboteurBoardState boardState) {
		double max=Double.NEGATIVE_INFINITY;
		SaboteurMove bestMove=boardState.getRandomMove();
		for(Node node:t.root.children) {
			double tmp=node.ucb;	
		
			if(tmp>=max) {
				max=tmp;
		//		System.out.println(max);
				bestMove=MyTools.selectAction(boardState);
			}
			
		}
		return bestMove;
	}
	
	public SaboteurMove MCTS(SaboteurBoardState state,Tree t0) {
	
		
		for(int i=0;i<5;i++) {//repeat 10 times before choosing the best action
			SimulatedBoardState s0=selection(t0);
			expansion(s0,t0,state);
			ArrayList<Double> result=simulation(t0);
			backpropagation(t0);
			for(Double r:result) {
	//			System.out.print(r+" ");
			}	
		}
			//selection(t0);
		return bestAction(t0,state);
	}
	

}