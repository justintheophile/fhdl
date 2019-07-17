package emulation.fhdl;

import java.util.Arrays;
import java.util.Stack;

import emulation.console;

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

	public Bus evaluate(int width, String expression) {
		Stack<Bus> stack = new Stack<Bus>();
		String[] tokens = convertToPostFix(expression).split(",");
		for (String token : tokens) {
			if (arrayContains(bOperators, token)) {
				Bus right = stack.pop();
				Bus left = stack.pop();

				Bus temp = new Bus(width, 0);

				if (token.equals("+") || token.equals("|")) {
					temp = left.or(right);
				} else if (token.equals("*") || token.equals("&")) {
					temp = left.and(right);
				} else if (token.equals("^")) {
					temp = left.xor(right);
				} else if (token.equals(">")) {
					Bus bb = new Bus(left);
					bb = left.rightShift(right);
					temp.set(bb);

				} else if (token.equals("<")) {
					Bus bb = new Bus(left);
					bb = left.leftShift(right);
					temp.set(bb);
				} else if (token.equals(":")) {
					temp = new Bus(left.getWidth(), left.toInt() == right.toInt() ? 1 : 0);
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
		if (not || twos)
			token = token.substring(1);
		if (token.contains("[")) {
			// bit select
			String indexString = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
			String variable = token.substring(0, token.indexOf("["));
			Bus bb = (Bus) scope.getVariable(variable);
			if (!indexString.contains("..")) {
				int index = decode(indexString);

				bus.set(bb.getBitValue(index));
			} else {
				int high = decode(indexString.substring(0, indexString.indexOf("..")).trim());
				int low = decode(indexString.substring(indexString.indexOf("..") + 2).trim());
				Bus sub = bb.subBus(high, low);
				bus.set(sub);
			}
		} else if (token.contains("{")) {
			// bit select
			String indexString = token.substring(token.indexOf("{") + 1, token.indexOf("}")).trim();
			String variable = token.substring(0, token.indexOf("{")).trim();
			Mem bb = (Mem) scope.getVariable(variable);

			Bus index = evaluate((int) 31, indexString);

			bus.set((Bus) bb.get(index.toInt()));

		} else if (arrayContains(legalVarNameStarters, token.substring(0, 1))) {
			// variable
			Variable v = scope.getVariable(token);
			if (v == null) {
				// throw error here
			}
			// bus.setWidth(v.getWidth());
			if (v instanceof Bus)
				bus.set((String) v.get());
			else
				bus.set(decode((String) v.get()));

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
			if (arrayContains(bOperators, nextChar + "") || arrayContains(specialOperators, nextChar + "")) {
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
