package ekia;

public class DiseñoMapa {

    public static DISEÑO dis = DISEÑO.libre; // Por default
    public Handler contenido; // Este es para que se carguen las paredes e items aqui y se transfiera el diseño al mapa nuevo
    public Handler handler; // Para darselo a los objetos al crearlo

    public DiseñoMapa(DISEÑO dis, Handler handler) {
        DiseñoMapa.dis = dis;
        contenido = new Handler();
        this.handler = handler;
    }

    public void diseñar() { // En esta funcion se cargara los diseños de mapas dependiendo del id
        contenido.object.clear();
        if (dis == DISEÑO.intermedio) {
            float x = 0;
            float y = 100;
            int ancho = 200;
            int alto = 20;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 0;
            y = 250;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 180;
            y = 250;
            ancho = 20;
            alto = 100;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = EKIA.ANCHO - 205;
            y = 100;
            ancho = 200;
            alto = 20;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            y = 250;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            ancho = 20;
            alto = 100;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = EKIA.ANCHO / 2 - 20;
            y = EKIA.ALTO / 2 - 80;
            ancho = 40;
            alto = 40;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
        } else if (dis == DISEÑO.laberinto) {
            float x = 0;
            float y = 0;
            int ancho = 40;
            int alto = 40;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 595;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 298;
            y = 20;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 139;
            y = 100;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 457;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 50;
            y = 140;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 245;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 351;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 547;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));

            //Reflejo
            x = 0;
            y = 408;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 595;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 298;
            y = 388;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 139;
            y = 308;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 457;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 50;
            y = 268;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 245;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 351;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 547;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));

            x = 129;
            y = 204;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 298;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));
            x = 467;
            contenido.addObject(new Pared(x, y, ID.ParedR, handler, null, ancho, alto));

        }
    }

    public Handler getDiseño() { // Regresa el mapa que se va a crear
        concatenaDiseño();
        return handler;
    }

    public void concatenaDiseño() {
        for (int i = 0; i < contenido.object.size(); i++) {
            GameObject temp = contenido.object.get(i);

            handler.addObject(temp);
        }
    }

    public void setDiseño(DISEÑO dis) {
        DiseñoMapa.dis = dis; // Para cambiar el diseño
    }

}
