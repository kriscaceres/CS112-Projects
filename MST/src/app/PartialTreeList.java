package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;
import structures.MinHeap;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	//modifies the list so tree is now rear.
    	Node ptr = new Node(tree); //create pointer with its own partial tree.
    	if (rear == null) { //if the list is empty
    		ptr.next = ptr; //the next node is itself (CLL)
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++; //increment the size for a successful append.
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList partialTreeList = new PartialTreeList();
		MinHeap<Arc> tempHeap = new MinHeap<>();
		for (Vertex vf : graph.vertices){//for each vertex in the graph vertex list
			// loops through each vertex
			PartialTree partialTree = new PartialTree(vf); //create a new partial tree per vertex
			Vertex.Neighbor singleVertexNeighbor = vf.neighbors; //grab a neighbor from the vertex
			while (singleVertexNeighbor != null) {
				//create an arc between the vertex and its neighbor and assign the weight
				Arc arc = new Arc(vf, singleVertexNeighbor.vertex, singleVertexNeighbor.weight);
				//grab the MinHeap of arcs and insert your new arc
				partialTree.getArcs().insert(arc);

				//go to the next neighbor of the vertex you are on.
				singleVertexNeighbor = singleVertexNeighbor.next;
			}

			partialTreeList.append(partialTree); //adds your new partial tree into the list of partial trees
		}

		return partialTreeList; //return the list.
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {

		// Create a new Array List of arcs to hold the MST
		ArrayList<Arc> arcArrayList = new ArrayList<>();

		// Go through the algorithm until there is only 1 PartialTree remaining in the list (MST)
		while (ptlist.size() > 1) {
			// Get/Remove the first PartialTree from the PartialTreeList
			PartialTree partialTree = ptlist.remove();
			// Get the MinHeap from that particular PartialTree
			MinHeap<Arc> minHeap = partialTree.getArcs();
			// Get/Remove the minimum weighted arc from the MinHeap
			Arc minArc = minHeap.deleteMin(); //greatest priority is minimum weighted arc
			while (minArc != null) {// Loop the minHeap of arcs until it finds one that links to another partialTree
				// Get V1 and V2 of the arc
				Vertex v1 = minArc.getv1();
				Vertex v2 = minArc.getv2();
				// Check whether V1 and V2 are in other PartialTrees using removeTreeContaining
				// Try to find v1 or v2 in the ptlist.
				PartialTree otherPartialTree;
				//try {
				otherPartialTree = ptlist.removeTreeContaining(v1);         // See if v1 is in the other PartialTree
				//} catch (NoSuchElementException e) {
				//    e.printStackTrace();
				//}
				if (otherPartialTree == null) {
					//try {
					otherPartialTree = ptlist.removeTreeContaining(v2);     // See if v2 is in the PartialTree
					//} catch (NoSuchElementException e) {
					//    e.printStackTrace();
					//}
				}
				//
				if (otherPartialTree != null) {                             // Either v1 or v2 are in other PartialTree in the ptlist
					// Merge current PartialTree with the otherPartialTree, including the MinHeap
					partialTree.merge(otherPartialTree);
					arcArrayList.add(minArc);                               // Put the arc into the MST
					//
					ptlist.append(partialTree);                             // Put the combined PartialTree back into the ptlist.
					//
					// The number of PartialTrees in the ptlist should decrease by 1
					//
					break;                                                  // Exit minArc while loop. Go to ptlist loop
				}
				else {
					// v1 and v2 of the arc are not in any other PartialTree.
					// They both are in the current PartialTree. Ignore and continue with the next arc from the MinHeap
				}
				// Go to the next arc in the MinHeap
				minArc = minHeap.deleteMin();
			}
		}
		//
		return arcArrayList;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException
	{	PartialTree partialTreeToRemove = null;

		// Empty list
		if (rear == null) {
			throw new NoSuchElementException("Empty Tree List.");
		}

		// The loop variable
		Node temp = rear;

		// Loop through the list starting from rear
		do {
			//current node temp handling one node
			PartialTree tree = temp.tree;
			boolean isVertexInTree = isVertexInPT(tree, vertex);
			if (isVertexInTree) {
				// Return the tree in that node
				partialTreeToRemove = tree;
				//
				// Remove the node from the list
				removeFromList(temp);
				//
				break;
			}
			// go to next one
			temp = temp.next;

			// continue if not reach rear
		} while (temp != rear);
		//
		if (partialTreeToRemove == null) {
			//throw new NoSuchElementException("Tree not found.");
			return null;
		}
		else {
			return partialTreeToRemove;
		}
    		/* COMPLETE THIS METHOD */
    	

     }

	private boolean isVertexInPT (PartialTree pTree, Vertex vertex) {
		// Go up the parentTree to the top starting from the input vertex
		Vertex parent = vertex;
		while (parent.parent != parent) {         // Continue if it not the top of the parentTree
			// Go up to the next parent in the tree
			parent = parent.parent;
		}
		// If the top is the root of the partial tree checking, the vertex is in the partial tree
		boolean check = parent == pTree.getRoot();
		return check;
	}

	private void removeFromList (Node node) {
		// Find the node before
		Node prev;                // prev.next == node
		prev = node;
		while (!(prev.next == node)) {//will place prev right before node
			prev = prev.next;
		}
		//get the node after
		Node after = node.next;
		// Only 1 node in the list
		if (after == node && prev == node) {
			rear = null;
			size--;
		}
		// Two nodes in the list
		else if (after == prev) {
			if (node == rear) {                        // If the removed node is the rear itself, point the rear to the next one first
				rear = rear.next;
			}
			//
			(node.next).next = node.next;
			size--;
		}
		else { //more than 2 nodes
			if (node == rear) {
				rear = prev; //the rear is the one before
			}
			prev.next = after;
			size--;
		}
		// Clean up the node removed (just in case)
		node.next = null;
	}
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


