package Juegos;

import java.awt.Graphics;

import Elementos.AdministradorEnemigos;
import Elementos.Jugador;
import Niveles.LevelManager;


public class Juego {
    private VtaJuego vta;
    private Jugador player;
    private PanelJuego pan;
    private LevelManager levelMan;
    private Camera camera;
    private Background background;
    private GameLoop gameLoop;

    public static int NIVEL_ACTUAL_ANCHO;
    public static int NIVEL_ACTUAL_ALTO;
    public static int[][] NIVEL_ACTUAL_DATA;

    public final static int TILES_DEF_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_HEIGHT = 23;
    public final static int TILES_WIDTH = 40;
    public final static int TILES_SIZE = (int) (TILES_DEF_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_HEIGHT;

    private AdministradorEnemigos adminEnemigos;

    public Juego() {
        inicializar();
        pan = new PanelJuego(this);
        vta = new VtaJuego(pan);
        vta.add(pan);
        pan.requestFocus();
        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

    private void inicializar() {
        levelMan = new LevelManager(this);
        adminEnemigos = new AdministradorEnemigos();

        NIVEL_ACTUAL_ALTO = levelMan.getCurrentLevel().getLvlData().length * TILES_SIZE;
        NIVEL_ACTUAL_ANCHO = levelMan.getCurrentLevel().getLvlData()[0].length * TILES_SIZE;
        NIVEL_ACTUAL_DATA = levelMan.getCurrentLevel().getLvlData();  
        player = new Jugador(200, 200, (int) (64 * SCALE), (int) (40 * SCALE));
        player.loadLvlData(levelMan.getCurrentLevel().getLvlData());

        adminEnemigos.crearEnemigoVerde(400, 200);
        adminEnemigos.crearEnemigoVerde(550, 200);
        adminEnemigos.crearEnemigoVerde(700, 200);

        
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT, NIVEL_ACTUAL_ANCHO, NIVEL_ACTUAL_ALTO);
        background = new Background();
    }

    public void updates() {
        player.update(camera.getxLvlOffset(), camera.getyLvlOffset());
        levelMan.update();
        pan.updateGame();
        adminEnemigos.update();

        if (player.getArmaActual() != null) {
            adminEnemigos.comprobarColisionesBalas(player.getArmaActual().getAdminBalas());
        }

        camera.checkCloseToBorder((int) player.getHitBox().getX(), (int) player.getHitBox().getY());
    }

    public void render(Graphics g) {
        background.draw(g, camera.getxLvlOffset());
        levelMan.draw(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        adminEnemigos.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        player.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
    }

    public Jugador getPlayer() {
        return player;
    }

    public PanelJuego getPanel() {
        return pan;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public void updateMouseInfo(int mouseX, int mouseY) {
        // Almacena o pasa directamente la información del mouse al jugador
        player.updateMouseInfo(mouseX, mouseY);
    }

    public AdministradorEnemigos getAdminEnemigos() {
        return adminEnemigos;
    }
}
