package Juegos;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Jugador;
import Elementos.Personaje;
import Elementos.Administradores.AdministradorEnemigos;
import Elementos.Decoraciones.Decoracion;
import Elementos.Decoraciones.EstacionQuimica;
import Elementos.Administradores.AdministradorBalas;
import Elementos.Administradores.AdministradorDecoraciones;
import Niveles.LevelManager;
import Utilz.MetodoAyuda;
import Menus.Menu;
import Menus.MenuMuerte;
import Menus.MenuPausa;
import Menus.SelectorPersonajes;

public class Juego {
    private VtaJuego vta;
    private Jugador player;
    private PanelJuego pan;
    private LevelManager levelMan;
    private Camera camera;
    private Background background;
    private GameLoop gameLoop;
    private HUDQuimico hudQuimico;
    private AdministradorDecoraciones adminDecoraciones;

    public static int NIVEL_ACTUAL_ANCHO;
    public static int NIVEL_ACTUAL_ALTO;
    public static int[][] NIVEL_ACTUAL_DATA;
    public static Jugador jugadorActual;
    public static Elementos.Administradores.AdministradorEnemigos ADMIN_ENEMIGOS;

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
    private boolean necesitaReinicio = false;

    private EstacionQuimica estacionQuimicaActiva = null;

    private EstadoJuego estadoJuego = EstadoJuego.MENU;
    private Menu menu;
    private MenuPausa menuPausa;
    private MenuMuerte menuMuerte;
    private SelectorPersonajes selectorPersonajes;

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
        ADMIN_ENEMIGOS = adminEnemigos;
        adminDecoraciones = new AdministradorDecoraciones();

