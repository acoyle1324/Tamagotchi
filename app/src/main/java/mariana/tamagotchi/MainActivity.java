package mariana.tamagotchi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static Gatito gatito;
    private static int com = 100, ener = 100, feli = 100, limp = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    public void color() {
//        if (limpieza.getPercentComplete() > 0.75) {
//            limpieza.setForeground(Color.green);
//        }
//        if (limpieza.getPercentComplete() < 0.75) {
//            limpieza.setForeground(Color.yellow);
//        }
//        if (limpieza.getPercentComplete() < 0.5) {
//            limpieza.setForeground(Color.orange);
//        }
//        if (limpieza.getPercentComplete() < 0.25) {
//            limpieza.setForeground(Color.red);
//        }
//    }
}
