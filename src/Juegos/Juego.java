package Juegos;

import java.awt.Graphics;
import java.awt.event.KeyEvent; 
import java.util.ArrayList;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Jugador;
import Elementos.Administradores.AdministradorEnemigos;
import Elementos.Decoraciones.Decoracion;
import Elementos.Decoraciones.EstacionQuimica;
import Elementos.Administradores.AdministradorBalas;
import Elementos.Administradores.AdministradorDecoraciones;
import Niveles.LevelManager;
import Utilz.MetodoAyuda;



public class Juego {
    private VtaJuego vta;
    private Jugador player;
    private PanelJuego pan;
    private LevelManager levelMan;
    private Camera camera;
    private Background background;
    private GameLoop gameLoop;
    private AdministradorDecoraciones adminDecoraciones;

    public static int NIVEL_ACTUAL_ANCHO;
    public static int NIVEL_ACTUAL_ALTO;
    public static int[][] NIVEL_ACTUAL_DATA;
    public static Jugador jugadorActual;

    public final static int TILES_DEF_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_HEIGHT = 23;
    public final static int TILES_WIDTH = 40;
    public final static int TILES_SIZE = (int) (TILES_DEF_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_HEIGHT;

    private AdministradorEnemigos adminEnemigos;
    
    // Para control de niveles
    private boolean cambiandoNivel = false;
    private int nivelDestino = -1;

    private EstacionQuimica estacionQuimicaActiva = null;

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
        adminDecoraciones = new AdministradorDecoraciones();

        NIVEL_ACTUAL_ALTO = levelMan.getCurrentLevel().getLvlData().length * TILES_SIZE;
        NIVEL_ACTUAL_ANCHO = levelMan.getCurrentLevel().getLvlData()[0].length * TILES_SIZE;
        NIVEL_ACTUAL_DATA = levelMan.getCurrentLevel().getLvlData();  
        MetodoAyuda.actualizarBloquesSinHitbox(levelMan.getCurrentLevelIndex());
        player = new Jugador(200, 200, (int) (48 * SCALE), (int) (48 * SCALE));
        jugadorActual = player;
        player.loadLvlData(levelMan.getCurrentLevel().getLvlData());
        
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT, NIVEL_ACTUAL_ANCHO, NIVEL_ACTUAL_ALTO);
        background = new Background(levelMan.getCurrentLevelIndex());
        
        levelMan.cargarDecoraciones();
        levelMan.cargarEntidades(this);
    }

    public void updates() {
        // Verificar si estamos en proceso de cambio de nivel
        if (cambiandoNivel) {
            completarCambioNivel();
            return;
        }
        
        player.update(camera.getxLvlOffset(), camera.getyLvlOffset());
        levelMan.update();
        pan.updateGame();
        adminEnemigos.update();
        adminDecoraciones.update();

        if (player.getArmaActual() != null) {
            adminEnemigos.comprobarColisionesBalas(player.getArmaActual().getAdminBalas());
        }

        camera.checkCloseToBorder((int) player.getHitBox().getX(), (int) player.getHitBox().getY());
        
        for (Enemigo enemigo : adminEnemigos.getEnemigos()) {
        if (enemigo.getAdminBalas() != null) {
            comprobarColisionesBalasEnemigasConJugador(enemigo.getAdminBalas());
        }
    }
    }
    
    // Método para iniciar un cambio de nivel
    public void cambiarNivel(int nivelIndex) {
        if (nivelIndex >= 0 && nivelIndex < levelMan.getTotalLevels()) {
            cambiandoNivel = true;
            nivelDestino = nivelIndex;
            
            // Desactivar controles del jugador durante la transición
            player.resetDirBooleans();
        }
    }
    
    // Cambiar al siguiente nivel
    public void siguienteNivel() {
        cambiarNivel((levelMan.getCurrentLevelIndex() + 1) % levelMan.getTotalLevels());
    }
    
    // Completar el cambio de nivel
    private void completarCambioNivel() {
        // Limpiar balas y otros objetos
        if (player.getArmaActual() != null) {
            player.getArmaActual().getAdminBalas().limpiarBalas();
        }
        adminEnemigos.limpiarEnemigos();
        adminDecoraciones.limpiarDecoraciones();
        
        // Cambiar nivel a través del LevelManager
        levelMan.changeLevel(nivelDestino);
        background = new Background(levelMan.getCurrentLevelIndex());
        
        // Actualizar cámara para el nuevo nivel
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT, NIVEL_ACTUAL_ANCHO, NIVEL_ACTUAL_ALTO);
        
        // Cargar datos del nivel para el jugador
        player.loadLvlData(NIVEL_ACTUAL_DATA);
        
        levelMan.cargarEntidades(this);

        // Completar la transición
        cambiandoNivel = false;
        nivelDestino = -1;
    }
    
    private void comprobarColisionesBalasEnemigasConJugador(AdministradorBalas adminBalas) {
    if (adminBalas == null || player == null) return;
    
    ArrayList<Bala> balas = adminBalas.getBalas();
    
    for (Bala bala : balas) {
        if (bala.estaActiva() && bala.getHitBox().intersects(player.getHitBox())) {
            // La bala impactó en el jugador
            // Aquí puedes implementar lógica de daño al jugador
            System.out.println("¡Jugador recibió impacto de bala enemiga!");
            bala.desactivar();
        }
    }
}
    public void render(Graphics g) {
        background.draw(g, camera.getxLvlOffset());
        
        if(levelMan.getCurrentLevelIndex() != 2){
            adminDecoraciones.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
            levelMan.draw(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        } else {
            levelMan.draw(g, camera.getxLvlOffset(), camera.getyLvlOffset());
            adminDecoraciones.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        }
        adminEnemigos.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        player.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());

        if (estacionQuimicaActiva != null && estacionQuimicaActiva.isEstacionAbierta()) {
            estacionQuimicaActiva.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
        }
        
        // Aquí se podría agregar un efecto de transición entre niveles
        if (cambiandoNivel) {
            // Por ejemplo, dibujar una pantalla de carga o un efecto de fade
        }
    }

    public void interactuarConEstacionQuimica() {
    // Buscar estaciones químicas en las decoraciones
        for (Decoracion decoracion : adminDecoraciones.getDecoraciones()) {
            if (decoracion instanceof EstacionQuimica) {
                EstacionQuimica estacion = (EstacionQuimica) decoracion;
                if (estacion.isJugadorCerca()) {
                    estacion.interactuar();
                    estacionQuimicaActiva = estacion;
                    return;
                }
            }
        }
    }

    public void procesarTeclaEstacionQuimica(int keyCode) {
        if (estacionQuimicaActiva != null && estacionQuimicaActiva.isEstacionAbierta()) {
            boolean procesado = estacionQuimicaActiva.procesarTecla(keyCode);
            if (procesado && keyCode == KeyEvent.VK_ESCAPE) {
                estacionQuimicaActiva = null;
            }
        }
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
    
    public LevelManager getLevelManager() {
        return levelMan;
    }
    
    // Método para detectar eventos específicos (puede usarse para cambiar niveles)
    public void procesarEvento(String tipoEvento, Object... args) {
        if ("cambiar_nivel".equals(tipoEvento) && args.length > 0) {
            int nivelDestino = (int)args[0];
            cambiarNivel(nivelDestino);
        }
    }

    public AdministradorDecoraciones getAdminDecoraciones() {
        return adminDecoraciones;
    }
}