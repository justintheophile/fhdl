package fhdl.lang;

import java.util.Arrays;
import java.util.Stack;

public class MathEngine {
	/**
	 * purpose: create a logical math engine for fhdl
	 */

	private static final String[] bOperators = "+ | & * ^ > < :".split(" ");
	private static final String[] uOperators = "! -".split(" ");
	private static final String[] specialOperators = "( )".split(" ");
	public static final String[] legalVarNameStarters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_"
			.split("");

	private ScopeController scope;

	public MathEngine(ScopeController scope) {
		this.scope = scope;
	}

	public Register evaluate(int width, String expression) {
		Stack<Register> stack = new Stack<Register>();
		String[] tokens = convertToPostFix(expression).split(",");
		boolean not = false;
		for (String token : tokens) {
			if (arrayContains(bOperators, token)) {
				Register right = stack.pop();
				Register left = stack.pop();

				Register temp = new Register(width, 0);

				if (token.equals("+") || token.equals("|")) {
					temp = left.or(right);
				} else if (token.equals("*") || token.equals("&")) {
					temp = left.and(right);
				} else if (token.equals("^")) {
					temp = left.xor(right);
				} else if (token.equals(">")) {
					Register bb = new Register(left);
					bb = left.rightShift(right);
					temp.set(bb);

				} else if (token.equals("<")) {
					Register bb = new Register(left);
					bb = left.leftShift(right);
					temp.set(bb);
				} else if (token.equals(":")) {
					temp = new Register(left.getWidth(), left.toInt() == right.toInt() ? 1 : 0);
				}
				stack.push(temp);
			} else if (arrayContains(uOperators, token)) {
				Register top = stack.pop();
				Register temp = new Register(width, 0);

				if (token.equals("!")) {
					temp = top.not();
				} else if (token.equals("-")) {
					temp = top.twosCompliment();
				}
				stack.push(temp);

			} else {
				stack.push(getFromToken(width, token));
			}
		}
		return stack.pop();
	}

	private Register getFromToken(int width, String token) {
		Register bus = new Register(width);
		boolean not = token.startsWith("!");
		boolean twos = token.startsWith("-");
		if (not || twos)
			token = token.substring(1);
		if (token.contains("[")) {
			// bit select
			String indexString = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			String variable = token.substring(0, token.indexOf("["));
			Register bb = (Register) scope.getVariable(variable);
			if (!indexString.contains("..")) {
				int index = decode(indexString);

				bus.set(bb.getBitValue(index));
			} else {
				String left = indexString.substring(0, indexString.indexOf("..")).trim();
				String right = indexString.substring(indexString.indexOf("..") + 2).trim();
				Register leftRegister = evaluate(31, left);
				Register rightRegister = evaluate(31, right);
				int leftVal = leftRegister.toInt();
				int rightVal = rightRegister.toInt();

				int highVal = Math.max(leftVal, rightVal);
				int lowVal = Math.min(leftVal, rightVal);
				Register sub = bb.subBus(highVal, lowVal);
				bus.set(sub);
			}
		} else if (token.contains("{")) {
			// bit select
			String indexString = token.substring(token.indexOf("{") + 1, token.indexOf("}")).trim();
			String variable = token.substring(0, token.indexOf("{")).trim();
			Mem bb = (Mem) scope.getVariable(variable);

			Register index = evaluate((int) 31, indexString);

			bus.set((Register) bb.get(index.toInt()));

		} else if (token.length() >= 1 && arrayContains(legalVarNameStarters, token.substring(0, 1))) {
			// variable
			Variable v = scope.getVariable(token);
			if (v == null) {
				// throw error here
			} else {
				// bus.setWidth(v.getWidth());
				if (v instanceof Register)
					bus.set((String) v.get());
				else
					bus.set(decode((String) v.get()));
			}

		} else {
			int val = decode(token);
			bus.set(val);
		}
		if (not)
			return bus.not();
		if (twos)
			return bus.twosCompliment();
		return bus;
	}

	public static int decode(String text) {
		// from https://stackoverflow.com/a/13549627/4674423\
		int result = 0;
		try {
			result = text.toLowerCase().startsWith("0b") ? Integer.parseInt(text.substring(2), 2)
					: Integer.decode(text);
		} catch (NumberFormatException e) {

		}
		return result;
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
		String lastToken = "";
		for (int i = 0; i < expression.length();) {
			String token = getNextToken(expression, i);
			if (arrayContains(bOperators, token)) {
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
			} else if (arrayContains(uOperators, token)) {
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
			lastToken = token;
		}

		return postfix;
	}

	private int getPrecedence(String operator) {
		if (arrayContains(bOperators, operator)) {
			return arrayIndexOf(bOperators, operator) / 2;
		} else if (arrayContains(uOperators, operator)) {
			return bOperators.length * arrayIndexOf(uOperators, operator) / 2;
		}
		return -1;
	}

	private String getNextToken(String expression, int index) {
		String part = expression.substring(index);
		String token = "";
		for (int i = 0; i < part.length(); i++) {
			char nextChar = part.charAt(i);
			if (arrayContains(uOperators, nextChar + "") || arrayContains(bOperators, nextChar + "")
					|| arrayContains(specialOperators, nextChar + "")) {
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

	public static int arrayIndexOf(String[] array, String term) {
		// utility method to see if term is contained in string array
		for (int i = 0; i < array.length; i++) {
			String temp = array[i];
			if (temp.equals(term)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean arrayContains(String[] array, String term) {
		return arrayIndexOf(array, term) != -1;
	}
}
