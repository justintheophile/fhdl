package fhdl.lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import fhdl.testing.console;

public class Script {
	private String rawWords = "@ # entity goto goif nanoSleep milliSleep import end instance new print printHex printDec clr reg";
	private String keywords[] = rawWords.split(" ");
	private static final String keysymbols[] = "@ { } = ; ( ) , . + ^ ! * & |".split(" ");
	private ScopeController scope;
	private MathEngine math;
	public int index, lineOffset;
	private Stack<Integer> callStack = new Stack<Integer>();
	public static boolean interupt;

	public Script(ScopeController scope, MathEngine math) {
		init();
		this.math = math;
		this.scope = scope;
	}

	public Script() {
		init();
		this.scope = new ScopeController();
		this.math = new MathEngine(scope);

	}

	private void init() {
		for (int i = 1; i < 32; i++) {
			rawWords += "bus" + i + " ";
			rawWords += "mem" + i + " ";
		}
		keywords = rawWords.split(" ");
	}

	public ScopeController getScopeController() {
		return scope;
	}

	public void runFile(String path) {
		try {
			run(loadFile(path));
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String loadFile(String path) {
		String contents = "";
		try {
			contents = new String(Files.readAllBytes(Paths.get(path)));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents;
	}

	public void run(String script) throws ScriptException {
		// imports
		index = 0;
		try {
			for (int i = 0; i < script.length();) {
				if (interupt) {
					interupt = false;
					return;
				}
				String token = getNextToken(script, i);
				i += token.length();

				token = token.trim();
				if (token.length() > 0) {
//				console.log(token);

					if (MathEngine.arrayContains(keywords, token)) {
						if (token.equals("entity")) {
							// entity declaration

							// get entity name
							String name = getNextToken(script, i);
							i += name.length();

							name = name.trim();
							console.log("name: " + name);

							// create entity variable object
							Entity newEntity = new Entity(name, i);
							scope.createVariable(name, newEntity);

							// get parameter names
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							console.log("params: " + params);

							String[] parameters = params.split(",");
							newEntity.parameters = parameters;
							newEntity.start = i;

							// get entity script and skip as to not execute empty entity
							String entityScript = getUntillTerminate(script, i, "end");
							i += entityScript.length();

							newEntity.body = entityScript;
						} else if (token.equals("import")) {
							String line = getUntillTerminate(script, i - 7, ";");
							String path = line.substring(line.indexOf(" "), line.length() - 1).trim();
							String file = loadFile(path);
							int lineCount = file.split("\n").length;
							lineOffset += lineCount + 1;
							script = script.replace(line, file + "\n");
							console.log(1, "imported: " + path);
							i -= 7;
						} else if (token.startsWith("reg") || token.startsWith("bus") || token.startsWith("mem")) {
							// initialize variable
							String var = getUntillTerminate(script, i, ";");
							processVariable(token + " " + var);
							i += var.length();

						} else if (token.equals("end")) {
							// ends entity definition
							scope.exitScope();
							i = callStack.pop();

						} else if (token.equals("instance")) {
							// create instance of entity

							// get instance name
							String varName = getUntillTerminate(script, i, "=");
							i += varName.length();

							varName = varName.replace("=", "").trim();

							// get entity template name
							String entityName = getUntillTerminate(script, i, "(");
							i += entityName.length();

							entityName = entityName.replace("(", "").trim();

							// get initialization parameters
							String entityParameters = getUntillTerminate(script, i, ")");
							i += entityParameters.length();

							entityParameters = entityParameters.replace(")", "");
							// get entity template
							Entity template = (Entity) scope.getVariable(entityName);
							// enter instance scope
							scope.enterScope(varName);
							// set parameters
							String[] params = entityParameters.split(",");
							for (int j = 0; j < params.length; j++) {
								String value = params[j].trim();
								String init = template.parameters[j].trim();
								processVariable(init + "=" + value + ";");
							}

							// run instance template to initialize instance
							callStack.push(i);
							i = template.start;

							// exit instance scope
						} else if (token.equals("print")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							int lineNumber = script.substring(0, i).split("\n").length - lineOffset;
							console.log(2, "l" + lineNumber + ": "
									+ math.evaluate(math.evaluate(64, split[0]).toInt(), split[1].trim()));
						} else if (token.equals("printHex")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String out = split[1].trim() + ":"
									+ math.evaluate(math.evaluate(64, split[0]).toInt(), split[1].trim()).toHex();
							console.log(2, out);
						} else if (token.equals("printDec")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String out = split[1].trim() + ":"
									+ math.evaluate(math.evaluate(64, split[0]).toInt(), split[1].trim()).toInt();
							console.log(2, out);
						} else if (token.equals("goto")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String tag = split[0].trim();

							if (script.contains("#" + tag)) {
								i = script.indexOf("#" + tag);
							}
						} else if (token.equals("goif")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String tag = split[0].trim();
							String condition = split[1].trim();
							if (math.evaluate(16, condition).toInt() != 0) {
								if (script.contains("#" + tag)) {
									i = script.indexOf("#" + tag);

								}
							}
						} else if (token.equals("milliSleep")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String time = split[0].trim();
							int millis = math.evaluate(32, time).toInt();
							milli(millis);

						} else if (token.equals("nanoSleep")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();

							params = params.trim().substring(1, params.length() - 1);
							String[] split = params.split(",");
							String time = split[0].trim();
							int nanos = math.evaluate(32, time).toInt();
							nano(nanos);

						} else if (token.equals("clr")) {
							String params = getUntillTerminate(script, i, ")");
							i += params.length();
							console.logs.clear();
						} else if (token.startsWith("#")) {
							String comment = "";

							for (int c = i; c < script.length(); c++) {
								char ch = script.charAt(c);
								if (ch == '\n') {
									break;
								} else {
									comment += ch + "";
								}
							}
							i += comment.length();

						}
					} else {
						if (MathEngine.arrayContains(keysymbols, token)) {

						} else if (token.startsWith("#")) {
							String comment = "";

							for (int c = i; c < script.length(); c++) {
								char ch = script.charAt(c);
								if (ch == '\n') {
									break;
								} else {
									comment += ch + "";
								}
							}
							i += comment.length();

						} else {
							String var = getUntillTerminate(script, i, ";");
							processVariable(token + " " + var);
							i += var.length();
						}
					}
				}
				index = i;

			}
		} catch (Exception e) {
//			int lineNumber = script.substring(0, index).split("\n").length;
//			int linei = ordinalIndexOf(script, "\n", lineNumber - 1);
//			String line = script.substring(linei + 1, ordinalIndexOf(script, "\n", lineNumber)).trim();
//			console.log(5, "error: \n line " + lineNumber + " ->  \n  > " + line);
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void nano(int nanos) {
		long start = System.nanoTime();
		while (start + nanos >= System.nanoTime())
			;
	}

	public static void milli(int millis) {
		// from https://gist.github.com/bric3/314c3d01a80e5e3c158965dcd459a8a5
		long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millis);
		while (System.nanoTime() < deadline) {
		}
		;
	}

	public static int ordinalIndexOf(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}

	public void processVariable(String var) {
		// TODO lots of repeated code here. needs refactor
		String type = "";
		String name = "";
		String value = "";
		if (var.startsWith("bus")||var.startsWith("reg") || var.startsWith("mem")) {
			// has type
			type = var.substring(0, var.indexOf(" "));
			var = var.substring(type.length());
		}
		if (var.contains("=")) {
			// has value
			value = getUntillTerminate(var, var.indexOf("=") + 1, ";").replace(";", "").trim();
		}
		// var name
		name = getUntillTerminate(var, 0, "=");
		name = name.replace("=", "").replace(";", "").trim();

		if (type.length() > 0) {

			// initializing variable
			if (type.startsWith("bus")||type.startsWith("reg")) {
				Register bus = null;
				int width = Integer.parseInt(type.substring(3));
				if (value.length() > 0) {
					bus = math.evaluate(width, value);
				} else {
					bus = new Register(width, 0);
				}
				if (bus != null) {
					scope.createVariable(name, bus);
					Register w = new Register(8, bus.getWidth());
					if (name.endsWith("_")) {
						scope.createVariable(name + ".width_", w);
					} else {
						scope.createVariable(name + ".width", w);
					}
				} else {

				}
			} else if (type.startsWith("mem")) {
				int width = Integer.parseInt(type.substring(3));

				Mem mem = new Mem(width);
				if (value.length() > 0) {
					if (value.contains("{")) {
						String[] expressions = value.replace("{", "").replace("}", "").replace(";", "").split(",");

						for (String e : expressions) {
							e = e.trim();
							if (e.startsWith("~")) {
								// size of empty memory slots
								int size = math.evaluate(31, e.replace("~", "")).toInt();
								for (int j = 0; j < size; j++) {
									mem.add(new Register(width, 0));
								}
							} else {
								Register w = math.evaluate(width, e);
								mem.add(w);
							}
						}
					} else {
						Variable copy = scope.getVariable(value.trim());
						if (copy instanceof Mem) {
							mem.set((Mem) copy);
						} else {
							// type error
						}
					}
				}
				scope.createVariable(name, mem);
			}

		} else {
			// set after initialization

			Variable target = scope.getVariable(name);

			if (target instanceof Register) {
				Register bus = null;

				if (value.length() > 0) {
					bus = math.evaluate(((Register) target).getWidth(), value);
				}
				if (bus != null) {
					target.set(bus);
				}
			} else if (target instanceof Mem) {
				int width = ((Mem) target).width;
				Mem mem = new Mem(width);

				if (value.contains("{")) {
					String[] expressions = value.replace("{", "").replace("}", "").replace(";", "").split(",");

					for (String e : expressions) {
						e = e.trim();
						if (e.startsWith("~")) {
							// size of empty memory slots
							int size = math.evaluate(31, e.replace("~", "")).toInt();
							for (int j = 0; j < size; j++) {
								mem.add(new Register(width, 0));
							}
						} else {
							Register w = math.evaluate(width, e);
							mem.add(w);
						}
					}
				} else {
					Variable copy = scope.getVariable(value.trim());
					if (copy instanceof Mem) {
						mem.set((Mem) copy);
					} else {
						// type error
					}
				}
				target.set(mem);
			} else if (target == null && name.contains("{")) {
				// set mem slot value
				target = scope.getVariable(name.substring(0, name.indexOf("{")).trim());
				String indexString = name.substring(name.indexOf("{") + 1, name.indexOf("}")).trim();
				if (target != null) {
					Mem m = (Mem) target;
					int index = math.evaluate(31, indexString).toInt();

					m.list.get(index).set(math.evaluate(m.width, value));
				} else {
					// error
				}
			}
		}
	}

	public String getUntillTerminate(String script, int index, String terminate) {
		// get tokens until token is equals to terminate
		String expression = "";
		String next = getNextToken(script, index);

		while (index < script.length()) {
			expression += next;
			index += next.length();
			if (!next.trim().equals(terminate.trim())) {
				next = getNextToken(script, index);
			} else {
				break;
			}
		}

		return expression;
	}

	public String getNextToken(String script, int index) {
		String part = script.substring(index);
		String token = "";
		for (int i = 0; i < part.length(); i++) {
			char nextChar = part.charAt(i);

			if (MathEngine.arrayContains(keysymbols, nextChar + "")) {
				if (token.length() == 0) {
					token += nextChar;
					break;
				} else {
					break;
				}
			} else if (Character.isWhitespace(nextChar)) {
				token += nextChar;
				break;

			} else {
				token += nextChar;
			}

		}
		return token;

	}

}
