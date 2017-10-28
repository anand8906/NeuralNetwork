package univ.uwm.cs760.nn.util;

public class MathFunctions {
    
	public static double calculateSigmoid(double x)
	{
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}
}
