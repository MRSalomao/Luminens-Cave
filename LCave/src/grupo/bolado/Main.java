package grupo.bolado;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Main 
{
	public static Element root;
	
	public static void main(String[] args) 
	{
		XmlReader xmlReader = new XmlReader();
		try 
		{
			// Open configuration xml and load game settings
			root = xmlReader.parse(new FileHandle(new File("assets/cfgs.xml")));
		} 
		catch (IOException e) { e.printStackTrace(); }
		
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.addIcon("assets/icons/icon16.png", FileType.Internal);
		cfg.addIcon("assets/icons/icon32.png", FileType.Internal);
		cfg.addIcon("assets/icons/icon128.png", FileType.Internal);
		cfg.title = "LumensCave";
		cfg.vSyncEnabled = true;
		cfg.useGL20 = Main.root.getChildByName("game").getBoolean("softShadows");
		cfg.width = Main.root.getChildByName("game").getInt("resolutionX");
		cfg.height = Main.root.getChildByName("game").getInt("resolutionY");
		cfg.fullscreen = Main.root.getChildByName("game").getBoolean("fullscreen");
		
		new LwjglApplication(new Game(), cfg);
	}
}
