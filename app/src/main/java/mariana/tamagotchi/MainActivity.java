package mariana.tamagotchi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;


import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Gatito gatito;
    private int com, ener, feli, limp, amo;
    public ProgressBar comida, energia, felicidad, limpieza, amor;
    public GifImageView gifImageView;
    public TextView comidaTV, energiaTV, felicidadTV, limpiezaTV, amorTV;
    public TextToSpeech textToSpeech;
    public static final int COMIDA = 1, ENERGIA = 2, FELICIDAD = 3, LIMPIEZA = 4, AMOR = 5, ONE_SECOND = 1000, ONE_MINUTE = 60000, TEN_SECONDS = 10000;
    private SensorManager mSensorManager, lightSensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private boolean isFaceUp, isHugOn; //true = faceup, false = facedown
    private static final double GRAVITY_THRESHOLD = SensorManager.STANDARD_GRAVITY / 2;
    public int currentTime, lastTime = 0;
    public float lux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Build gatito
        gatito = new Gatito(100, 100, 100, 100, 100);

        //Progress bars
        comida = (ProgressBar) findViewById(R.id.comida);
        energia = (ProgressBar) findViewById(R.id.energia);
        felicidad = (ProgressBar) findViewById(R.id.felicidad);
        limpieza = (ProgressBar) findViewById(R.id.limpieza);
        amor = (ProgressBar) findViewById(R.id.amor);

        comidaTV = (TextView) findViewById(R.id.textViewComida);
        energiaTV = (TextView) findViewById(R.id.textViewEnergia);
        felicidadTV = (TextView) findViewById(R.id.textViewFelicidad);
        limpiezaTV = (TextView) findViewById(R.id.textViewLimpieza);
        amorTV = (TextView) findViewById(R.id.textViewAmor);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");

        comidaTV.setTypeface(typeface);
        energiaTV.setTypeface(typeface);
        felicidadTV.setTypeface(typeface);
        limpiezaTV.setTypeface(typeface);
        amorTV.setTypeface(typeface);

        //Gif image
        gifImageView = (GifImageView) findViewById(R.id.gif);

        //Set progress bars to full
        energia.setProgress(gatito.getEnergia());
        comida.setProgress(gatito.getComida());
        felicidad.setProgress(gatito.getFelicidad());
        limpieza.setProgress(gatito.getLimpieza());
        amor.setProgress(gatito.getAmor());

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locSpanish = new Locale("spa", "MEX");
                    textToSpeech.setLanguage(locSpanish);
