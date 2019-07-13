package emulation.fhdl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import emulation.console;

public class Script {
	private static final String keywords[] = ("# entity goto goif sleep import end instance new print "
			+ "bus1 bus2 bus3 bus4 bus5 bus6 bus7 bus8 bus9 bus10 bus11 bus12 bus13 bus14 bus15 bus16 bus17 bus18 bus19 bus20 bus21 bus22 bus23 bus24 bus25 bus26 bus27 bus28 bus29 bus30 bus31 bus32")
					.split(" ");
	private static final String keysymbols[] = "{ } = ; ( ) , . + ^ ! * & |".split(" ");
	private ScopeController scope;
	private MathEngine math;

	public Script(ScopeController scope, MathEngine math) {
		this.math = math;
		this.scope = scope;
	}

	public void runFile(String path) {
		String contents;
		try {
			contents = new String(Files.readAllBytes(Paths.get(path)));
			run(contents);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(String script) {
		for (int i = 0; i < script.length();) {
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

						// get entity script and skip as to not execute empty entity
						String entityScript = getUntillTerminate(script, i, "end");
						i += entityScript.length();
						newEntity.body = entityScript;
					} else if (token.startsWith("bus")) {
						// initialize variable
						String var = getUntillTerminate(script, i, ";");
						processVariable(token + " " + var);
						i += var.length();
					} else if (token.equals("end")) {
						// ends entity definition
						String entityName = scope.getTopScope();
						scope.exitScope();
						console.log(entityName);
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
						console.log(1, varName + " > " + entityName + " > " + entityParameters);

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
						run(template.body);

						// exit instance scope
					} else if (token.equals("print")) {
						String params = getUntillTerminate(script, i, ")");
						i += params.length();
						params = params.trim().substring(1, params.length() - 1);
						String[] split = params.split(",");
						int lineNumber = script.substring(0, i).split("\n").length;
						console.log(3, lineNumber + ":" + i + " -> "
								+ math.evaluate(math.evaluate(64, split[0]).toInt(), split[1].trim()));
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
					}else if(token.equals("sleep")) {
						String params = getUntillTerminate(script, i, ")");
						i += params.length();
						params = params.trim().substring(1, params.length() - 1);
						String[] split = params.split(",");
						String time = split[0].trim();
						int millis = math.evaluate(32, time).toInt();
						try {
							Thread.sleep(millis);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					if (MathEngine.arrayContains(keysymbols, token)) {

					} else if (token.startsWith("#")) {

					} else {
						String var = getUntillTerminate(script, i, ";");
						processVariable(token + " " + var);
						i += var.length();
					}
				}
			}
		}
	}

	public void processVariable(String var) {

		String type = "";
		String name = "";
		String value = "";
		if (var.startsWith("bus")) {
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
		console.log(type + ": " + name + ": " + value);

		if (type.length() > 0) {
			// initializing variable
			Bus bus = null;
			if (type.startsWith("bus")) {
				int width = Integer.parseInt(type.substring(3));

				if (value.length() > 0) {
					bus = math.evaluate(width, value);
				} else {
					bus = new Bus(width, 0);
				}
			}

			if (bus != null) {
				scope.createVariable(name, bus);
				Bus w = new Bus(8, bus.getWidth());
				if (name.endsWith("_")) {
					scope.createVariable(name + ".width_", w);
				} else {
					scope.createVariable(name + ".width", w);

				}
			}
		} else {
			Bus bus = null;
			Bus target = (Bus) scope.getVariable(name);

			if (value.length() > 0) {
				bus = math.evaluate(target.getWidth(), value);
			}
			if (bus != null) {
				target.set(bus);
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
