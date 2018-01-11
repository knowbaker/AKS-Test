package com.singh.primes;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerAKS extends BigInteger {
	private double log2n;

	public BigIntegerAKS(String val) {
		super(val);
		this.log2n = log()/Math.log(2);
	}
	
	public BigInteger logSquared() {
		double logSq = log2n * log2n;
		return get(logSq);//equivalent of taking floor of the bigdecimal or floor( (log2(n))^2 )
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
	
	/*
	 * Find the smallest r such that order of n (mod r) > log^2(n).
	 */
	public BigInteger calculateR() {
		BigInteger k = logSquared();
		BigInteger q = k.add(BigInteger.ONE);
		BigInteger i = BigInteger.ONE;
		for(; ; q = q.add(BigInteger.ONE)) {
			for(; i.compareTo(k) <= 0; i = i.add(BigInteger.ONE))
				if(modPow(i, q).equals(BigInteger.ONE)) //since 1 (mod q) = 1
					break;
				
			if(i.compareTo(k) > 0)//found it
				return q;
		}
	}
	
	private double calculateY(double x, int b, BigInteger p, BigInteger xBig) {
		return (b-1)*x + Math.floor(p.divide(xBig.pow(b - 1)).doubleValue()); 
	}

	public double log() {
		// from http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
		BigInteger b;

		int temp = bitLength() - 1000;
		if (temp > 0) {
			b = shiftRight(temp);
			return Math.log(b.doubleValue()) + temp * Math.log(2);
		} else
			return Math.log(doubleValue());// * Math.log(2));
	}
	
}
