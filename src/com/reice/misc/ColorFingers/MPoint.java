package com.reice.misc.ColorFingers;

public class MPoint {

	public double x, y;

	// This always refers to an object at (0,0)
	MPoint(double x_value, double y_value) {
		x = x_value;
		y = y_value;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void clear() {
		this.x = 0;
		this.y = 0;
	}

	public double distance(MPoint that) {
		double xDiff = x - that.x;
		double yDiff = y - that.y;
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

}
