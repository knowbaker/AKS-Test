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
		return fromDouble(log2n * log2n);//equivalent to taking floor of the bigdecimal or floor( (log2(n))^2 )
	}
	
	/**
	 * Step 1: If (n = a^b for a ∈ N and b > 1), output COMPOSITE.	 
	 * TODO: If bth root returns even value it has to be perfect power so no need to explore further
	 * This short cut is mentioned in "On the implementation of AKS-class primality tests by R. Crandall and J. Papadopoulos"
	 */
	public boolean isPerfectPower() {
		log2n = log2n == 0 ? log() : log2n;
		for (int i = 2; i <= log2n; ++i) {
			double ithRootOfP = bthRoot(i);
			BigInteger ithRootOfPBig = fromDouble(ithRootOfP);
			if (this.equals(ithRootOfPBig.pow(i)))
				return true;
		}
		return false;
	}
	
	/**
	 * Step 2: Find the smallest r such that order of n (mod r) > log^2(n).
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
	
	/**
	 * Step 3: If 1 < (a,n) < n for some a <= r, output COMPOSITE
	 */
	public boolean gcdExists() {
		BigInteger r = calculateR();
		for(BigInteger a = new BigInteger("2"); a.compareTo(r) <= 0; a = a.add(BigInteger.ONE)) {
			BigInteger gcdOfAAndN = gcd(a);
			if(gcdOfAAndN.compareTo(BigInteger.ONE) > 0 && gcdOfAAndN.compareTo(this) < 0)
				return true;
		}
		return false;
	}
	
	/**
	 * The order of this modulo r is the smallest number k such that this^k = 1 (mod r)
	 */
	public BigInteger orderOfNModR(BigInteger r) {
		BigInteger k = BigInteger.ONE;
		for(; !modPow(k, r).equals(BigInteger.ONE); k = k.add(BigInteger.ONE));
		return k;
	}
	
	/**
	 * Reference: http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
	 */
	public double log() {		
		BigInteger b;
		int temp = bitLength() - 1000;
		if (temp > 0) {
			b = shiftRight(temp);
			return Math.log(b.doubleValue()) + temp * Math.log(2);
		} else
			return Math.log(doubleValue());// * Math.log(2));
	}
		
	public BigInteger fromDouble(double d) {
		return new BigDecimal(d).toBigInteger();
	}
	
	/**
	 * Calculate bth root of a number using Newton Raphson method
	 * Reference: "On the implementation of AKS-class primality tests by R. Crandall and J. Papadopoulos"
	 * 
	 * @param  b the number used to calculate this^(1/b)
	 * @return the value of {@code this} is raised to power 1/{@code b}
	 */
	protected double bthRoot(int b) {
		int numBits = bitLength();
		double x = Math.pow(2, Math.ceil( ((double)numBits) / ((double)b) ));
		double y = 0;
		//TODO: Fix bug of infinite loop
		//UPDATE: Changing y == x to y >= x fixes it. Verify further.
		while(y <= x) {
			y = Math.floor(calculateY(x, b, this, fromDouble(x)) / b);
			if(y >= x) {
				break;
			}
			x = y;
		}
		return x;
	}
	
	/**
	 * Basic implementation of totient function
	 * TODO: Improve time complexity
	 * @param r
	 * @return φ(n)
	 */
	protected BigInteger totient(BigInteger r) {
		if(r.equals(BigInteger.ONE))
			return BigInteger.ONE;
		
		BigInteger phi = BigInteger.ZERO;		
		for(BigInteger i = BigInteger.ONE; i.compareTo(r) < 0; i = i.add(BigInteger.ONE))
			if(r.gcd(i).equals(BigInteger.ONE))
				phi = phi.add(BigInteger.ONE);
		
		return phi;
	}

	private double calculateY(double x, int b, BigInteger p, BigInteger xBig) {
		return (b-1)*x + Math.floor(p.divide(xBig.pow(b - 1)).doubleValue()); 
	}
}
