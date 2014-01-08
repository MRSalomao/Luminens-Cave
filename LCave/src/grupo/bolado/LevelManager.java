package grupo.bolado;

import java.awt.Color;
import java.util.ArrayList;

import grupo.bolado.entities.LevelCollider;
import grupo.bolado.entities.Lumen;
import grupo.bolado.entities.PlantEater;
import grupo.bolado.entities.PlantShooter;
import grupo.bolado.entities.Plu;
import grupo.bolado.entities.Portal;
import grupo.bolado.gui.Renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import static grupo.bolado.Game.*;

public class LevelManager implements Renderable
{
	public static LevelManager singleton;
	
	// Tilemap object and it's renderer
	public static OrthogonalTiledMapRenderer renderer;
	public static TiledMap map;
	public static Color bgColor;
	
	public static String activeMapID;
	public static int totalLumens;
	
	// Game entities
	static Plu plu;
	static public ArrayList<PlantEater> plantsEater;
	static public ArrayList<PlantShooter> plantsShooter;
	static public ArrayList<Lumen> lumens;
	
	public boolean shouldRestart = false;
	public boolean shouldGoBackToMenu = false;
	public boolean shouldGoToNextLevel = false;
	
	static public Portal portal;
	static public LevelCollider levelCollider;
	
	// Camera
	static public CameraHandler cameraHandler;
	
