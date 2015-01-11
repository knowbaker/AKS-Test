package com.singh.primes;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerAKS extends BigInteger {
	private double log2n;

	public BigIntegerAKS(String val) {
		super(val);
		this.log2n = log();
	}
	
	public BigInteger logSquared() {
		BigInteger log = get(log2n);
		return log.multiply(log);
	}
	
	/*
	 * the order of this modulo r is the smallest number k such that this^k = 1 (mod r)
	 */
	public BigInteger orderOfNModR(BigInteger r) {
		BigInteger k = BigInteger.ONE;
		for(; !modPow(k, r).equals(BigInteger.ONE); k = k.add(BigInteger.ONE));
		return k;
	}

	// TODO: If bth root returns even value it has to be perfect power so no need to explore further
	// This short cut is mentioned in "On the implementation of AKS-class primality tests by R. Crandall and J. Papadopoulos"
	public boolean isPerfectPower() {
		log2n = log2n == 0 ? log() : log2n;
		for (int i = 2; i <= log2n; ++i) {
			double ithRootOfP = bthRoot(i);
			BigInteger ithRootOfPBig = get(ithRootOfP);
			if (this.equals(ithRootOfPBig.pow(i))) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Calculate bth root of a number using Newton Raphson method
	 * Algorithm taken from "On the implementation of AKS-class primality tests by R. Crandall and J. Papadopoulos"
	 * 
	 * @param  b the number used to calculate this^(1/b)
	 * @return the value of {@code this} is raised to power 1/{@code b}
	 */
	public double bthRoot(int b) {
		int numBits = bitLength();
		double x = Math.pow(2, Math.ceil( ((double)numBits) / ((double)b) ));
		double y = 0;
		//TODO: Fix bug of infinite loop
		//UPDATE: Changing y == x to y >= x fixes it. Verify further.
		while(y <= x) {
			y = Math.floor(calculateY(x, b, this, get(x)) / b);
			if(y >= x) {
				break;
			}
			x = y;
		}
		return x;
	}
	
	public BigInteger get(double d) {
		return new BigDecimal(d).toBigInteger();
	}
	
	private double calculateY(double x, int b, BigInteger p, BigInteger xBig) {
		return (b-1)*x + Math.floor(p.divide(xBig.pow(b - 1)).doubleValue()); 
	}

	private double log() {
		// from http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
		BigInteger b;

		int temp = bitLength() - 1000;
		if (temp > 0) {
			b = shiftRight(temp);
			return (Math.log(b.doubleValue()) + temp) * Math.log(2);
		} else
			return (Math.log(doubleValue()) * Math.log(2));
	}
	
}
