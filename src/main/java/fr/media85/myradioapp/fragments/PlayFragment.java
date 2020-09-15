package fr.media85.myradioapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import fr.media85.myradioapp.MediaPlayerService;
import fr.media85.myradioapp.R;
import fr.media85.myradioapp.network.WebRadioApi;
import fr.media85.myradioapp.models.Attributes;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PlayFragment extends Fragment  {

    private ImageView playIv;
    private MediaPlayerService service;
    private String name, title;
    private String url = "https://api.radioking.io/widget/radio/paradise-radio/track/";
    private TextView nameTv, titleTv;
    private ImageView coverIv, bgIv;
    private String urlCover;
    private ProgressBar progressBar;
    private Bitmap bigPic;
    private MediaSessionCompat mediaSession;
    private SeekBar volumeSb;
    private AudioManager audioManager = null;

    private ProgressDialog working_dialog;


    public static final String CHANNEL_ID = "exempleServiceChannel";

    public PlayFragment(){

    }


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        playIv = rootView.findViewById(R.id.playIv);
        nameTv = rootView.findViewById(R.id.nameTv);
        titleTv = rootView.findViewById(R.id.titleTv);
        coverIv = rootView.findViewById(R.id.coverIv);
        bgIv = rootView.findViewById(R.id.bgIv);
        progressBar = rootView.findViewById((R.id.progressBar));
        volumeSb = rootView.findViewById(R.id.volumeSb);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();

        titrageShowWithSchedule();


        playIv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                    if(playIv.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24).getConstantState())){
                        Intent serviceIntent = new Intent(getContext(), MediaPlayerService.class);
                        serviceIntent.setAction(MediaPlayerService.ACTION_PLAY);
                        //getContext().startService(serviceIntent);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getContext().startForegroundService(serviceIntent);
                        } else {
                            getContext().startService(serviceIntent);
                        }


                        getContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
                        playIv.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                        showWorkingDialog();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeWorkingDialog();
                            }
                            }, 10000);

                    } else {
                        Intent serviceIntent = new Intent(getContext(), MediaPlayerService.class);
                        serviceIntent.setAction(MediaPlayerService.ACTION_STOP);
                        getContext().stopService(serviceIntent);
                        getContext().unbindService(mConnection);
                        playIv.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    }

            }
        });

        return rootView;
    }



    private void initControls()
    {
        try
        {
            audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

            volumeSb.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSb.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }}


    private void titrageShowWithSchedule(){

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        WebRadioApi service = retrofit.create(WebRadioApi.class);
                        Call<Attributes> call = service.getAttributesDetails();
                        call.enqueue(new Callback<Attributes>() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void onResponse(Call<Attributes> call, Response<Attributes> response) {
                                try {
                                    name = response.body().getArtist();
                                    title = response.body().getTitle();
                                    urlCover = response.body().getCover();
                                    Log.e("FragmentPlay", "onResponse: " + " " + "name: " +name + " "+ "title: "+ title );

                                    nameTv.setText(name);
                                    titleTv.setText(title);

                                    Glide.with(getContext())  //2
                                            .load(urlCover) //3
                                            .circleCrop() //4
                                            .into(coverIv);

                                    Glide.with(getContext())
                                            .load(urlCover)
                                            .apply(bitmapTransform(new MultiTransformation<Bitmap>(new BlurTransformation(25, 3),
                                                    new ColorFilterTransformation(Color.argb(20, 0, 0, 0)))))
                                            .into(bgIv);


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
                }, 0, 20, TimeUnit.SECONDS);



    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder _service) {

            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) _service;
            service = binder.getServiceInstance(); //Get instance of your service!
            service.registerClient(PlayFragment.this); //Activity register in the service as client for callabcks!

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    private void showWorkingDialog() {
        working_dialog = ProgressDialog.show(getContext(), "","Loading please wait...", true);
    }

    private void removeWorkingDialog() {
        if (working_dialog != null) {
            working_dialog.dismiss();
            working_dialog = null;
        }
    }


    public void stopMusicService() {
        if (service != null) {
            try {
                service.stopSelf();
                getContext().unbindService(mConnection);
                getContext().stopService(new Intent(getContext(), service.getClass()));
                service = null;
            } catch (IllegalArgumentException ex) {
                getContext().stopService(new Intent(getContext(), service.getClass()));
                service = null;
                ex.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(getContext(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_STOP);
        getContext().stopService(serviceIntent);
        //getContext().unbindService(mConnection);
    }


}