        NIVEL_ACTUAL_ALTO = levelMan.getCurrentLevel().getLvlData().length * TILES_SIZE;
        NIVEL_ACTUAL_ANCHO = levelMan.getCurrentLevel().getLvlData()[0].length * TILES_SIZE;
        NIVEL_ACTUAL_DATA = levelMan.getCurrentLevel().getLvlData();  
        MetodoAyuda.actualizarBloquesSinHitbox(levelMan.getCurrentLevelIndex());
        player = new Jugador(200, 200, (int) (48 * SCALE), (int) (48 * SCALE), Personaje.TipoPersonaje.ECLIPSA);
        jugadorActual = player;
        hudQuimico = new HUDQuimico(player.getSistemaQuimico());
        player.loadLvlData(levelMan.getCurrentLevel().getLvlData());
        
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT, NIVEL_ACTUAL_ANCHO, NIVEL_ACTUAL_ALTO);
        background = new Background(levelMan.getCurrentLevelIndex());
        
        levelMan.cargarDecoraciones();
        levelMan.cargarEntidades(this);
        menu = new Menu(this);
        menuPausa = new MenuPausa(this);
        menuMuerte = new MenuMuerte(this);
        selectorPersonajes = new SelectorPersonajes(this);
    }

    public void updates() {
        switch (estadoJuego) {
            case MENU:
                menu.update();
                break;
            case SELECCION_PERSONAJE:
                selectorPersonajes.update();
                break;
            case PLAYING:
                if(necesitaReinicio) {
                    reiniciarJuego();
                    return;
                }
                
                if (cambiandoNivel) {
                    completarCambioNivel();
                    return;
                }
                
                player.update(camera.getxLvlOffset(), camera.getyLvlOffset());
                if (player.estaMuerto()) {
                    volverAlMenu();
                    return;
                }
                levelMan.update();
                pan.updateGame();
                adminEnemigos.update();
                comprobarColisionEnemigosConJugador();
                administrarDañoBalasEnemigas();
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
                break;
            case MUERTE:
                menuMuerte.update();
                break;
            default:
                break;
        }
    }

    private void comprobarColisionEnemigosConJugador() {
        if (player.estaMuerto()) return;
        
        for (Enemigo enemigo : adminEnemigos.getEnemigos()) {
            if (enemigo.estaActivo() && 
                enemigo.getHitBox().intersects(player.getHitBox())) {
                player.recibirDaño(5); // Daño por contacto
            }
        }
    }

    private void administrarDañoBalasEnemigas() {
        if (player.estaMuerto()) return;
    
        for (Enemigo enemigo : adminEnemigos.getEnemigos()) {
            if (enemigo.getAdminBalas() != null) {
                ArrayList<Bala> balas = enemigo.getAdminBalas().getBalas();
                for (Bala bala : balas) {
                    if (bala.estaActiva() && bala.getHitBox().intersects(player.getHitBox())) {
                        boolean enDodgeroll = player.isDodgeInvulnerable();
                        
                        // Usar el nuevo método para manejar la colisión
                        bala.colisionConJugador(enDodgeroll);
                        
                        // Solo aplicar daño si no está invulnerable
                        if (!player.isInvulnerable() && !enDodgeroll) {
                            player.recibirDaño(bala.getDaño());
                        }
                    }
                }
            }
        }
    }

    private void comprobarColisionesBalasEnemigasConJugador(AdministradorBalas adminBalas) {
        if (adminBalas == null || player == null) return;
        
        ArrayList<Bala> balas = adminBalas.getBalas();
        
        for (Bala bala : balas) {
            if (bala.estaActiva() && bala.getHitBox().intersects(player.getHitBox())) {
                boolean enDodgeroll = player.isDodgeInvulnerable();
                
                // Solo procesamos el impacto si no está en dodgeroll
                if (!enDodgeroll) {
                    bala.colisionConJugador(false);
                    System.out.println("¡Jugador recibió impacto de bala enemiga!");
                }
            }
        }
    }

    public void configurarJugadorConPersonaje(Personaje.TipoPersonaje tipoPersonaje) {
        float x = player != null ? (float) player.getHitBox().getCenterX() : 200;
        float y = player != null ? (float) player.getHitBox().getCenterY() : 200;
        
        player = new Jugador(x, y, (int) (48 * SCALE), (int) (48 * SCALE), tipoPersonaje);
        player.loadLvlData(levelMan.getCurrentLevel().getLvlData());
        jugadorActual = player;
        hudQuimico = new HUDQuimico(player.getSistemaQuimico());
    }

    public void render(Graphics g) {
        switch (estadoJuego) {
            case MENU:
                menu.draw(g);
                break;
            case SELECCION_PERSONAJE:
                selectorPersonajes.draw(g);
                break;
            case PLAYING:
                // Todo el código existente de render va aquí
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
    
                if (!cambiandoNivel) {
                    hudQuimico.render(g);
                }
                
                if (cambiandoNivel) {
                    // Por ejemplo, dibujar una pantalla de carga o un efecto de fade
                }
                break;
            case PAUSA:
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
                
                if (!cambiandoNivel) {
                    hudQuimico.render(g);
                }
                menuPausa.draw(g);
                menuPausa.update();
                break;
            case MUERTE:
                background.draw(g, camera.getxLvlOffset());
            
                // Dibujamos otros elementos del juego
                if(levelMan.getCurrentLevelIndex() != 2){
                    adminDecoraciones.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                    levelMan.draw(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                } else {
                    levelMan.draw(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                    adminDecoraciones.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                }
                adminEnemigos.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                player.render(g, camera.getxLvlOffset(), camera.getyLvlOffset());
                
                // Dibujamos el menú de muerte
                menuMuerte.draw(g);
                break;
            default:
                break;
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
    
    
    
    private void volverAlMenu() {
        estadoJuego = EstadoJuego.MUERTE;      
    }

    public void reiniciarJuego() {
        // Limpiar recursos
        if (player.getArmaActual() != null) {
            player.getArmaActual().getAdminBalas().limpiarBalas();
        }
        adminEnemigos.limpiarEnemigos();
        adminDecoraciones.limpiarDecoraciones();
        
        // Reiniciar al nivel 0
        levelMan.changeLevel(0);
        
        // Actualizar variables globales
        NIVEL_ACTUAL_DATA = levelMan.getCurrentLevel().getLvlData();
        NIVEL_ACTUAL_ALTO = NIVEL_ACTUAL_DATA.length * TILES_SIZE;
        NIVEL_ACTUAL_ANCHO = NIVEL_ACTUAL_DATA[0].length * TILES_SIZE;
        
        // Reiniciar background y cámara
        background = new Background(0);
        camera = new Camera(GAME_WIDTH, GAME_HEIGHT, NIVEL_ACTUAL_ANCHO, NIVEL_ACTUAL_ALTO);
        
        // Recrear jugador con el mismo personaje
        Personaje.TipoPersonaje tipoPersonaje = player.getPersonaje().getTipo();
        player = new Jugador(200, 200, (int) (48 * SCALE), (int) (48 * SCALE), tipoPersonaje);
        jugadorActual = player;
        hudQuimico = new HUDQuimico(player.getSistemaQuimico());
        player.loadLvlData(NIVEL_ACTUAL_DATA);
        
        // Cargar entidades y decoraciones del nivel 0
        levelMan.cargarEntidades(this);
        levelMan.cargarDecoraciones();
        
        necesitaReinicio = false;
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

    public AdministradorDecoraciones getAdminDecoraciones() {
        return adminDecoraciones;
    }

    public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }
    
    public void setEstadoJuego(EstadoJuego estadoJuego) {
        // Si venimos de MUERTE o PLAYING y vamos a MENU, marcar para reiniciar
        if ((this.estadoJuego == EstadoJuego.MUERTE || this.estadoJuego == EstadoJuego.PLAYING || this.estadoJuego == EstadoJuego.PAUSA) 
                && estadoJuego == EstadoJuego.MENU) {
            necesitaReinicio = true;
        }
        
        // Si iniciamos un nuevo juego desde el menú, asegurar que esté reiniciado
        if (this.estadoJuego == EstadoJuego.MENU && estadoJuego == EstadoJuego.SELECCION_PERSONAJE) {
            if (necesitaReinicio) {
                reiniciarJuego();
            }
        }
        
        this.estadoJuego = estadoJuego;
    }

    public Menu getMenu() {
        return menu;
    }

    public SelectorPersonajes getSelectorPersonajes() {
        return selectorPersonajes;
    }

    public boolean necesitaReinicio() {
        return necesitaReinicio;
    }
    
    public MenuPausa getMenuPausa() {
        return menuPausa;
    }

    public MenuMuerte getMenuMuerte() {
        return menuMuerte;
    }
}