	public void loadMap(String mapID)
	{
		// Init singleton
		singleton = this;
		
		activeMapID  = mapID;
		
		// Load TileMap
		map = new TmxMapLoader().load("assets/levels/" + "Level " + mapID + ".tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / CameraHandler.pixelsPerUnit);
		bgColor = Color.decode( (String) map.getProperties().get("backgroundcolor") );
		
		// Load collision for this level
		levelCollider = new LevelCollider();
		
		// Setup ambient light
		rayHandler.setAmbientLight(0.1f,0.1f,0.1f,0.3f);
		
		// Initialize entities that can be reseted after Plu's death
		restartMap();
	}
	
	
	public void restartMap()
	{
		// Our main character
		plu = new Plu();
		
		// Set Plu's initial position for this level
		MapObject startPoint = map.getLayers().get("PluStart").getObjects().get(0);
		plu.setPosition(new Vector2( ((EllipseMapObject) startPoint).getEllipse().x/32f,  
									 ((EllipseMapObject) startPoint).getEllipse().y/32f ) );

		// CameraHandler setup
		cameraHandler = new CameraHandler(plu.pluBody.getPosition().x);
		
		// Portal
		portal = new Portal(map.getLayers().get("Portal").getObjects().get(0));
		
		// Plant eaters, if any on this level
		plantsEater = new ArrayList<PlantEater>();
		MapLayer tmpMapLayer = map.getLayers().get("PlantaIsca");
		if (tmpMapLayer != null)
		{	
			for (MapObject plantPosition : tmpMapLayer.getObjects())
			{
				plantsEater.add( new PlantEater(plantPosition, plantsEater.size()) );
			}
		}
		
		// Plant shooters, if any on this level
		plantsShooter = new ArrayList<PlantShooter>();
		tmpMapLayer = map.getLayers().get("PlantaCospeDireita");
		if (tmpMapLayer != null)
		{	
			for (MapObject plantPosition : tmpMapLayer.getObjects())
			{
				plantsShooter.add( new PlantShooter(plantPosition, plantsShooter.size(), 1, false) );
			}
		}
		tmpMapLayer = map.getLayers().get("PlantaCospeEsquerda");
		if (tmpMapLayer != null)
		{	
			for (MapObject plantPosition : tmpMapLayer.getObjects())
			{
				plantsShooter.add( new PlantShooter(plantPosition, plantsShooter.size(), -1, false) );
			}
		}
		tmpMapLayer = map.getLayers().get("PlantaCospeBaixo");
		if (tmpMapLayer != null)
		{	
			for (MapObject plantPosition : tmpMapLayer.getObjects())
			{
				plantsShooter.add( new PlantShooter(plantPosition, plantsShooter.size(), -1, true) );
			}
		}
		tmpMapLayer = map.getLayers().get("PlantaCospeCima");
		if (tmpMapLayer != null)
		{	
			for (MapObject plantPosition : tmpMapLayer.getObjects())
			{
				plantsShooter.add( new PlantShooter(plantPosition, plantsShooter.size(), 1, true) );
			}
		}
		
		
		// Lumens
		lumens = new ArrayList<Lumen>();
		if (map.getLayers().get("Lumens") != null)
		{	
			for (MapObject lumenPosition : map.getLayers().get("Lumens").getObjects())
			{
				lumens.add( new Lumen(lumenPosition, lumens.size()) );
			}
			
			totalLumens = map.getLayers().get("Lumens").getObjects().getCount();
		}
		else
		{
			totalLumens = 0;
		}
	}
	
	
	public void renderForeground1()
	{
		renderer.render(new int[] {5,6,8});
	}
	
	public void renderForeground2()
	{
		renderer.render(new int[] {7});
	}
	
	public void renderBackground()
	{
		renderer.render(new int[] {0,1,2,3,4});
	}
	
	
	public void dispose()
	{
		plu.dispose();
		levelCollider.dispose();
		portal.dispose();
		for (PlantEater pEater : plantsEater)
		{
			pEater.dispose();
		}
		for (PlantShooter pShot : plantsShooter)
		{
			pShot.dispose();
		}
		for(Lumen lumen : lumens)
		{
			lumen.dispose();
		}
		
	}
//		renderer.dispose();
//		b2World.dispose();
//		plu.dispose();

	@Override
	public void render() 
	{
		
		// Clear the screen
		// Select color used to clear the screen from the tmx file
		Gdx.gl.glClearColor(bgColor.getRed()/255f, bgColor.getGreen()/255f, bgColor.getBlue()/255f, bgColor.getAlpha()/255f); 
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Update enemies
		updateEnemies();
		
		// Update the character
		plu.update();
		
		// Update lumens
		for(Lumen lumen : lumens)
		{
			lumen.update();
		}
		
		cameraHandler.update(); 
		
		// Render the background
		renderBackground();

		// Render lights and shadows
		Game.rayHandler.setCombinedMatrix(camera.combined);
		Game.rayHandler.updateAndRender();
		
		// Render the first part of the foreground - behind the character
		renderForeground1();
		
		for (PlantEater pEater : plantsEater)
		{
			pEater.renderTongue();
		}
		
		portal.renderPortal();
		
		// Render our character
		plu.render();
		
		renderEnemies();
		
		// Render the rest of the foreground
		renderForeground2();
		
		// Render lumens
		for(Lumen lumen : lumens)
		{
			lumen.renderLumen();
		}
		
		// Render Box2D debug and log fps, if requested
		if (debugOn)
		{
			b2Debug.render(b2World, camera.combined);
			fpsLogger.log();
		}
		
		// Update the game's physics
		b2World.step(1/60f, 6, 2);
		
		if (shouldRestart)
		{
			dispose();
			plantsEater = null;
			plantsShooter = null;
			rayHandler.removeAll();
			shouldRestart = false;
			PlantEater.attackingPlant = -1;
			loadMap(activeMapID);
			return;
		}
		
		if (shouldGoToNextLevel)
		{
			dispose();
			plantsEater = null;
			plantsShooter = null;
			shouldGoToNextLevel = false;
			rayHandler.removeAll();
			PlantEater.attackingPlant = -1;
			loadMap(activeMapID.substring(0,2) + (1 + Integer.valueOf(activeMapID.substring(2))) );
			return;
		}
	}

	void updateEnemies()
	{
		for (PlantEater pEater : plantsEater)
		{
			pEater.update();
		}
		for (PlantShooter pShot : plantsShooter)
		{
			pShot.update();
		}
	}
	
	void renderEnemies()
	{
		for (PlantEater pEater : plantsEater)
		{
			pEater.renderBody();
		}
		for (PlantShooter pShot : plantsShooter)
		{
			pShot.renderBody();
		}
	}

	
	@Override
	public void resize() 
	{
		cameraHandler.resize();
	}

}
