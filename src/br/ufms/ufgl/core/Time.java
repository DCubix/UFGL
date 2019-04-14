package br.ufms.ufgl.core;

/**
 *
 * @author Twister
 */
public class Time {
	
	public static double get() {
		return (double) System.nanoTime() / 1_000_000_000.0;
	}
	
}
