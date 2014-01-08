package grupo.bolado;

import static grupo.bolado.Game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;

import static grupo.bolado.LevelManager.*;

public class CameraHandler 
{
	// Camera settings
	public static float zoom;
	public static float pixelsPerUnit = 32f;
	public static float unitsPerPixel = 1f / pixelsPerUnit;
	
	float followToleranceLeft, followToleranceRight;
	float followToleranceTotal = 0f;
	
	float cameraMovementAhead = 0f, cameraMovementAheadCap = 2.5f, moveAheadSpeed = .4f;
	
	boolean lookRight = true;
	
	// Limits
	public float leftLimit, rightLimit, topLimit, bottomLimit;
	Ellipse el0;
	Ellipse el1;
	
	public CameraHandler(float pluPosX)
	{
		// Initial tolerance values
		followToleranceLeft  = pluPosX - followToleranceTotal/2f;
		followToleranceRight = pluPosX + followToleranceTotal/2f;
		
		// Get some parameters from the xml file
		zoom = Main.root.getChildByName("game").getFloat("zoom");
		
		// Initial camera size setting
		resize();
	}
	
	public void updateLevelLimits()
	{
		if(el0 == null)
		{
			el0 = ( (EllipseMapObject) map.getLayers().get("Bounds").getObjects().get(0) ).getEllipse();
			el1 = ( (EllipseMapObject) map.getLayers().get("Bounds").getObjects().get(1) ).getEllipse();
			
			el0.y = ( (Integer) map.getProperties().get("height") )*32f - el0.y;
			el1.y = ( (Integer)map.getProperties().get("height") )*32f - el1.y;
		}
		
		if (el0.y < el1.y)
		{
			leftLimit = el0.x/32f + camera.viewportWidth*0.5f;
			rightLimit = el1.x/32f - camera.viewportWidth*0.5f;
			topLimit = el1.y/32f - camera.viewportHeight*0.5f;
			bottomLimit = el0.y/32f + camera.viewportHeight*0.5f;
		}
		else
		{
			leftLimit = el1.x/32f + camera.viewportWidth*0.5f;
			rightLimit = el0.x/32f - camera.viewportWidth*0.5f;
			topLimit = el0.y/32f - camera.viewportHeight*0.5f;
			bottomLimit = el1.y/32f + camera.viewportHeight*0.5f;
		}
	}
	
	public void update()
	{
		// Camera approaches plu's y coordinate with a linear damping
		camera.position.y += (plu.pluBody.getPosition().y - camera.position.y) * .05f;

		
		// If plu is under the follow tolerance to the left
		if (plu.pluBody.getPosition().x < followToleranceLeft)
		{
			// Update camera tolerance values
			followToleranceLeft  = plu.pluBody.getPosition().x;
			followToleranceRight = plu.pluBody.getPosition().x + followToleranceTotal;
			
			// Look a little bit ahead to the left
			decreaseMovementAhead();
		}
		// Else, if it's above the follow tolerance to the right
		else if (plu.pluBody.getPosition().x > followToleranceRight)
		{
			// Update camera tolerance values
			followToleranceLeft  = plu.pluBody.getPosition().x - followToleranceTotal;
			followToleranceRight = plu.pluBody.getPosition().x;
			
			// Look a little bit ahead to the right
			increaseMovementAhead();
		}
		
		// Finally, set camera's x position to current average tolerance position + 'cameraMovementAhead'
		camera.position.x = (followToleranceRight + followToleranceLeft)/2f + cameraMovementAhead;
		
		// Don't let it go out of the level bounds
		if (camera.position.x < leftLimit) camera.position.x = leftLimit;
		else if (camera.position.x > rightLimit) camera.position.x = rightLimit;
		if (camera.position.y < bottomLimit) camera.position.y = bottomLimit;
		else if (camera.position.y > topLimit) camera.position.y = topLimit;
		
		// Debug line
//		Gdx.app.log("cameraDebug", "plu posX " + plu.pluBody.getPosition().x + 
//								   " / tolLeft " + followToleranceLeft + 
//							       " / tolRight " + followToleranceRight + 
//								   " / cam pos " + camera.position.x + 
//								   " / cam moveAhead " + cameraMovementAhead);
		
		// Send camera information to the tilemap renderer and sprite batch
		camera.update();
		renderer.setView(camera);
	}
	
	void increaseMovementAhead()
	{
		// increase in small steps
		cameraMovementAhead += Gdx.graphics.getDeltaTime() * moveAheadSpeed * plu.pluBody.getLinearVelocity().x;
		
		// Capping
		if (cameraMovementAhead > cameraMovementAheadCap) cameraMovementAhead = cameraMovementAheadCap;
	}
	
	void decreaseMovementAhead()
	{
		// Decrease in small steps
		cameraMovementAhead += Gdx.graphics.getDeltaTime() * moveAheadSpeed * plu.pluBody.getLinearVelocity().x;
		
		// Capping
		if (cameraMovementAhead < -cameraMovementAheadCap) cameraMovementAhead = -cameraMovementAheadCap;
	}
	
	public void resize()
	{
		// Adapt the camera to the new viewport resolution
		camera.setToOrtho(false, Gdx.graphics.getWidth()/32f, Gdx.graphics.getHeight()/32f);
		
		camera.position.y = plu.pluBody.getPosition().y;
		
		camera.update();
		
		// Get level bounds or limits and adapt them to the camera size
		updateLevelLimits();
	}
}
