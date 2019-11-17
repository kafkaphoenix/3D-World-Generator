package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));
		Player player = new Player(stanfordBunny, new Vector3f(90, 5, -100), 0, 100, 0, 0.6f);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		FontType font = new FontType(loader.loadTextureFonts("candara"), "candara");
//		GUIText text = new GUIText("This is a test", 3f, font, new Vector2f(0.5f, 0.5f), 0.5f, true);
//		text.setColour(0, 0, 0);
//		text.setWidth(0.5f);//normal text
//		text.setEdge(0.1f);
////		text.setWidth(0.51f);//big text
////		text.setEdge(0.02f);
////		text.setWidth(0.46f);//small text
////		text.setEdge(0.19f);
//		text.setBorderWidth(0.4f);
//		text.setBorderEdge(0.5f);
//		text.setOutlineColour(1.0f, 0, 0);
//		text.setOffset(0);

		// *********TERRAIN TEXTURE STUFF**********
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// *****************************************

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);

		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader),
				fernTextureAtlas);
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
				new ModelTexture(loader.loadTexture("flower")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
				new ModelTexture(loader.loadTexture("grassTexture")));

		TexturedModel pine = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		pine.getTexture().setHasTransparency(true);
		
		TexturedModel booble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader), 
				new ModelTexture(loader.loadTexture("lowPolyTree")));
		booble.getTexture().setHasTransparency(true);
		
	
		fern.getTexture().setHasTransparency(true);
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmapPerlin");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
	
		
		//******************NORMAL MAP MODELS************************
		
//		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
//				new ModelTexture(loader.loadGameTexture("barrel")));
//		barrelModel.getTexture().setNormalMap(loader.loadGameTexture("barrelNormal"));
//		barrelModel.getTexture().setShineDamper(10);
//		barrelModel.getTexture().setReflectivity(0.5f);
//		
//		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
//				new ModelTexture(loader.loadGameTexture("crate")));
//		crateModel.getTexture().setNormalMap(loader.loadGameTexture("crateNormal"));
//		crateModel.getTexture().setShineDamper(10);
//		crateModel.getTexture().setReflectivity(0.5f);
		
		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);
		
		
		//************ENTITIES*******************
		
		Random random = new Random(5666778);
		for (int i = 0; i < 600; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				
				if(y < 0)
				{
					
				}
				else
				{
					entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0,
							random.nextFloat() * 360, 0, 0.9f));
				}		
			}
			
			if (i % 2 == 0) {

				float x = random.nextFloat() * 800;
				float x2 = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float z2 = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
				float y2 = terrain.getHeightOfTerrain(x2, z2);
				
				if(y < 0)
				{
					
				}
				else
				{
					entities.add(new Entity(pine, new Vector3f(x, y, z), 0,
							random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
				}
				if(y2 < 0)
				{
					
				}
				else
				{
					entities.add(new Entity(booble, new Vector3f(x2, y2, z2), 0,
							random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.6f));
				}

			}
			
			if(i % 5 == 0)
			{
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				float y = terrain.getHeightOfTerrain(x, z);
					
				entities.add(new Entity(boulderModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}
			
//			if(i % 7 == 0)
//			{
//				float x = random.nextFloat() * 800;
//				float z = random.nextFloat() * -800;
//				float y = terrain.getHeightOfTerrain(x, z);
//				
//				if(y < 0)
//				{
//					
//				}
//				else
//				{
//					entities.add(new Entity(flower, new Vector3f(x, y, z), 0,
//							random.nextFloat() * 360, 0, 5f));
//				}	
//			}
//			
//			if(i % 11 == 0)
//			{
//				float x = random.nextFloat() * 800;
//				float z = random.nextFloat() * -800;
//				float y = terrain.getHeightOfTerrain(x, z);
//				
//				if(y < 0)
//				{
//					
//				}
//				else
//				{
//					entities.add(new Entity(grass, new Vector3f(x, y, z), 0,
//							random.nextFloat() * 360, 0, 5f));
//				}	
//			}
		}
	
		
		//*******************OTHER SETUP***************

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.0f, 1.0f, 1.0f));
		lights.add(sun);
//		lights.add(new Light(new Vector3f(-200f, 10f, -200f),new Vector3f(10f, 0f, 0f)));
//		lights.add(new Light(new Vector3f(200f, 10f, 200f),new Vector3f(0f, 0f, 10f)));
		
//		lights.add(new Light(new Vector3f(185f, -4.7f, -293f), new Vector3f(2, 0, 0),new Vector3f(1f, 0.01f, 0.002f)));
//		lights.add(new Light(new Vector3f(370f, 4.2f, -300f),new Vector3f(0, 2, 2),new Vector3f(1f, 0.01f, 0.002f)));
//		lights.add(new Light(new Vector3f(293f, -6.8f, -305f),new Vector3f(2, 2, 0),new Vector3f(1f, 0.01f, 0.002f)));
//
//		entities.add(new Entity(lamp, new Vector3f(185f, -4.7f, -293f), 0, 0, 0, 1));
//		entities.add(new Entity(lamp, new Vector3f(370f, 4.2f, -300f), 0, 0, 0, 1));
//		entities.add(new Entity(lamp, new Vector3f(293f, -6.8f, -305f), 0, 0, 0, 1));


		entities.add(player);
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
	
		//**********Water Renderer Set-up************************
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers, 0.1f,1000.0f);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(400, -400, 0);
		waters.add(water);
		
		//**********Particles**************************************
		
		ParticleTexture particleTexture  = new ParticleTexture(loader.loadParticleTextureAtlas("fire"), 8, true);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 50, 50, 0.3f, 4, 10);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setScaleError(0.4f);
		system.setScaleError(0.8f);
		
		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init(loader);
		
		//****************Game Loop Below*********************

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			
			//system.generateParticles(player.getPosition());
			
			ParticleMaster.update(camera);
			
			renderer.renderShadowMap(entities, sun);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			//render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			
			//render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();	
			fbo.bindFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));	
			waterRenderer.render(waters, camera, sun);
			
			ParticleMaster.renderParticles(camera);
			fbo.unbindFrameBuffer();
			PostProcessing.doPostProcessing(fbo.getColourTexture());
			
			guiRenderer.render(guiTextures);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}

		//*********Clean Up Below**************
		
		PostProcessing.cleanUp();
		fbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}


}
