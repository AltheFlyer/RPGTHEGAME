package com.rpg.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class RPG extends ApplicationAdapter {
	
	SpriteBatch batch;
	Rectangle character;
	Texture characterSprite;
	Texture rocketSprite;
	Texture zombieSprite;
	Texture explosionSprite;
	Texture gibSprite;
	Sprite rpg;
	Sprite particle;
	OrthographicCamera camera;
	float yVelocity;
	Array<Rocket> rockets;
	Array<Explosion> explosions;
	Array<Zombie> zombies;
	Array<Gib> gibs;
	float cooldown;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		character = new Rectangle(175, 20, 50, 120);
		characterSprite = new Texture("Character.png");
		rocketSprite = new Texture("Rocket.png");
		zombieSprite = new Texture("Zombie.png");
		explosionSprite = new Texture("Explosion.png");
		gibSprite = new Texture("Gib.png");
		rpg = new Sprite();
		rpg.setRegion(rocketSprite);
		particle = new Sprite();
		particle.setRegion(gibSprite);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 400);
		yVelocity = 0;
		cooldown = 0;
		rockets = new Array<Rocket>();
		explosions = new Array<Explosion>();
		zombies = new Array<Zombie>();
		gibs = new Array<Gib>();
	}

	@Override
	public void render () {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(characterSprite, character.x, character.y);
		//Draw rockets
		for (Rocket rocket: rockets) {
			//System.out.println(Math.atan2(rocket.deltaY, rocket.deltaX));
			rpg.setOrigin(12, 12);
			rpg.setRotation((float) Math.atan2(rocket.deltaY, rocket.deltaX) * MathUtils.radiansToDegrees - 90);
			rpg.setBounds(rocket.hitbox.x, rocket.hitbox.y, 25, 25);
			rpg.draw(batch);
		}
		//Draw explosions
		for (Explosion explosion: explosions) {
			batch.draw(explosionSprite, explosion.x, explosion.y);
		}
		//Draw zombies
		for (Zombie zombie: zombies) {
			batch.draw(zombieSprite, zombie.hitbox.x, zombie.hitbox.y);
		}
		//Draw Gibs
		for (Gib gib: gibs) {
			particle.setOrigin(15, 5);
			particle.setRotation(gib.rotation);
			particle.setBounds(gib.x, gib.y, 30, 10);
			particle.draw(batch);
		}
		batch.end();
		
		//Character Controls
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			character.x -= 200 * Gdx.graphics.getDeltaTime();
			//camera.position.x -= 140 * Gdx.graphics.getDeltaTime();
			if (character.x < 0) {
				character.x = 0;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			character.x += 200 * Gdx.graphics.getDeltaTime();
			if (character.x > 350) {
				character.x = 350;
			}
		}
		//Jumping
		if ((yVelocity == 0 && character.y == 20) && (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))) {
			yVelocity = 30;
		}
		
		//Gravity
		character.y += yVelocity;
		if (character.y < 20) {
			character.y = 20;
			yVelocity = 0;
		} else {
			yVelocity -= 120 * Gdx.graphics.getDeltaTime();
		}
		
		//Rocket creation
		if (Gdx.input.isTouched() && cooldown <= 0) {
			Vector3 mousePos = new Vector3();
			camera.unproject(mousePos.set(Gdx.input.getX(),Gdx.input.getY(),0));
			float toX = (float) Math.cos(Math.atan2(mousePos.y - (character.y + 60), mousePos.x - (character.x + 25))) * 700;
			float toY = (float) Math.sin(Math.atan2(mousePos.y - (character.y + 60), mousePos.x - (character.x + 25))) * 700;
			rockets.add(new Rocket(character.x + 25, character.y + 60, toX, toY));
			cooldown = 0.2f;
		}
		
		//Rocket moving
		for (Rocket rocket : rockets) {
			rocket.hitbox.x += rocket.deltaX * Gdx.graphics.getDeltaTime();
			rocket.hitbox.y += rocket.deltaY * Gdx.graphics.getDeltaTime();
			rocket.deltaY -= 25;
		}
		
		//Mark rockets and zombies as hit
		Array<Zombie> tempz = new Array<Zombie>();
		for (Rocket rocket: rockets) {
			if (rocket.hitbox.y <= 20 || rocket.hitbox.x < 0 || rocket.hitbox.x > 375) {
				rocket.hit = true;
			}
			for (Zombie zombie: zombies) {
				if (rocket.hitbox.overlaps(zombie.hitbox)) {
					explosions.add(new Explosion(rocket.hitbox.x, rocket.hitbox.y));
					zombie.hit = true;
					rocket.hit = true;
				}
			}
		}
		
		//Rockets removal
		Iterator<Rocket> iter = rockets.iterator();
		while (iter.hasNext()) {
			Rocket rocket = iter.next();
			if (rocket.hit) {
				explosions.add(new Explosion(rocket.hitbox.x, rocket.hitbox.y));
				iter.remove();
			}
		}
		
		//Zombies removal
		Iterator<Zombie> ziter = zombies.iterator();
		while (ziter.hasNext()) {
			Zombie zombie = ziter.next();
			if (zombie.hit) {
				ziter.remove();
				//Add gibs
				for (int i = 0; i < MathUtils.random(1, 5); i ++) {
					gibs.add(new Gib(zombie.hitbox.x + 15, zombie.hitbox.y + 60));
				}
			}
		}
		
		//Explosion advance / removal
		Iterator<Explosion> iter1 = explosions.iterator();
		Array<Explosion> temporary = new Array<Explosion>();
		while (iter1.hasNext()) {
			Explosion explosion = iter1.next();
			if (explosion.time < 0) {
				iter1.remove();
			} else {
				explosion.time -= Gdx.graphics.getDeltaTime();
				temporary.add(explosion);
			}
		}
		explosions = temporary;
		
		//cooldown advance
		if (cooldown > 0) {
			cooldown -= Gdx.graphics.getDeltaTime();
		}
		
		//Zombie moving
		tempz = new Array<Zombie>();
		for (Zombie zombie: zombies) {
			if (zombie.hitbox.x < character.x) {
				zombie.hitbox.x += 100 * Gdx.graphics.getDeltaTime();
			} else if (zombie.hitbox.x > character.x) {
				zombie.hitbox.x -= 100 * Gdx.graphics.getDeltaTime();
			}
			
			if ((zombie.yVelocity == 0 && zombie.hitbox.y == 20) && (character.y > zombie.hitbox.y)) {
				zombie.yVelocity = 30;
			}
			
			//Gravity
			zombie.hitbox.y += zombie.yVelocity;
			if (zombie.hitbox.y < 20) {
				zombie.hitbox.y = 20;
				zombie.yVelocity = 0;
			} else {
				zombie.yVelocity -= 120 * Gdx.graphics.getDeltaTime();
			}
			
			tempz.add(zombie);
		}
		zombies = tempz;
		
		//Zombie Spawning
		int rand = MathUtils.random(100);
		if (rand > 95) {
			if (rand % 2 == 0) {
				zombies.add(new Zombie(0, 20, MathUtils.random(0, 50)));
			} else {
				zombies.add(new Zombie(400,  20, MathUtils.random(0, 50)));
			}
		}
		
		//Gib stuff
		Iterator<Gib> giter = gibs.iterator();
		while (giter.hasNext()) {
			Gib gib = giter.next();
			//Move the gib
			gib.x += gib.deltaX * Gdx.graphics.getDeltaTime();
			gib.y += gib.deltaY * Gdx.graphics.getDeltaTime();
			//gib gravity
			gib.deltaY -= 20;
			//bouncing gibs (no ceiling)
			if (gib.x < 0) {
				gib.x = 0;
				gib.deltaX *= -0.98;
				gib.deltaRotation *= 0.98;
			}
			if (gib.x > 400) {
				gib.x = 400;
				gib.deltaX *= -0.98;
				gib.deltaRotation *= 0.98;
			}
			if (gib.y < 20) {
				gib.y = 20;
				gib.deltaY *= -0.8;
				gib.deltaRotation *= 0.98;
			}
			//rotation change
			gib.rotation += gib.deltaRotation * 100 * Gdx.graphics.getDeltaTime();
			//gib lifetime
			gib.lifetime += Gdx.graphics.getDeltaTime();
			if (gib.lifetime > 10) {
				giter.remove();
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		characterSprite.dispose();
		rocketSprite.dispose();
		zombieSprite.dispose();
		explosionSprite.dispose();
		gibSprite.dispose();
	}
}
