package fr.media85.myradioapp;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import fr.media85.myradioapp.fragments.PlayFragment;
import fr.media85.myradioapp.models.Attributes;
import fr.media85.myradioapp.network.WebRadioApi;
import fr.media85.myradioapp.network.WebRadioClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static fr.media85.myradioapp.fragments.PlayFragment.CHANNEL_ID;


public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";
    private static final String TEST_URL = "https://www.radioking.com/play/paradise-radio"; //Here the audio player's link of the radio
    PlayFragment fragment;
    WifiManager.WifiLock wifiLock;
    private static final String TAG = "BackgroundSoundService";
    protected MediaPlayer mediaPlayer = null;
    private Bitmap bigPic;
    private MediaSessionCompat mediaSession;
    private String name, title;
    private String urlCover;
    private AudioManager mAudioManager;
    private WebRadioApi webRadioApi;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "tag");
        getPlayerNotifWithSchedule();

        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

    }


    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind()" );
        return null;
    }



    public class LocalBinder extends Binder {
        public MediaPlayerService getServiceInstance() {
            return MediaPlayerService.this;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int request = mAudioManager.requestAudioFocus(mMyFocusListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        if (intent.getAction().equals(ACTION_PLAY)) {

            switch (request){
                //if accept Start media
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    playbackNow();
                    break;
                //if don't accept Toast massage show
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    Toast.makeText(this, "AUDIOFOCUS_REQUEST_FAILED", Toast.LENGTH_SHORT).show();
                    break;
            }

        } else if (intent.getAction().equals(ACTION_STOP)){
            mediaPlayer.pause();
        }


        return START_NOT_STICKY;
    }



    private AudioManager.OnAudioFocusChangeListener mMyFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onAudioFocusChange(int focusChange) {

            switch(focusChange){
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(TAG, "onAudioFocusChange: AUDIO_GAIN");
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mediaPlayer.release();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mediaPlayer.setVolume(0.1f,0.1f);
                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void playbackNow(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock");
        wifiLock.acquire();


        /** WorkAround Huawei **/
        String tag = "com.my_app:LOCK";
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && Build.MANUFACTURER.equals("Huawei")) { tag = "LocationManagerService"; }
        PowerManager.WakeLock wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, tag); wakeLock.acquire();
        /**********************/


        try {
            // Set the stream URL location
            mediaPlayer.setDataSource(TEST_URL);
            // prepareAsync must be called after setAudioStreamType and setOnPreparedListener
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }



    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        mediaPlayer.start();
        WifiManager.WifiLock wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock");

        wifiLock.acquire();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( wifiLock != null) wifiLock.release();
        if (mediaPlayer != null) mediaPlayer.release();
        stopForeground(true);
    }


    public void registerClient(PlayFragment activity) {
        fragment = activity;
    }

    public boolean isplaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }



    private void getPlayerNotifWithSchedule(){

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {

                        webRadioApi = WebRadioClient.getAttributes().create(WebRadioApi.class);

                        Call<Attributes> call = webRadioApi.getAttributesDetails();

                        call.enqueue(new Callback<Attributes>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @SuppressLint("CheckResult")
                            @Override
                            public void onResponse(Call<Attributes> call, Response<Attributes> response) {
                                try {
                                    name = response.body().getArtist();
                                    title = response.body().getTitle();
                                    urlCover = response.body().getCover();

                                    Glide.with(MediaPlayerService.this).asBitmap().load(urlCover).into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            bigPic = resource;
                                        }
                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });


                                    /** MEDIA NOTIF **/
                                    createNotificationChannel();

                                    Intent notificationIntent = new Intent(MediaPlayerService.this, MainActivity.class);
                                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent contentIntent = PendingIntent.getActivity(MediaPlayerService.this, 1,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    // STOP INTENT
                                    Intent stopIntent = new Intent(MediaPlayerService.this, MediaPlayerService.class);
                                    stopIntent.setAction(MediaPlayerService.ACTION_STOP);
                                    PendingIntent stopPintent = PendingIntent.getService(MediaPlayerService.this, 1, stopIntent, PendingIntent.FLAG_ONE_SHOT );

                                    // PLAY INTENT
                                    Intent playIntent = new Intent(MediaPlayerService.this, MediaPlayerService.class);
                                    playIntent.setAction(MediaPlayerService.ACTION_PLAY);
                                    PendingIntent playPintent = PendingIntent.getService(MediaPlayerService.this, 1, playIntent, PendingIntent.FLAG_ONE_SHOT );

                                    Notification builder =
                                            new NotificationCompat.Builder(MediaPlayerService.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.logo)
                                                    .setContentTitle(name)
                                                    .setContentText(title)
                                                    .setAutoCancel(true)
                                                    .setLargeIcon(bigPic)
                                                    .addAction(R.drawable.ic_baseline_stop_24, "ACTION_STOP", stopPintent)
                                                    .addAction(R.drawable.ic_baseline_play_arrow_24, "ACTION_PLAY", playPintent)
                                                    .setStyle(new androidx.media.app.NotificationCompat
                                                            .MediaStyle()
                                                            .setShowActionsInCompactView(0, 1)
                                                            .setMediaSession(mediaSession.getSessionToken()))
                                                    .setContentIntent(contentIntent)
                                            .build();

                                    startForeground(1, builder);


                                } catch (Exception e) {
                                    Log.d("onResponse", "There is an error");
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<Attributes> call, Throwable t) {
                                Log.e("onFailure", t.toString());
                            }
                        });
                    }
                }, 0, 30, TimeUnit.SECONDS);
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
