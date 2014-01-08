package grupo.bolado;

import grupo.bolado.gui.MainMenu;
import grupo.bolado.gui.Renderable;

import java.io.File;

import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Game implements ApplicationListener 
{
	// Screens
	public static MainMenu mainMenu;
	public static LevelManager levelManager;
	public static Renderable activeRenderer;
	
	// IO event listener
	static MainInputProcessor inputProcessor;
	
	// Main singleton objects
	public static TextureAtlas atlas;
	public static OrthographicCamera camera;
	public static World b2World;
	public static RayHandler rayHandler;
	
	// Debug objects
	static boolean debugOn;
	static Box2DDebugRenderer b2Debug;
	static FPSLogger fpsLogger;
	
	// Game font
	public static BitmapFont font;
	
	// Our sounds
	public static Music bgMusic;
	
	@Override
	public void create() 
	{
		// Our game's camera
		camera = new OrthographicCamera();
		
		// Atlas with most textures used in the game
		atlas = new TextureAtlas(new FileHandle(new File("assets/spriteAtlas/atlas.atlas")));
		
		// Physics world - where all physics calculation is done
		float gravity = -35f;
		b2World = new World(new Vector2(0f, gravity), true);
		b2World.setContactListener(new WorldContactListener());
		
		// Box2D debugger - will draw Box2D objects on the screen, when activated
		b2Debug = new Box2DDebugRenderer();
		
		// Handles lights and shadows in the game
		rayHandler = new RayHandler(b2World);
		RayHandler.useDiffuseLight(true);
		RayHandler.setGammaCorrection(true);
		
		// Object used to log our game's FPS
		fpsLogger = new FPSLogger();
		
		// Object that handles input callbacks
		inputProcessor = new MainInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		// Manages loading and displaying current maps
		levelManager = new LevelManager();
		
		// The main menu of our game
		mainMenu = new MainMenu();
		
		// The screen we are rendering at the moment - Main Menu or one of the levels.
		activeRenderer = mainMenu;
		
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/inGameMusic.wav"));
		
		bgMusic.setLooping(true);
		bgMusic.play();
	}

	@Override
	public void render() 
	{		
		activeRenderer.render();
	}
	
	@Override
	public void resize(int width, int height) 
	{
		activeRenderer.resize();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	@Override
	public void dispose() 
	{
		levelManager.dispose();
		rayHandler.dispose();
//		bgMusic.dispose();
	}
}
