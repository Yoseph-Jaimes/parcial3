package com.parcial3.model.enums;

public enum Competencia {
    COMUNICACION_ESCRITA("Comunicación Escrita"),
    RAZONAMIENTO_CUANTITATIVO("Razonamiento Cuantitativo"),
    LECTURA_CRITICA("Lectura Crítica"),
    COMPETENCIAS_CIUDADANAS("Competencias Ciudadanas"),
    INGLES("Inglés"),
    FORMULACION_PROYECTOS("Formulación De Proyectos De Ingeniería"),
    PENSAMIENTO_CIENTIFICO("Pensamiento Científico - Matemáticas Y Estadística"),
    DISENO_SOFTWARE("Diseño De Software");

    private String nombre;

    Competencia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}