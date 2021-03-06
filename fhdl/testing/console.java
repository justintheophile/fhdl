package fhdl.testing;

import java.util.ArrayList;

/**
 * 
 * Simple implementation of a logger. Used for debugging.
 * 
 * @param priorityLevel is used to sort. when negative, no messages will be
 *                      printed. however, when 0 or above, only messages with a
 *                      priority that is >= to that of the priority level will
 *                      be printed.
 * 
 * @guidline priority values: 0 = not important message, 1 = regular message, 2
 *           = non fatal error, 3 and above = fatal error of varying importance
 * 
 * 
 */
public class console {
	// I know this breaks naming conventions but I'm just too used to javascript at
	// this point
	public static int priorityLevel = 1;
	public static boolean showInfo = false;

	/**
	 * 
	 * @param priority the priority of the message
	 * @param message  what is to be sent to the console
	 */
	public static void log(int priority, Object message) {
		logs.add(message.toString());

		if (priorityLevel >= 0 && priority >= priorityLevel) {
			if (showInfo) {
				String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
				String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
				String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
				int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
				message = className + ": " + lineNumber + "> " + message;
			}
			if (priority >= 5) {
				System.err.println(message);
			} else {
				System.out.println(message);
			}
		}
	}

	public static void log(Object message) {
		log(0, message);
	}

	public static ArrayList<String> logs = new ArrayList<String>();
}
