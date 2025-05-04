// src/Elementos/Personaje.java
package Elementos;

import Utilz.LoadSave;

public class Personaje {
    public enum TipoPersonaje {
        ECLIPSA("Eclipsa (Mujer Loba)", LoadSave.PLAYER_ATLAS, 150f, 3.5f),
        HALAN("Dr. Halan (Científico)", LoadSave.PLAYER_ATLAS_HALAN, 100f, 3f),
        VALTHOR("Valthor (Caballero)", LoadSave.PLAYER_ATLAS_VALTHOR, 200f, 2f);
        
        private final String nombre;
        private final String spriteAtlas;
        private final float vidaMaxima;
        private final float velocidad;
        
        TipoPersonaje(String nombre, String spriteAtlas, float vidaMaxima, float velocidad) {
            this.nombre = nombre;
            this.spriteAtlas = spriteAtlas;
            this.vidaMaxima = vidaMaxima;
            this.velocidad = velocidad;
        }
        
        public String getNombre() { return nombre; }
        public String getSpriteAtlas() { return spriteAtlas; }
        public float getVidaMaxima() { return vidaMaxima; }
        public float getVelocidad() { return velocidad; }
    }
    
    private TipoPersonaje tipo;
    
    public Personaje(TipoPersonaje tipo) {
        this.tipo = tipo;
    }
    
    public String getNombre() { return tipo.getNombre(); }
    public String getSpriteAtlas() { return tipo.getSpriteAtlas(); }
    public float getVidaMaxima() { return tipo.getVidaMaxima(); }
    public float getVelocidad() { return tipo.getVelocidad(); }
    
    public void usarHabilidadEspecial() {
        switch(tipo) {
            case ECLIPSA:
                System.out.println("¡Eclipsa usa su habilidad de loba!");
                break;
            case HALAN:
                System.out.println("¡Dr. Halan usa su conocimiento químico!");
                break;
            case VALTHOR:
                System.out.println("¡Valthor usa su defensa de caballero!");
                break;
        }
    }
}