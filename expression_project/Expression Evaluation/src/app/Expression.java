package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;


public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is created
	 * and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables and all array items are set to
	 * zero - they will be loaded from a file in the loadVariableValues method.
	 *
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/** DO NOT create new vars and arrays - they are already created before being sent in
		 ** to this method - you just need to fill them in.
		 **/
		expr = expr.replaceAll("\\s", "");
		String delimitersString = delims + "0123456789";
		StringTokenizer st = new StringTokenizer(expr, delimitersString);
		String nextToken = "";
		
		int prevIndex = 0;
		
		while (st.hasMoreTokens()) {
			nextToken = st.nextToken();
			int indexOfToken = expr.indexOf(nextToken, prevIndex);
			int tokenLength = nextToken.length();
			if (Character.isLetter(nextToken.charAt(0))) {
				if (indexOfToken + tokenLength + 1 > expr.length()) { //Checks if token is at end of expr
					Variable tempVar = new Variable(nextToken);
					if (!vars.contains(tempVar)) { //Adds if not already in array
						vars.add(tempVar);
					}
				} else if (expr.charAt(indexOfToken + tokenLength) == '[') { //Checks if it is an array
					Array tempArray = new Array(nextToken);
					if (!arrays.contains(tempArray)) { //Adds if not already in array
						arrays.add(tempArray);
					}
				} else {
					Variable tempVar = new Variable(nextToken);
					if (!vars.contains(tempVar)) { //Adds if not already in array
						vars.add(tempVar);
					}
				}
			}
			prevIndex = indexOfToken + tokenLength - 1; //set the current index of token(+token length - 1) to prev token
		}
	}
	/**
	 * Loads values for variables and arrays in the expression
	 *
	 * @param sc     Scanner for values input
	 * @param vars   The variables array list, previously populated by makeVariableLists
	 * @param arrays The arrays array list - previously populated by makeVariableLists
	 * @throws IOException If there is a problem with the input
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 *
	 * @param vars   The variables array list, with values for all variables in the expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		// following line just a placeholder for compilation
		if(!isOperator(expr) && !Character.isLetter(expr.charAt(0))&& (expr.length() == 1)) { //if expr is just a number
			return Float.parseFloat(expr);
		} else if(!isOperator(expr) && Character.isLetter(expr.charAt(0)) && (expr.length() == 1)) { //if expression is just one var
			return getVariableValue(expr, vars);
		}

		Stack<Float> varStack = new Stack<Float>();
		Stack<String> operatorStack = new Stack<String>();

		StringTokenizer st = new StringTokenizer(expr, delims, true);
		String nextToken = "";

		while(st.hasMoreTokens()) {
			int prevIndex = 0;
			nextToken = st.nextToken();
			int tokenLength = nextToken.length();
			int indexOfToken = expr.indexOf(nextToken, prevIndex);
			if(isNumber(nextToken)) { //push onto varStack if just num
				float num = Float.parseFloat(nextToken);
				varStack.push(num);
			} else if(Character.isLetter(nextToken.charAt(0))) {
				if (isLeftBracket(st.nextToken())) {
					for (Array arr : arrays) {
						if (arr.name.equals(nextToken)) {
							String inBracket = expr.substring(indexOfToken + 2, expr.lastIndexOf(']'));
							varStack.push((float) arr.values[(int) evaluate(inBracket, vars, arrays)]);
							expr = expr.substring(expr.lastIndexOf(']'));
							expr = expr.replace("]","");
							for(int i = 0; i < st.countTokens(); i++) {
								nextToken = st.nextToken();
								if(isRightBracket(nextToken)) {
									break;
								}
							}
							break;
						}
					}
				}
				for (Variable var : vars) {
					if (var.name.equals(nextToken)) {
						varStack.push((float) var.value);
						break;
					}
				}
			}
			else if(isOperator(nextToken)) {
				if(operatorStack.isEmpty() || precedence(nextToken) > precedence(operatorStack.peek())) {
					operatorStack.push(nextToken);
				} else {
					while(!operatorStack.isEmpty() && precedence(nextToken) <= precedence(operatorStack.peek())) {
						varStack.push(processOperator(operatorStack.pop(), varStack));
					}
					operatorStack.push(nextToken);
				}
			} else if(isLeftParentheses(nextToken)) {
				operatorStack.push(nextToken);
			} else if(isRightParentheses(nextToken)) {
				while(!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {

					varStack.push(processOperator(operatorStack.pop(), varStack));
				}
				if(!operatorStack.isEmpty() && isLeftParentheses(operatorStack.peek())) {
					operatorStack.pop();
				}
			}
			prevIndex = indexOfToken + tokenLength - 1;
		}
		//Empty the operator Stack
		while(!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
			varStack.push(processOperator(operatorStack.pop(), varStack));
		}


		return varStack.pop();
	}

	
	
	
	
	private static boolean isOperator(String operator) {
		boolean valid = true;
		char[] symbols = operator.toCharArray();
		for(char c : symbols) {
			valid = ((c == '+') || (c == '-') || (c == '*') || (c == '/'));
			if(valid) {
				return valid;
			}
		}
		return false;
	}

	private static boolean isLeftParentheses(String token) {
		char symbol = token.charAt(0);
		return ((symbol == '(') || (symbol == '['));
	}
	private static boolean isRightParentheses(String token) {
		char symbol = token.charAt(0);
		return ((symbol == ')') || (symbol == ']'));
	}

	private static float processOperator(String operator, Stack<Float> vars) {
		float x = vars.pop();
		float y = vars.pop();
		return process(x, y, operator);
	}

	private static int precedence(String nextToken) {
		switch (nextToken) {
			case "+":
				return 1;
			case "-":
				return 1;
			case "*":
				return 2;
			case "/":
				return 2;
		}
		return 0;
	}

	private static boolean isNumber(String nextToken) {
		try {
			Float.parseFloat(nextToken);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	private static float getVariableValue(String variableName, ArrayList<Variable> vars) {
		for(Variable var : vars) {
			if(var.name.equals(variableName)) {
				return (float)var.value;
			}
		}
		return 0;
	}
	

	public static float getArrayVal(String arrayName, int index, ArrayList<Array> arrays) {
		for(Array array : arrays) {
			if(array.name.equals(arrayName)) {
				return (float)array.values[index];
			}
		}
		return 0;
	}
	

	private static float process(float x, float y, String operator) {
		switch(operator) {
		
			case "+":
				return x + y;
			case "-":
				return x-y;
			case "*":
				return x*y;
			case "/":
				return x/y;

		}
		return 0;
	}
	private static boolean isLeftBracket(String character) {
		char symbol = character.charAt(0);
		return ((symbol == '['));
	}
	private static boolean isRightBracket(String character) {
		char symbol = character.charAt(0);
		return ((symbol == ']'));
	}
}