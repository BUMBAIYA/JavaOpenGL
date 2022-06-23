package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("Grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		ModelData lampData = OBJFileLoader.loadOBJ("lamp");
		RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());
		
		TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setHasTransparency(true);
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.5f, 0.5f, 0.5f)));
		lights.add(new Light(new Vector3f(185, 10, -185), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		Light light = new Light(new Vector3f(100, 17, -100), new Vector3f(0, 2, 0), new Vector3f(1, 0.01f, 0.002f));
		lights.add(light);
		
		entities.add(new Entity(lamp, new Vector3f(185, -4.7f, -185), 0, 0, 0, 1));
		Entity lampEntity = new Entity(lamp, new Vector3f(100, -4.2f, -100), 0, 0, 0, 1);
		entities.add(lampEntity);
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		terrains.add(terrain);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		ModelData playerData = OBJFileLoader.loadOBJ("rainbowCube");
		RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
		TexturedModel playerTexturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("rainbow")));
		
		Player player = new Player(playerTexturedModel, new Vector3f(100, 5, -50), 0, 0, 0, 1);
		entities.add(player);
		Camera camera = new Camera(player);
		
		WaterShader waterShader = new WaterShader();
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(75, -75, 0));
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waters);
		
		
		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			
			renderer.renderScene(entities, terrains, lights, camera);
			waterRenderer.render(waters, camera);
			
			DisplayManager.updateDisplay();
		}
		
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();

	}

}
