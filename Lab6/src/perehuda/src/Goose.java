package perehuda.src;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Map;

public class Goose {
	private TransformGoose body;
	private TransformGoose leftLeg;
	private TransformGoose rightLeg;
	private TransformGoose mainMove1;
	private TransformGoose mainMove2;
	private TransformGoose mainModel1;
	private TransformGoose mainModel2;

	public Goose(Canvas canvas) throws FileNotFoundException {
		TransformGoose[] transfoms = loadObject("src/perehuda/source_folder/goose.obj", "body", "leftleg", "rightleg");

		body = transfoms[0];
		leftLeg = transfoms[1];
		rightLeg = transfoms[2];

		Material material = new Material();
		material.setAmbientColor (new Color3f(1, 1, 1));
		material.setDiffuseColor (new Color3f(1f, 1f, 1f));
		material.setSpecularColor(new Color3f(0.1f, 0.1f, 0.1f));
		material.setShininess(1f);
		material.setLightingEnable(true);

		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.COMBINE);

		TextureLoader textureLoader = new TextureLoader("src/perehuda/source_folder/ground.jpg", "RGB", canvas);
		Appearance ap = new Appearance();
		ap.setTexCoordGeneration(new TexCoordGeneration(
				TexCoordGeneration.OBJECT_LINEAR, TexCoordGeneration.TEXTURE_COORDINATE_2));
		ap.setMaterial(material);
		ap.setTextureAttributes(texAttr);
		ap.setTexture(textureLoader.getTexture());

		TransformGoose ground = new TransformGoose(new Box(1000, 1000, 0.1f, ap));
		ground.translate(0, 0, -1);

		mainMove1 = new TransformGoose(body.asNode(), leftLeg.asNode(), rightLeg.asNode());
		mainMove2 = new TransformGoose(mainMove1.asNode());
		mainModel1 = new TransformGoose(mainMove2.asNode(), ground.asNode());
		mainModel2 = new TransformGoose(mainModel1.asNode());

		rotateModel(-Math.PI/1.8, Math.PI, 0);
	}

	private static TransformGoose[] loadObject(String filename, String... groupNames) throws FileNotFoundException {
		Scene scene = new ObjectFile(ObjectFile.RESIZE).load(filename);
		BranchGroup root = scene.getSceneGroup();

		Map<String, Shape3D> nameMap = scene.getNamedObjects();

		root.removeAllChildren();

		TransformGoose[] ret = new TransformGoose[groupNames.length];

		for (int i = 0; i < groupNames.length; ++i) {
			ret[i] = new TransformGoose(nameMap.get(groupNames[i]));
		}

		return ret;
	}

	double legRotateDX = 0.01, bodyRotateDy = 0.005;

	public void update() {
		leftLeg.rotate(legRotateDX, 0, 0);
		rightLeg.rotate(-legRotateDX, 0, 0);

		if(Math.abs(leftLeg.rotX) > 0.1) {
			legRotateDX *= -1;
		}

		body.rotate(0, bodyRotateDy, 0);

		if(Math.abs(body.rotY) > 0.05) {
			bodyRotateDy *= -1;
		}

		double speed = 0.01;

		mainMove2.translate(speed * Math.sin(mainMove1.rotZ), -speed * Math.cos(mainMove1.rotZ), 0);
	}

	public void turnLeft() {
		mainMove1.rotate(0, 0, 0.01);
	}

	public void turnRight() {
		mainMove1.rotate(0, 0, -0.01);
	}

	public void rotateModel(double rotX, double rotY, double rotZ) {
		mainModel1.rotate(rotX, 0, 0);
		mainModel2.rotate(0, rotY, 0);
	}

	public Node asNode() {
		return mainModel2.asNode();
	}
}
