package mariana.tamagotchi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.Random;


import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Gatito gatito;
    private int com, ener, feli, limp, amo, lastrandom;
    private int comStatus, enerStatus, feliStatus, limpStatus, amoStatus;
    public ProgressBar comida, energia, felicidad, limpieza, amor;
    public GifImageView gifImageView;
    public TextView comidaTV, energiaTV, felicidadTV, limpiezaTV, amorTV;
    public TextToSpeech textToSpeech;
    public static final int COMIDA = 1, ENERGIA = 2, FELICIDAD = 3, LIMPIEZA = 4, AMOR = 5, ONE_SECOND = 1000, ONE_MINUTE = 60000, TEN_SECONDS = 10000;
    private SensorManager mSensorManager, lightSensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private boolean isFaceUp, isHugOn, isFabOpen; //true = faceup, false = facedown
    private static final double GRAVITY_THRESHOLD = SensorManager.STANDARD_GRAVITY / 2;
    public int currentTime, lastTime = 0;
    public float lux;
    MediaPlayer mp;
    public Animation FabOpen, FabClose, FabClockwise, FabAnticlockwise;
    public Random randy;
    public Locale locSpanish, locSpain;
    public FloatingActionButton fab, fabComida, fabLimpieza, fabMusica, fabMic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Build gatito
        gatito = new Gatito(100, 100, 100, 100, 100);
        comStatus = 1;
        limpStatus = 1;
        feliStatus = 1;
        amoStatus = 1;
        enerStatus = 1;

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

        locSpain = new Locale("es", "ES");

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    locSpanish = new Locale("spa", "MEX");
                    textToSpeech.setLanguage(locSpanish);
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

        //Fab button
        fab = (FloatingActionButton) findViewById(R.id.mainFAB);
        fabComida = (FloatingActionButton) findViewById(R.id.foodFAB);
        fabLimpieza = (FloatingActionButton) findViewById(R.id.cleanFAB);
        fabMusica = (FloatingActionButton) findViewById(R.id.musicFAB);
        fabMic = (FloatingActionButton) findViewById(R.id.micFAB);

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);


        //Media player
        mp = MediaPlayer.create(this, R.raw.yolanda);

    }

    //Make T2S speak
    public void getSpeech(String string) {
        mp.stop();
        textToSpeech.speak(string, TextToSpeech.QUEUE_ADD, null, null);
    }

    //Substract points to progress bar
    public void minusPoints(int name, int minus) {
        switch (name) {
            case COMIDA:
                com = comida.getProgress();
                com = balance(com, name);
                com = com - minus;
                com = balance(com, name);
                updateProgress(comida, com, name);
                break;
            case ENERGIA:
                ener = energia.getProgress();
                ener = balance(ener, name);
                ener = ener - minus;
                ener = balance(ener, name);
                updateProgress(energia, ener, name);
                break;
            case FELICIDAD:
                feli = felicidad.getProgress();
                feli = balance(feli, name);
                feli = feli - minus;
                feli = balance(feli, name);
                updateProgress(felicidad, feli, name);
                break;
            case LIMPIEZA:
                limp = limpieza.getProgress();
                limp = balance(limp, name);
                limp = limp - minus;
                limp = balance(limp, name);
                updateProgress(limpieza, limp, name);
                break;
            case AMOR:
                amo = amor.getProgress();
                amo = balance(amo, name);
                amo = amo - minus;
                amo = balance(amo, name);
                updateProgress(amor, amo, name);
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }

    }

    //Add points to progress bar
    public void plusPoints(int name, int plus) {
        switch (name) {
            case COMIDA:
                com = comida.getProgress();
                com = balance(com, name);
                com = com + plus;
                com = balance(com, name);
                updateProgress(comida, com, name);
                break;
            case ENERGIA:
                ener = energia.getProgress();
                ener = balance(ener, name);
                ener = ener + plus;
                ener = balance(ener, name);
                updateProgress(energia, ener, name);
                break;
            case FELICIDAD:
                feli = felicidad.getProgress();
                feli = balance(feli, name);
                feli = feli + plus;
                feli = balance(feli, name);
                updateProgress(felicidad, feli, name);
                break;
            case LIMPIEZA:
                limp = limpieza.getProgress();
                limp = balance(limp, name);
                limp = limp + plus;
                limp = balance(limp, name);
                updateProgress(limpieza, limp, name);
                break;
            case AMOR:
                amo = amor.getProgress();
                amo = balance(amo, name);
                amo = amo + plus;
                amo = balance(amo, name);
                updateProgress(amor, amo, name);
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }
    }

    //Make sure points are within range
    public int balance(int x, int name) {
        if (x <= 0) {
            x = 0;
            updateStatus(name, 5);
        }
        if (x > 100) {
            x = 100;
            updateStatus(name, 1);
        }
        return x;
    }

    //Update progress bar value
    public void updateProgress(ProgressBar progressBar, int value, int name) {
        progressBar.setProgress(value);
        color(progressBar, name);
    }

    //If status changes, speak.
    public void updateStatus(int name, int status) {
        switch (name) {
            case COMIDA:
                if (status != comStatus) {
                    comStatus = status;
                    if (status == 4) {
                        setSpeech(name);
                    }
                }
                break;
            case ENERGIA:
                if (status != enerStatus) {
                    enerStatus = status;
                    if (status == 4) {
                        setSpeech(name);
                    } else if (status == 5) {
                        changeImg(R.drawable.dead);
                        mp.stop();
                        textToSpeech.stop();
                        lightSensorManager.unregisterListener(lightEventListener);
                        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
                        fab.setVisibility(View.INVISIBLE);
                        textToSpeech.speak("Tas bien suato, ya lo mataste", TextToSpeech.QUEUE_FLUSH, null, null);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                textToSpeech.shutdown();
                            }
                        }, 2600);
                    }
                }
                break;
            case FELICIDAD:
                if (status != feliStatus) {
                    feliStatus = status;
                    if (status == 4) {
                        setSpeech(name);
                    }
                }
                break;
            case LIMPIEZA:
                if (status != limpStatus) {
                    limpStatus = status;
                    if (status == 4) {
                        setSpeech(name);
                    }
                }
                break;
            case AMOR:
                if (status != amoStatus) {
                    amoStatus = status;
                    if (status == 4) {
                        setSpeech(name);
                    }
                }
                break;
            default:
                Log.i("Error", "Rayos");
                break;
        }
    }

    //Change progress bar color
    public void color(ProgressBar progressBar, int name) {
        int value = progressBar.getProgress();
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();

        if (value >= 75) {
            progressDrawable.setColorFilter(getColor(R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            updateStatus(name, 1);
        } else if (value >= 50) {
            progressDrawable.setColorFilter(getColor(R.color.colorYellow), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            updateStatus(name, 2);
        } else if (value >= 25) {
            progressDrawable.setColorFilter(getColor(R.color.colorOrange), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            updateStatus(name, 3);
        } else {
            progressDrawable.setColorFilter(getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            updateStatus(name, 4);
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
        mp.stop();
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
        mp.stop();
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

    public void mainFab(View view) {
        if (isFabOpen) {
            fabComida.startAnimation(FabClose);
            fabLimpieza.startAnimation(FabClose);
            fabMusica.startAnimation(FabClose);
            fabMic.startAnimation(FabClose);
            fab.startAnimation(FabAnticlockwise);
            fabComida.setClickable(false);
            fabMic.setClickable(false);
            fabLimpieza.setClickable(false);
            fabMusica.setClickable(false);
            isFabOpen = false;
        } else {
            fabComida.startAnimation(FabOpen);
            fabComida.setClickable(true);
            fabLimpieza.startAnimation(FabOpen);
            fabLimpieza.setClickable(true);
            fabMusica.startAnimation(FabOpen);
            fabMusica.setClickable(true);
            fabMic.startAnimation(FabOpen);
            fabMic.setClickable(true);
            fab.startAnimation(FabClockwise);
            isFabOpen = true;
        }
    }

    public void dance(View view) {
        getDance();
    }

    public void getDance() {
        mp.stop();

        int i = random(0, 6);
        switch (i) {
            case 0:
                updateDanceSong(R.drawable.musica, R.raw.bip);
                break;
            case 1:
                updateDanceSong(R.drawable.musica2, R.raw.bip2);
                break;
            case 2:
                updateDanceSong(R.drawable.musica3, R.raw.yolanda);
                break;
            case 3:
                updateDanceSong(R.drawable.musica4, R.raw.bip4);
                break;
            case 4:
                updateDanceSong(R.drawable.musica5, R.raw.bip5);
                break;
            case 5:
                updateDanceSong(R.drawable.musica6, R.raw.bip6);
                break;

        }
    }

    public void feed(View view) {
        getFood();
    }

    public void getFood() {
        int i = random(0, 7);
        switch (i) {
            case 0:
                updateFeedImage(R.drawable.comer);
                break;
            case 1:
                updateFeedImage(R.drawable.comer2);
                break;
            case 2:
                updateFeedImage(R.drawable.comer3);
                break;
            case 3:
                updateFeedImage(R.drawable.comer4);
                break;
            case 4:
                updateFeedImage(R.drawable.comer5);
                break;
            case 5:
                updateFeedImage(R.drawable.comer6);
                break;
            case 6:
                updateFeedImage(R.drawable.comer7);
                break;
        }
    }

    public void updateFeedImage(int img) {
        changeImg(img);
        plusPoints(FELICIDAD, 30);
        plusPoints(COMIDA, 50);
        minusPoints(LIMPIEZA, 30);
        minusPoints(ENERGIA, 10);
    }

    public void wash(View view) {
        getShower();
    }

    public void getShower() {
        changeImg(R.drawable.shower);
        plusPoints(LIMPIEZA, 100);
        minusPoints(FELICIDAD, 20);
        minusPoints(AMOR, 20);
        minusPoints(COMIDA, 5);
        minusPoints(ENERGIA, 10);
    }


    public void updateDanceSong(int img, int music) {
        changeImg(img);
        mp = MediaPlayer.create(this, music);
        mp.start();
        plusPoints(FELICIDAD, 100);
        plusPoints(AMOR, 100);
        minusPoints(ENERGIA, 10);
    }

    public int random(int min, int max) {
        max = max - 1;
        randy = new Random();
        int randomNum = randy.nextInt((max - min) + 1) + min;
        //lastrandom = randomNum;
        while (randomNum == lastrandom) {
            randomNum = randy.nextInt((max - min) + 1) + min;
        }
        lastrandom = randomNum;
        return randomNum;
    }

    public void calling(View view) {
        changeImg(R.drawable.hablar);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Speech to text no esta disponible en tu dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    public void decodeSpeech(String speech) {
        Toast.makeText(this, speech, Toast.LENGTH_SHORT).show();
        switch (speech) {
            case "food":
                getFood();
                break;
            case "shower":
                getShower();
                break;
            case "dance":
                getDance();
                break;
            default:
                Toast.makeText(this, "No te entendí", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    decodeSpeech(result.get(0));
                }
                break;
        }
    }
}
