package grupo.bolado.entities;

import static grupo.bolado.Game.camera;
import static grupo.bolado.Game.rayHandler;
import grupo.bolado.Game;
import grupo.bolado.LevelManager;
import grupo.bolado.utils.AnimationContainer;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Plu
{
	AnimationContainer idle, jump, jumpEffect, move, dash, die, victory;
	AnimationContainer currentAnim;
	
	PointLight pluLight;
	public Body pluBody;
	PointLight pl;
	
	// Big Plu
	public Fixture bigPluFixture;
	public FixtureDef bigPluFixtureDef;
	public CircleShape bigPluShape;
	public float bigBodyRadiusGround = .60f;
	
	// Small Plu
	public Fixture smallPluFixture;
	public FixtureDef smallPluFixtureDef;
	public CircleShape smallPluShape;
	public float smallBodyRadiusGround = .30f;
	
	float idleTimer;
	float timeTillIdle = 1f;
	
	// Plu states
	public boolean onTheGround = false;
	public boolean jumping = false;
	public boolean falling = true;
	public boolean goingRight = true;
	public boolean isGrabbed = false;
	public boolean isDashing = false;
	public boolean canDash = false;
	public boolean isLilWayne = false;
	public boolean isDying = false;
	public boolean levelComplete = false;
	
	public int lumensCollected = 0;
	
	float speedX;
	float accel = 25.0f;
	float maxSpeed = 20.0f;
	float minSpeed = 7.0f;
	float jumpInitialSpeed = 23.0f;
	float maxFallSpeed = -25.0f;
	float dashSpeed = 40f;
	
	public static Sound jumpSound, deadSound;
	
	public static Plu singleton;
	
	
	
	public Plu()
	{		
		singleton = this;
		
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("assets/PluJump.mp3"));
		deadSound = Gdx.audio.newSound(Gdx.files.internal("assets/PluDead.mp3"));
		
		
		// Plu's sprite size
//		super.setSize(pluW, pluH);
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		
		// Plu's initial position
//		float initialPosX = Main.root.getChildByName("plu").getChildByName("animations")

		
		pluBody = Game.b2World.createBody(circleDef);
		
		
		// ------- //
		// BIG PLU //
		// ------- //
		bigPluFixtureDef = new FixtureDef();
		
		// Plu's shape size (collisions)
		bigPluShape = new CircleShape();
		bigPluShape.setRadius(bigBodyRadiusGround);
		
		// Plu's settings (physics)
		bigPluFixtureDef.shape = bigPluShape;
		bigPluFixtureDef.density = 1.0f;
		bigPluFixtureDef.friction = 0.0f;
		bigPluFixtureDef.restitution = 0.0f;
		bigPluFixtureDef.filter.groupIndex = 1;
		bigPluFixture = pluBody.createFixture(bigPluFixtureDef);
		
//		bigPluShape.dispose();
		
		// --------- //
		// SMALL PLU //
		// --------- //
		smallPluFixtureDef = new FixtureDef();
		
		// Plu's shape size (collisions)
		smallPluShape = new CircleShape();
		smallPluShape.setRadius(smallBodyRadiusGround);
		
		// Plu's settings (physics)
		smallPluFixtureDef.shape = smallPluShape;
		smallPluFixtureDef.density = 1.0f;
		smallPluFixtureDef.friction = 0.0f;
		smallPluFixtureDef.restitution = 0.0f;
		smallPluFixtureDef.filter.groupIndex = 1;
		// --------- //
		
//		smallPluShape.dispose();
		
		// Identify it for later use in collision detection
		pluBody.setUserData(IDs.PLU_ID);
		
		// Load Plu's animations
		idle = new AnimationContainer("Idle", "plu");
		jump = new AnimationContainer("Jump", "plu");
		jumpEffect = new AnimationContainer("JumpEffect", "plu");
		move = new AnimationContainer("Move", "plu");
		dash = new AnimationContainer("DashAir", "plu");
		die = new AnimationContainer("Die", "plu");
		victory = new AnimationContainer("Victory", "plu");
		
		// First animation is Idle
		currentAnim = idle;

		// Plu's light
		pluLight = new PointLight(rayHandler, 600, new Color(1,1,1,0.0f), 50, 0, 0);
	}
	
	public void changeForm()
	{
		if (!isLilWayne)
		{
			Lumen.targetBaseY = -0.1f;
			
			isLilWayne = true;
			// Changing the fixture of pluBody (setting to the small one)
			smallPluFixture = pluBody.createFixture(smallPluFixtureDef);
			pluBody.destroyFixture(bigPluFixture);
			
			// Slow settings
			accel = accel/2; maxSpeed = maxSpeed/2; minSpeed = minSpeed/2; jumpInitialSpeed = jumpInitialSpeed/2; dashSpeed = dashSpeed/2; 
		}
		else
		{
			Lumen.targetBaseY = 1.1f;
			
			isLilWayne = false;
			// Changing the fixture of pluBody (setting to the big one)
			pluBody.createFixture(bigPluFixtureDef);
			pluBody.destroyFixture(smallPluFixture);
			
			// Normal settings
			accel = accel*2; maxSpeed = maxSpeed*2; minSpeed = minSpeed*2; jumpInitialSpeed = jumpInitialSpeed*2; dashSpeed = dashSpeed*2; 
		}
	}
	
	
	public void dash() 
	{
		if (canDash && !onTheGround) {
			
			currentAnim = dash;
			currentAnim.goToFrame(0);
			
			float duration = 0.3f; // seconds
			isDashing = true;
			canDash = false;
			
			pluBody.setGravityScale(0);
			if (goingRight)
				pluBody.setLinearVelocity(dashSpeed, 0);
			else
				pluBody.setLinearVelocity(-dashSpeed, 0);
			
			Timer.schedule(new Task(){
			    @Override
			    public void run() {
			    	pluBody.setGravityScale(1);
			    	pluBody.setLinearVelocity(maxSpeed, 0);
			    	isDashing = false;
			    }
			}, duration);
		}
	}
	
	public void setPosition(Vector2 position)
	{
		pluBody.setTransform(position , 0f);
		camera.position.set(position, 0f);
	}
	
	public void hitTheCeiling()
	{
		jumping = false;
		pluBody.setLinearVelocity(pluBody.getLinearVelocity().x, 0f);
	}
	
	public void update()
	{
		
		float dt = Gdx.graphics.getDeltaTime();
		
		boolean moveKeyPressed = false;
		
		// Move right
		if (Gdx.input.isKeyPressed(Keys.RIGHT) && !isGrabbed && !isDashing && !isDying) 
		{
			pluBody.setLinearVelocity(pluBody.getLinearVelocity().add(+accel * dt, 0));
			
			if (pluBody.getLinearVelocity().x < minSpeed)
			{
				pluBody.setLinearVelocity(minSpeed, pluBody.getLinearVelocity().y);
			}
			
			if (pluBody.getLinearVelocity().x > maxSpeed)
			{
				pluBody.setLinearVelocity(maxSpeed, pluBody.getLinearVelocity().y);
			}
			
			goingRight = true;
			moveKeyPressed = true;
		}
		
		// Move left
		else if (Gdx.input.isKeyPressed(Keys.LEFT)  && !isGrabbed && !isDashing && !isDying) 
		{
			pluBody.setLinearVelocity(pluBody.getLinearVelocity().add(-accel * dt, 0));
			
			if (pluBody.getLinearVelocity().x > -minSpeed)
			{
				pluBody.setLinearVelocity(-minSpeed, pluBody.getLinearVelocity().y);
			}
			
			if (pluBody.getLinearVelocity().x < -maxSpeed)
			{
				pluBody.setLinearVelocity(-maxSpeed, pluBody.getLinearVelocity().y);
			}
			
			goingRight = false;
			moveKeyPressed = true;
		}
		
		
		// Neither left or right arrow being pressed?
		if (!moveKeyPressed && !isDashing)
		{
			// Damp X movement until 0
			pluBody.setLinearVelocity(pluBody.getLinearVelocity().x*0.6f, pluBody.getLinearVelocity().y);
			
			if(onTheGround)
			{
				currentAnim = idle;
				
				idleTimer += Gdx.graphics.getDeltaTime();
				
				if (idleTimer > timeTillIdle)
				{
					idle.freeze = false;
				}
				else
				{
					currentAnim.goToFrame(0);
					currentAnim.freeze = true;
				}
			}
		}
		else
		{
			idleTimer = 0f;

			if (onTheGround)
			{
				currentAnim = move;
			}
		}
		
		// Falling or not?
		if (pluBody.getLinearVelocity().y < 0.0f)
		{
			jumping = false;
			falling = true;
			
			if (pluBody.getLinearVelocity().y < maxFallSpeed)
			{
				pluBody.setLinearVelocity(pluBody.getLinearVelocity().x, maxFallSpeed);
			}
			
			currentAnim = jump;
			currentAnim.goToFrame(8+(int)(pluBody.getLinearVelocity().y/-2.5f));
			currentAnim.freeze = true;
		}
		else
		{
			falling = false;
		}

		jumpCounter--;
		if (jumpCounter<0) jumpCounter=0;
		
		// Jump
		if ( Gdx.input.isKeyPressed(Keys.UP) && (onTheGround || isGrabbed) && !isDying) 
		{
			if (jumpCounter==0) 
			{
				jumpSound.play();
				jumpCounter = 20;
			}
			
			pluBody.setLinearVelocity(pluBody.getLinearVelocity().x, jumpInitialSpeed);
			jumping = true;
			onTheGround = false;
			isGrabbed = false;
//			Gdx.app.log("", PlantEater.attackingPlant+"");
			if(PlantEater.attackingPlant != -1)
			{
				PlantEater attackingPlant = LevelManager.plantsEater.get(PlantEater.attackingPlant);
				attackingPlant.isGrabbingPlu = false;
				attackingPlant.jumpCounter = 5;
			}
			
			currentAnim = jump;
			currentAnim.goToFrame(4);
			
			idleTimer = 0f;
		}
		
		if (isGrabbed)
		{
			currentAnim = jump;
			jump.goToFrame(7);
			jump.freeze = true;
			canDash = true;
		}
		
		// Well. Obvious, right?
		if (onTheGround)
		{
			canDash = true;
			jumping = false;
		}
		
		// Jump cancelled?
		if (!Gdx.input.isKeyPressed(Keys.UP) && pluBody.getLinearVelocity().y > 0.1f) 
		{
			jumping = false;
			pluBody.setLinearVelocity(pluBody.getLinearVelocity().x, pluBody.getLinearVelocity().y*0.9f);
		}
		
		if (isDying) 
		{
			deadSound.play();
			
			pluBody.setGravityScale(0);
			pluBody.setLinearVelocity(0, 0);
			
			die.looping = false;
			currentAnim = die;
			
			// Animation ended
			if(currentAnim.animationInstant == currentAnim.animation.animationDuration)
			{
				LevelManager.singleton.shouldRestart = true;
			}
		}
		
		if (levelComplete) 
		{
			pluBody.setGravityScale(0);
			pluBody.setLinearVelocity(0, 0);
			
			victory.looping = false;
			currentAnim = victory;
			
			if(currentAnim.animationInstant == currentAnim.animation.animationDuration)
			{
				LevelManager.singleton.shouldGoToNextLevel = true;
			}
		}
		
		if (pluBody.getPosition().y < LevelManager.cameraHandler.bottomLimit/32f)
		{
			isDying = true;
		}

		// Play it's animation
		currentAnim.play();
		
		pluLight.setPosition(pluBody.getPosition());
	}
	int jumpCounter;
	
	public void render()
	{
		if(isLilWayne)
		{
			currentAnim.sizeMultiplier = .5f;
		}
		else
		{
			currentAnim.sizeMultiplier = 1.0f;
		}
		
		currentAnim.render(pluBody.getPosition());
		currentAnim.flipVertically = goingRight;
	}
	
	public void dispose()
	{
		Game.b2World.destroyBody(pluBody);
	}
}	