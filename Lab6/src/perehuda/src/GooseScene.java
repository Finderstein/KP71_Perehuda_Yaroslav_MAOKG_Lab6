package perehuda.src;

import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class GooseScene extends JFrame implements KeyListener, ActionListener {
    public static void main(String[] args) throws IOException {
        new GooseScene();
    }

    private SimpleUniverse universe;
    private BranchGroup root;
    private Canvas3D canvas;
    private Goose goose;

    private boolean keyLeft, keyRight, keyForward, keyViewLeft, keyViewRight, keyViewUp, keyViewDown;

    public GooseScene() throws IOException {
        initCanvas();
        initUniverse();

        goose = new Goose(canvas);
        root = new BranchGroup();
        root.addChild(goose.asNode());

        Bounds influenceRegion = new BoundingSphere();
        addLightsToUniverse(influenceRegion);
        addBackground(influenceRegion);

        root.compile();
        universe.addBranchGraph(root);

        new Timer(10, this).start();

        setTitle("Goose");
        setSize(640, 480);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(canvas, BorderLayout.CENTER);

        setVisible(true);
    }

    private void initCanvas() {
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);
        canvas.setFocusable(true);

        canvas.addKeyListener(this);
    }

    private void initUniverse() {
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    private void addLightsToUniverse(Bounds influenceRegion) {
        Color3f lightColor = new Color3f(Color.WHITE);
        Vector3f lightDirection = new Vector3f(-1F, -1F, -1F);
        DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
        light.setInfluencingBounds(influenceRegion);
        root.addChild(light);
    }

    private void addBackground(Bounds influenceRegion) {
        Background background = new Background(new Color3f(Color.BLUE));
        background.setApplicationBounds(influenceRegion);
        root.addChild(background);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(keyLeft) {
            goose.turnLeft();
        }
        if(keyRight) {
            goose.turnRight();
        }
        if(keyForward) {
            goose.update();
        }

        double rotateX = (keyViewUp ? 1 : 0) - (keyViewDown ? 1 : 0);
        double rotateY = (keyViewLeft ? 1 : 0) - (keyViewRight ? 1 : 0);

        goose.rotateModel(rotateX*0.05, rotateY*0.05, 0);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: keyForward = true; break;
            case KeyEvent.VK_A: keyLeft = true; break;
            case KeyEvent.VK_D: keyRight = true; break;

            case KeyEvent.VK_LEFT: keyViewLeft = true; break;
            case KeyEvent.VK_RIGHT: keyViewRight = true; break;
            case KeyEvent.VK_UP: keyViewUp = true; break;
            case KeyEvent.VK_DOWN: keyViewDown = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: keyForward = false; break;
            case KeyEvent.VK_A: keyLeft = false; break;
            case KeyEvent.VK_D: keyRight = false; break;

            case KeyEvent.VK_LEFT: keyViewLeft = false; break;
            case KeyEvent.VK_RIGHT: keyViewRight = false; break;
            case KeyEvent.VK_UP: keyViewUp = false; break;
            case KeyEvent.VK_DOWN: keyViewDown = false; break;
        }
    }
}
