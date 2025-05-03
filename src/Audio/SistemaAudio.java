package Audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SistemaAudio {
    private Map<String, Clip> clips;
    private Map<String, Float> volumenPorSonido;
    private float volumenGeneral;
    private float volumenMusica;
    private float volumenEfectos;
    private Clip musicaActual;
    
    public SistemaAudio() {
        clips = new HashMap<>();
        volumenPorSonido = new HashMap<>();
        volumenGeneral = 1.0f;
        volumenMusica = 0.8f;
        volumenEfectos = 1.0f;
        musicaActual = null;
    }
    
    public void cargarSonido(String nombre, String ruta) {
        try {
            URL url = getClass().getResource("/recursos/audio/" + ruta);
            if (url == null) {
                System.err.println("ADVERTENCIA: No se encontró el archivo de audio: " + ruta);
                System.err.println("Ruta completa buscada: /recursos/audio/" + ruta);
                System.err.println("Por favor, asegúrate de que el archivo existe en src/recursos/audio/");
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clips.put(nombre, clip);
            volumenPorSonido.put(nombre, 1.0f);
            System.out.println("Audio cargado exitosamente: " + ruta);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error cargando sonido: " + ruta);
            e.printStackTrace();
        }
    }
    
    public void reproducirEfecto(String nombre) {
        Clip clip = clips.get(nombre);
        if (clip != null) {
            clip.setFramePosition(0);
            ajustarVolumen(clip, volumenGeneral * volumenEfectos * volumenPorSonido.get(nombre));
            clip.start();
        } else {
            System.err.println("ADVERTENCIA: No se encontró el clip de audio: " + nombre);
        }
    }
    
    public void reproducirMusica(String nombre, boolean loop) {
        // Detener música actual
        if (musicaActual != null && musicaActual.isRunning()) {
            musicaActual.stop();
        }
        
        Clip clip = clips.get(nombre);
        if (clip != null) {
            clip.setFramePosition(0);
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            ajustarVolumen(clip, volumenGeneral * volumenMusica * volumenPorSonido.get(nombre));
            clip.start();
            musicaActual = clip;
        } else {
            System.err.println("ADVERTENCIA: No se encontró el clip de música: " + nombre);
        }
    }
    
    public void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual = null;
        }
    }
    
    public void pausarMusica() {
        if (musicaActual != null && musicaActual.isRunning()) {
            musicaActual.stop();
        }
    }
    
    public void reanudarMusica() {
        if (musicaActual != null && !musicaActual.isRunning()) {
            musicaActual.start();
        }
    }
    
    private void ajustarVolumen(Clip clip, float volumen) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volumen) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    public void setVolumenGeneral(float volumen) {
        this.volumenGeneral = Math.max(0.0f, Math.min(1.0f, volumen));
        actualizarVolumenMusica();
    }
    
    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0.0f, Math.min(1.0f, volumen));
        actualizarVolumenMusica();
    }
    
    public void setVolumenEfectos(float volumen) {
        this.volumenEfectos = Math.max(0.0f, Math.min(1.0f, volumen));
    }
    
    private void actualizarVolumenMusica() {
        if (musicaActual != null) {
            String nombreClip = obtenerNombreClip(musicaActual);
            if (nombreClip != null && volumenPorSonido.containsKey(nombreClip)) {
                ajustarVolumen(musicaActual, volumenGeneral * volumenMusica * volumenPorSonido.get(nombreClip));
            }
        }
    }
    
    private String obtenerNombreClip(Clip clip) {
        for (Map.Entry<String, Clip> entry : clips.entrySet()) {
            if (entry.getValue() == clip) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public float getVolumenGeneral() { return volumenGeneral; }
    public float getVolumenMusica() { return volumenMusica; }
    public float getVolumenEfectos() { return volumenEfectos; }
}