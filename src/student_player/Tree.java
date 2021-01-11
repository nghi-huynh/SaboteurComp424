package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurMove;


public class Tree {
	
	public  Node root;
	public int size=0;
	public Tree() {
		
	}
	
	public int insert(Node rootNode,int parentId, ArrayList<Node> nodesToAdd ) {
		if(rootNode==null) {
			return 0;
		}
		if(rootNode.children==null) {//then it's the root
			rootNode.children=new ArrayList<Node>();
		}
		//if(id of root node is equal to parentID, add nodes to rootNode
		if(rootNode.id==parentId) {
			for(int i=0;i<nodesToAdd.size();i++) {
				Node node=nodesToAdd.get(i);
				node.parent=rootNode;
				rootNode.children.add(node);
			}
			return 1;
		}else {
			//check for other nodes to see if there is any id that matches with parent id
			for(int i=0;i<rootNode.children.size();i++) {
				int resultFlag=insert(rootNode.children.get(i),parentId,nodesToAdd);
				if(resultFlag==1) {
					return 1;
				}
			}
		}
		//parent id is not valid, return -1
		return -1;
	}
	
	public void traverse(Node root) {//post order traversal
		if(root==null) {
			return;
		}
		//System.out.println(root.board.getTurnPlayer());
		
		for(Node child: root.children) {
			traverse(child);
		}
	}
	

}
class Node{
	public int id;
	public Node parent;
	public ArrayList<Node> children;
	public SimulatedBoardState board;
	public double ucb;
	public double result;
	public int nVisited;
	public SaboteurMove move;
	
	public Node(int id,SimulatedBoardState state,SaboteurMove move,double ucb,double result,int nrOfVisited) {
		
		this.id=id;
		this.board=state;
		this.move=move;
		this.ucb=ucb;
		this.result=result;
		this.nVisited=nrOfVisited;
		children=new ArrayList<Node>();
	}
	public SimulatedBoardState getBoard() {
		return this.board;
	}
	public void setBoard(SimulatedBoardState state) {
		this.board=state;
	}
	public double getUCB() {
		return this.ucb;
	}
	public void setUCB(double ucb) {
		this.ucb=ucb;
	}
	public double getResult() {
		return this.result;
	}
	public void setResult(double r) {
		this.result=r;
	}
	public int getNVisited() {
		return this.nVisited;
	}
	public void setNVisited(int n) {
		this.nVisited=n;
	}
	
}