package com.rpg.game;

import com.badlogic.gdx.math.Rectangle;

public class Zombie {
	
	Rectangle hitbox;
	float yVelocity;
	boolean hit;
	
	public Zombie(float x, float y, float vel) {
		hitbox = new Rectangle(x, y, 30, 120);
		hit = false;
		yVelocity = vel;
	}
}
