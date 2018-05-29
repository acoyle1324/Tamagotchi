package mariana.tamagotchi;

/**
 * Created by Mariana on 21/05/2018.
 */

public class Gatito {
    private int comida, energia, felicidad, limpieza, amor;

    public Gatito(int c, int e, int f, int l, int a) {
        comida = c;
        energia = e;
        felicidad = f;
        limpieza = l;
        amor = a;
    }

    public int getAmor() {
        return amor;
    }

    public void setAmor(int a) {
        this.amor = a;
    }

    public void setComida(int c) {
        comida = c;
    }

    public int getComida() {
        return comida;
    }

    public void setEnergia(int e) {
        energia = e;
    }

    public int getEnergia() {
        return energia;
    }

    public void setFelicidad(int f) {
        felicidad = f;
    }

    public int getFelicidad() {
        return felicidad;
    }

    public void setLimpieza(int l) {
        limpieza = l;
    }

    public int getLimpieza() {
        return limpieza;
    }

    @Override
    public String toString() {
        String x = "Gatito\n\nComida: " + getComida() + "\nEnergia: " + getEnergia() + "\nFelicidad: " + getFelicidad() + "\nLimpieza: " + getLimpieza() + "\nAmor: " + getAmor();
        return x;
    }
}
