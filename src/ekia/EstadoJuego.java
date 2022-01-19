package ekia;

// Este enum nos servira para referirnos a que estado esta el juego
// Como pausa, en juego, menu, etc...
public enum EstadoJuego {
    Portada,
    Menu,
    Juego,
    Fin,
    SeleccionPersonaje, // Por el momento, solo sera de mapa
    Online,
    Host,
    Client,
    SeleccionPersonajeOnline, // Es lo mismo que el seleccion personaje, pero para cuando estamos en linea
    EresUnCobarde,
    Ayuda,
    Pausa,
    ErrorOnline
}
