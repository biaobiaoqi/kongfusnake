package com.snake;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int newX, int newY) {
        x = newX;
        y = newY;
    		}


    public boolean equals(Coordinate other) {
    			if (x == other.x && y == other.y) {
            return true;
    				}
    			return false;
    		}
}