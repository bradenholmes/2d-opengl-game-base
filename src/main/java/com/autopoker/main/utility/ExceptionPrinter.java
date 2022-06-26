package com.autopoker.main.utility;

public class ExceptionPrinter
{
	/**
	 * Prints an exception to the console.
	 * @param e Exception
	 * @param occasionDescription a description of when this occurred. Format:  exception occured while ' {desc} '
	 */
	public static void print(Exception e, String occasionDescription) {
		System.out.println(" ------ An exception occurred while '" + occasionDescription + "' ------ ");
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
}
