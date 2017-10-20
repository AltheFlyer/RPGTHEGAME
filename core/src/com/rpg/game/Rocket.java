package com.rpg.game;

import com.badlogic.gdx.math.Rectangle;

public class Rocket {
	
	Rectangle hitbox;
	float direction;
	float deltaX;
	float deltaY;
	boolean hit;
	
	public Rocket (float x1, float y1, float dx, float dy) {
		hitbox = new Rectangle(x1, y1, 25, 25);
		deltaX = dx;
		deltaY = dy;
		hit = false;
	}
	
	public void move() {
		
	}
}