//                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        //Set gravity sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);

        lightSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        lightEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                lux = sensorEvent.values[0];
                amo = amor.getProgress();
                if (lux < 25) {
                    if (isFaceUp) {
                        if (!isHugOn) {
                            changeImg(R.drawable.amor);
                        }

                        plusPoints(AMOR, 50);
                        plusPoints(FELICIDAD, 20);
                        minusPoints(ENERGIA, 5);
                        minusPoints(COMIDA, 5);
                        minusPoints(LIMPIEZA, 5);

                        Log.i("Hugs are ON: ", " plus 50 points");
                        isHugOn = true;
                    }
                } else if (isHugOn) {
                    changeImg(R.drawable.pusheen);
                    isHugOn = false;
                }
                Log.i("Luminosity", " " + lux);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    public void getSpeech(String string) {
        textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void minusPoints(int name, int minus) {
        switch (name) {
            case COMIDA:
                com = comida.getProgress();
                com = com - minus;
                updateProgress(comida, com, name);
                break;
            case ENERGIA:
                ener = energia.getProgress();
                ener = ener - minus;
                updateProgress(energia, ener, name);
                break;
            case FELICIDAD:
                feli = felicidad.getProgress();
                feli = feli - minus;
                updateProgress(felicidad, feli, name);
                break;
            case LIMPIEZA:
                limp = limpieza.getProgress();
                limp = limp - minus;
                updateProgress(limpieza, limp, name);
                break;
            case AMOR:
                amo = amor.getProgress();
                amo = amo - minus;
                updateProgress(amor, amo, name);
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }

    }

    public void plusPoints(int name, int plus) {
        switch (name) {
            case COMIDA:
                com = comida.getProgress();
                com = com + plus;
                updateProgress(comida, com, name);
                break;
            case ENERGIA:
                ener = energia.getProgress();
                ener = ener + plus;
                updateProgress(energia, ener, name);
                break;
            case FELICIDAD:
                feli = felicidad.getProgress();
                feli = feli + plus;
                updateProgress(felicidad, feli, name);
                break;
            case LIMPIEZA:
                limp = limpieza.getProgress();
                limp = limp + plus;
                updateProgress(limpieza, limp, name);
                break;
            case AMOR:
                amo = amor.getProgress();
                amo = amo + plus;
                updateProgress(amor, amo, name);
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }
    }

    //Update progress bar value
    public void updateProgress(ProgressBar progressBar, int value, int name) {
        progressBar.setProgress(value);
        color(progressBar, name);
    }

    //Change progress bar color
    public void color(ProgressBar progressBar, int name) {
        int value = progressBar.getProgress();
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();

        if (value >= 75) {
            progressDrawable.setColorFilter(getColor(R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        } else if (value >= 50) {
            progressDrawable.setColorFilter(getColor(R.color.colorYellow), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        } else if (value >= 25) {
            progressDrawable.setColorFilter(getColor(R.color.colorOrange), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        } else {
            progressDrawable.setColorFilter(getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            setSpeech(name);
        }

    }

    public void setSpeech(int name) {
        switch (name) {
            case COMIDA:
                getSpeech("Por favor dame comida");
                break;
            case ENERGIA:
                getSpeech("¿Puedo irme a dormir?");
                break;
            case FELICIDAD:
                getSpeech("¿Podemos jugar un rato?");
                break;
            case LIMPIEZA:
                getSpeech("¿Puedes darme un baño?");
                break;
            case AMOR:
                getSpeech("¿Puedes abrazarme?");
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }
    }


    public void setGatito() {
        gatito.setComida(comida.getProgress());
        gatito.setAmor(amor.getProgress());
        gatito.setLimpieza(limpieza.getProgress());
        gatito.setEnergia(energia.getProgress());
        gatito.setFelicidad(felicidad.getProgress());
    }

    public void getGatito() {
        comida.setProgress(gatito.getComida());
        amor.setProgress(gatito.getAmor());
        limpieza.setProgress(gatito.getLimpieza());
        energia.setProgress(gatito.getEnergia());
        felicidad.setProgress(gatito.getFelicidad());
    }

    @Override
    protected void onPause() {
        super.onPause();
        setGatito();
        textToSpeech.stop();
        lightSensorManager.unregisterListener(lightEventListener);
        mSensorManager.unregisterListener(lightEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lightSensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        getGatito();
    }

    //Change gif image
    public void changeImg(int image) {
        gifImageView.setImageResource(image);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ener = energia.getProgress();

        if (event.values[2] >= GRAVITY_THRESHOLD) {
            //Face up
            if (!isFaceUp) {
                changeImg(R.drawable.pusheen);
            }

            if (itsTime(5)) {
                minusPoints(AMOR, 1);
                minusPoints(FELICIDAD, 1);
                minusPoints(ENERGIA, 1);
                minusPoints(COMIDA, 1);
                minusPoints(LIMPIEZA, 1);
                Log.i("Device is UP: ", " minus 1 point");
            }
            Log.i("Device is UP: ", "");
            isFaceUp = true;
        } else if (event.values[2] <= (GRAVITY_THRESHOLD * -1)) {
            //Si estaba hacia arriba y cambio hacia abajo, cambia la imagen
            if (isFaceUp) {
                changeImg(R.drawable.dormir2);
            }
            if (itsTime(1)) {
                plusPoints(AMOR, 5);
                plusPoints(FELICIDAD, 5);
                plusPoints(ENERGIA, 5);
                Log.i("Device is DOWN: ", " plus 5 points");
            }

            if (itsTime(5)) {
                minusPoints(COMIDA, 1);
                minusPoints(LIMPIEZA, 1);
            }
            isFaceUp = false;
        }
    }

    public boolean itsTime(int number) {
        currentTime = Calendar.getInstance().get(Calendar.SECOND);
        if (lastTime != currentTime) {
            lastTime = currentTime;
            Log.i("Time", " " + currentTime);
            if (currentTime % number == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
