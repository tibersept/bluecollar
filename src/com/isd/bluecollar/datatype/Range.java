/**
 * 
 */
package com.isd.bluecollar.datatype;

/**
 * A range data type.
 * @author doan
 */
public class Range<T> {
	
	private T begin;
	private T end;
	
	/**
	 * Creates a new range.
	 * @param aBegin the range begin
	 * @param anEnd the range end
	 */
	public Range( T aBegin, T anEnd ) {
		begin = aBegin;
		end = anEnd;
	}

	/**
	 * Returns the range begin.
	 * @return the range begin
	 */
	public T getBegin() {
		return begin;
	}

	/**
	 * Sets the range begin.
	 * @param aBegin the range begin
	 */
	public void setBegin(T aBegin) {
		begin = aBegin;
	}

	/**
	 * Returns the range end.
	 * @return the range end
	 */
	public T getEnd() {
		return end;
	}

	/**
	 * Sets the range end. 
	 * @param anEnd the range end
	 */
	public void setEnd(T anEnd) {
		end = anEnd;
	}
	
}
