package emulation.fhdl;

import java.util.Arrays;
import java.util.Stack;

import emulation.Bus;

public class MathEngine {
	/**
	 * purpose: create a logical math engine for fhdl
	 */

	private static final String bOperators = "+|&*^";
	private static final String uOperators = "!-";
	private static final String specialOperators = "()";
	private static final String legalVarNameStarters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";

	private	ScopeController scope;

	public MathEngine(ScopeController scope) {
		this.scope = scope;
	}

	public Bus evaluate(int width, String expression) {
		Stack<Bus> stack = new Stack<Bus>();
		String[] tokens = convertToPostFix(expression).split(",");
		for (String token : tokens) {
			if (bOperators.contains(token)) {
				Bus right =  stack.pop();
				Bus left = stack.pop();
				
				Bus temp = new Bus(width, 0);
				
				if(token.equals("+") || token.equals("|") ) {
					temp = left.or(right);
				}else if(token.equals("*") || token.equals("&") ) {
					temp = left.and(right);
				}else if(token.equals("^")) {
					temp = left.xor(right);
				}
				stack.push(temp);
			} else {
				stack.push(getFromToken(width, token));
			}
		}
		return stack.pop();
	}

	private Bus getFromToken(int width, String token) {
		Bus bus = new Bus(width);
		boolean not = token.startsWith("!");
		boolean twos = token.startsWith("-");
		if (not||twos)
			token = token.substring(1);
		if (token.contains("[")) {
			// bit select
			String indexString = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			int index = decode(indexString);
			String variable = token.substring(0, token.indexOf("["));
			Bus bb = scope.getVariable(variable);
			bus.set(bb.getBitValue(index));
		} else if (legalVarNameStarters.contains(token.substring(0, 1))) {
			// variable
			Bus bb = scope.getVariable(token);
			bus.set(bb.toInt());
		} else {
			int val = decode(token);
			bus.set(val);
		}
		if (not)
			return bus.not();
		if(twos)
			return bus.twosCompliment();
		return bus;
	}

	private static int decode(String text) {
		// from https://stackoverflow.com/a/13549627/4674423
		return text.toLowerCase().startsWith("0b") ? Integer.parseInt(text.substring(2), 2) : Integer.decode(text);
	}

	private String convertToPostFix(String expression) {
		// algorithm from:
		// https://www.includehelp.com/c/infix-to-postfix-conversion-using-stack-with-c-program.aspx

		expression = expression.replaceAll("\\s+", "");
		String postfix = "";
		Stack<String> stack = new Stack<String>();

		// 1.
		stack.push("(");
		expression += ")";
		// 2.
		for (int i = 0; i < expression.length();) {
			String token = getNextToken(expression, i);
			if (bOperators.contains(token)) {
				// 5.
				boolean stop = false;
				int tokenPrecedence = getPrecedence(token);
				while (!stop) {
					String top = stack.pop();
					int topPrecedence = getPrecedence(top);

					if (topPrecedence >= tokenPrecedence) {
						postfix += top + ",";
					} else {
						stack.push(top);
						stop = true;
					}
				}
				stack.push(token);
			} else if (token.equals("(")) {
				// 4.
				stack.push("(");
			} else if (token.equals(")")) {
				// 6.
				boolean stop = false;
				while (!stop) {
					String top = stack.pop();
					if (top.equals("(")) {
						stop = true;
					} else {
						postfix += top + ",";
					}
				}
			} else {
				// 3.
				postfix += token + ",";
			}

			i += token.length();
		}

		return postfix;
	}

	private int getPrecedence(String operator) {
		if (bOperators.contains(operator)) {
			return bOperators.indexOf(operator) / 2;
		} else if (uOperators.contains(operator)) {
			return bOperators.length() * uOperators.indexOf(operator) / 2;
		}
		return -1;
	}

	private String getNextToken(String expression, int index) {
		String part = expression.substring(index);
		String token = "";
		for (int i = 0; i < part.length(); i++) {
			char nextChar = part.charAt(i);
			if (bOperators.contains(nextChar + "") || specialOperators.contains(nextChar + "")) {
				if (token.length() == 0) {
					token += nextChar;
					break;
				} else {
					break;
				}
			} else {
				token += nextChar;
			}
		}
		return token;
	}
}
