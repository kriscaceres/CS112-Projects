package structures;

import javax.swing.text.html.HTML;
import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		Stack <TagNode> tags = new Stack<TagNode>(); //create stack with tags to indicate hierarchy.
		root = new TagNode("html", null, null); //assumes that all html files start with <html> root.
		sc.nextLine(); //this reads through the html file line by line.
		tags.push(root); //this is the first tag and will be the last to be popped.
		while (sc.hasNext()) { //while there are more lines to read
			String input = sc.nextLine(); //input is the line we are on.
			TagNode tempNode = new TagNode(input, null, null);
			//allows us to add this node as a child or sibling.
			TagNode currentTagFirstChild = tags.peek().firstChild;
			//this lets us check if the first child has siblings or not.
			if (isCloseTag(input)) {
				tags.pop(); //pops the tag because we are done with it.
			}else if (isOpenTag(input)) { //if the input is an open tag of format <...>
				input = input.replace("<", "").replace(">", ""); //removes the formatting
				tempNode = new TagNode(input, null, null);
				if (currentTagFirstChild == null) {
					tags.peek().firstChild = tempNode;
				}else{
					while (currentTagFirstChild.sibling != null) {
						currentTagFirstChild = currentTagFirstChild.sibling;
						//this brings us to the rightmost sibling to append a new sibling to.
					}
					currentTagFirstChild.sibling = tempNode;
				}
				tags.push(tempNode);
			}
			else {
				if (currentTagFirstChild == null){
					tags.peek().firstChild = tempNode;
				}else{
					while(currentTagFirstChild.sibling != null) {
						currentTagFirstChild = currentTagFirstChild.sibling;
					}
					currentTagFirstChild.sibling = tempNode;
				}

				}
			}
			
			
			
			
	}
	private boolean isOpenTag(String input) {
		return input.contains("<") && input.contains(">") && !input.contains("/");
	}
	
	private boolean isCloseTag(String input) {
		return input.contains("<") && input.contains(">") && input.contains("/");
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		//find all occurrences of old tag and replace with new tag.
		//know root, must move down hierarchy. there are children with siblings.
		//recursion with private method?
		if (oldTag == null || newTag == null){
			return;
		}
		replaceTagRecursion(this.root.firstChild, oldTag, newTag);

	}

	private void replaceTagRecursion(TagNode root, String oldTag, String newTag){
		if (root == null){
			return;
		}
		if (root.tag.equals(oldTag)){
			root.tag = newTag;
		}
		replaceTagRecursion(root.firstChild, oldTag, newTag);
		replaceTagRecursion(root.sibling, oldTag, newTag);
	}



	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		if (root.firstChild == null){
			return;
		}
		TagNode ptr = findTable(root.firstChild);
		if (ptr == null){
			return;
		}
		ptr = ptr.firstChild;
		for (int i = 1; i< row; i++){
			ptr = ptr.sibling; //goes to the row specified.
		}
		ptr = ptr.firstChild; //goes to its first column
		while (ptr != null){
			TagNode tempText = ptr.firstChild; //temporarily holds the column's plain text.
			TagNode tempTextSibling = ptr.firstChild.sibling;
			ptr.firstChild = new TagNode("b", tempText, tempTextSibling);
			ptr = ptr.sibling;
		}

	}


	private TagNode findTable(TagNode root) {
		if (root == null) {
			return null;
		} else {
			TagNode sibling = null;
			TagNode child = null;
			if (root.tag.equals("table")){
				return root;
			}
			if (root.sibling != null){
				sibling = findTable(root.sibling);
				if (sibling.tag.equals("table")) {
					return sibling;
				}
			}
			if (root.firstChild != null){
				child = findTable(root.firstChild);
				if (child.tag.equals("table")) {
					return child;
				}
			}
		}
		return null;
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (root == null || tag == null){
			return;
		}else{
			while (containsTag(tag, root)){
				removeTag(tag, root, root.firstChild);
			}
		}
	}

	private void removeTag(String target, TagNode prev, TagNode curr){
			if (prev == null || curr == null){return;}
			else if (curr.tag.equals(target)){
				if (target.equals("ol") || target.equals("ul")){
					changeToP(curr, target);

				}
				if (curr == prev.firstChild){
					prev.firstChild = curr.firstChild;
					curr = setLastSib(curr.firstChild, curr.sibling);
				}else if (curr == prev.sibling){
					prev.sibling = curr.firstChild;
					curr = setLastSib(curr.firstChild, curr.sibling);
				}
			}
			prev = curr;
			removeTag(target, prev, curr.sibling);
			removeTag(target, prev, curr.firstChild);


	}
	private boolean containsTag(String tag, TagNode currentNode){
		if (currentNode == null){
			return false;
		}else if (currentNode.tag.equals(tag)){
			return true;
		}else {
			return containsTag(tag, currentNode.firstChild) || containsTag(tag, currentNode.sibling);
		}
	}



	private void changeToP(TagNode root, String target){
		TagNode prev = root;
		if (root == null){
			return;
		}else if (root.tag.equals(target)) {
			root = root.firstChild;
			while (root != null) {
				if (root.tag.equals("li")) {
					root.tag = "p";
				}
				root = root.sibling;

			}
			removeTag(target, prev, prev.firstChild);
			removeTag(target, prev, prev.sibling);

		}
		changeToP(prev.firstChild, target);
		changeToP(prev.sibling, target);

	}

	private TagNode setLastSib(TagNode next, TagNode sib){
		while (next.sibling != null){
			next = next.sibling;
		}
		next.sibling = sib;
		return next;
	}

	/*private TagNode setLastSibling(TagNode curr){
		TagNode sibling = curr.sibling;
		curr = curr.firstChild;
		while (curr.sibling != null){
			curr = curr.sibling;
		}
		curr.sibling = sibling;
		return curr;
	}
	*/

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		if (root == null || tag == null || word == null || word.isEmpty() || tag.isEmpty()){
			return;
		}
		addTag(word, tag, root, root.firstChild);

	}

	private void addTag(String target, String tag, TagNode prev, TagNode curr){
		if (curr == null || prev == null){
			return;
		}else if (curr.firstChild == null && curr.tag.toLowerCase().contains(target.toLowerCase())) {
			if (prev.firstChild == curr) { //checks up-down direction
				prev.firstChild = createAddedTagNode(curr.tag, target, tag);
				curr = setLastSib(prev.firstChild, curr.sibling);
			} else if (prev.sibling == curr) {
				prev.sibling = createAddedTagNode(curr.tag, target, tag);
				curr = setLastSib(prev.sibling, curr.sibling);
			}
			prev = curr;
			addTag(target, tag, prev, curr.sibling);
		}else{
			prev = curr;
			addTag(target, tag, prev, curr.firstChild);
			addTag(target, tag, prev, curr.sibling);
		}
	}


	private TagNode createAddedTagNode(String words, String target, String tag){
		Stack<TagNode> nodeStack = new Stack<TagNode>();
		String[] tokens = words.split("((?<=\\s)|(?=\\s))");
		StringBuilder builder = new StringBuilder();
		TagNode first = null;
		TagNode current = null;
		for (String s : tokens) {
			if (!isValidTarget(s, target)) {
				builder.append(s);
			} else if (isValidTarget(s, target)) {
				if (builder.length() > 0) {
					TagNode temp = new TagNode(builder.toString(), null, null);
					builder.setLength(0);
					nodeStack.push(temp);
					if (first == null) {
						first = nodeStack.peek();
					}
				}
				current = new TagNode(tag, new TagNode(s, null, null), null);
				if (!nodeStack.isEmpty()) {
					nodeStack.peek().sibling = current;
				}
				nodeStack.push(current);
				if (first == null) {
					first = current;
				}
			}
		}
		if (builder.length() > 0){
			current = new TagNode(builder.toString(), null, null);
			if (!nodeStack.isEmpty()){
				nodeStack.peek().sibling = current;
			}
			if (first == null){
				first = current;
			}
		}
		return first;


	}

	private boolean isValidTarget(String target, String word){
		word = word.toLowerCase();
		target = target.toLowerCase();
		if (word.equals(target)){
			return true;
		}else if (word.contains(target)){
			String punct = "?!:;,.";
			if (word.substring(0, target.length()).equals(target)){
				if (word.length() == target.length()){
					return true;
				}else {
					return (punct.contains(word.substring(target.length(), target.length() + 1)) && target.length() == word.length() + 1);
				}
			}
		}
		return false;

	}





	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
