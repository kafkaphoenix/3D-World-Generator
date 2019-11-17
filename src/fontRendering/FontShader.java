package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/fontRendering/fontFragment.txt";
	
	private int location_colour;
	private int location_translation;
	private int location_width;
	private int location_edge;
	private int location_borderWidth;
	private int location_borderEdge;
	private int location_outlineColour;
	private int location_offset;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_width = super.getUniformLocation("width");
		location_edge = super.getUniformLocation("edge");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdge = super.getUniformLocation("borderEdge");
		location_outlineColour = super.getUniformLocation("outlineColour");
		location_offset = super.getUniformLocation("offset");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	protected void loadOffset(float offset)
	{
		super.loadFloat(location_offset, offset);
	}
	
	protected void loadBorderWidth(float border)
	{
		super.loadFloat(location_borderWidth, border);
	}
	
	protected void loadBorderEdge(float border)
	{
		super.loadFloat(location_borderEdge, border);
	}
	
	protected void loadOutlineColour(Vector3f colour)
	{
		super.loadVector(location_outlineColour, colour);
	}
	
	protected void loadWidth(float width)
	{
		super.loadFloat(location_width, width);
	}
	
	protected void loadEdge(float edge)
	{
		super.loadFloat(location_edge, edge);
	}
	
	protected void loadColour(Vector3f colour)
	{
		super.loadVector(location_colour, colour);
	}
	
	protected void loadTranslation(Vector2f translation)
	{
		super.load2DVector(location_translation, translation);
	}


}
