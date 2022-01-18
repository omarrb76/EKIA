package ekia;

import java.text.DecimalFormat;

public class Cronometro {

    private float seg, aux;

    public Cronometro(float seg) {
        this.aux = this.seg = seg * 60; // Se multiplica por 60 porque son la cantidad de frames por segundo
    }

    public boolean tick() { // Regresara un verdadero si ya se murio

        seg--;

        if (seg <= 0) { // Ya se cumplio el tiempo de vida de este objeto
            return true;
        }

        return false;
    }

    // Estamos teniendo en cuenta que lo maximo que se puede poner son minutos, no horas
    private float getMinutos() {
        return seg / 3600; // Regresa los minutos
    }

    private float getSegundos() {
        return seg % 3600 / 60;
    }

    @Override
    public String toString() {
        DecimalFormat formato1 = new DecimalFormat("##.00");
        String minutes = String.format("%02d", (int) getMinutos());
        String res = minutes + " : " + formato1.format(getSegundos());
        return res;
    }

    public float getTiempo() { // Para los items
        return seg;
    }

    public void reiniciar() {
        seg = aux; // reinicia el contador
    }

    public boolean terminado() { // Regresa si ya acabo el cronometro sin meterse al tick
        if (seg <= 0) {
            return true;
        } else {
            return false;
        }
    }

}
