package com.rpg.game;

import com.badlogic.gdx.math.MathUtils;

public class Gib {
	
	float rotation;
	float deltaRotation;
	float x;
	float y;
	float deltaX;
	float deltaY;
	float lifetime;
	
	public Gib(float x1, float y1) {
		x = x1;
		y = y1;
		rotation = MathUtils.random(0, 360);
		deltaRotation = MathUtils.random(-3, 3);
		lifetime = 0;
		deltaX = MathUtils.random(-400, 400);
		deltaY = MathUtils.random(-1500, 1500);
	}
}
