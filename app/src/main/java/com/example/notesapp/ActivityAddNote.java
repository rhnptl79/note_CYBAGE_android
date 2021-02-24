package com.example.notesapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.bumptech.glide.Glide;
import com.example.notesapp.data.NotesData;
import com.example.notesapp.database.DatabaseHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class ActivityAddNote extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_LOCATION = 1;
    DatabaseHandler db;
    ImageView add_image;
    EditText et_title, et_decription;
    String latitude, longitude, image_path,voice_path;
    TextView btnSave, location;
    Button  btn_rm_pic;
    ChoosePhotoHelper choosePhotoHelper;
    LocationManager locationManager;
    NotesData notesData;

    private MediaPlayer player;

    private Button startRecordingButton, stopRecordingButton,btnPlayRecording;

    private MediaRecorder recorder;
    private boolean isPlaying;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        add_image = findViewById(R.id.add_image);
        et_title = findViewById(R.id.et_title);
        et_decription = findViewById(R.id.et_decription);
        btnSave = findViewById(R.id.btn_save);
        location = findViewById(R.id.location);
        startRecordingButton = findViewById(R.id.startRecordingButton);
        btnPlayRecording = findViewById(R.id.btnPlayRecording);
        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        btn_rm_pic = findViewById(R.id.btn_rm_pic);
        image_path = "";
        voice_path = "";
        latitude = "";
        longitude = "";
        db=new DatabaseHandler(this);

        if (getIntent().hasExtra("edit")){
            notesData=(NotesData)getIntent().getSerializableExtra("edit");
            btnSave.setText("Update Note");
            et_decription.setText(notesData.getDescription());
            et_title.setText(notesData.getTitle());
            latitude=notesData.getLat();
            longitude=notesData.getLng();

            image_path = notesData.getImagePath();
            Glide.with(ActivityAddNote.this)
                    .load(notesData.getImagePath())
                    .into(add_image);
            btn_rm_pic.setVisibility(View.VISIBLE);
            location.setText("Map");

            if (!notesData.getVoicePath().toString().isEmpty()){
                voice_path = notesData.getVoicePath();
                btnPlayRecording.setVisibility(View.VISIBLE);
            }

        }
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "locationName" + ")";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
            }}
        });
            getCurrentLocation();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("edit")){
                    db.updateNotes(new NotesData(notesData.getnId(), et_title.getText().toString(), et_decription.getText().toString(), image_path, latitude, longitude, voice_path, getIntent().getStringExtra("catId"), String.valueOf(System.currentTimeMillis())));
                }else {
                    db.addNotes(new NotesData(1, et_title.getText().toString(), et_decription.getText().toString(), image_path, latitude, longitude, voice_path, getIntent().getStringExtra("catId"), String.valueOf(System.currentTimeMillis())));
                }
                finish();
            }
        });

    }

    public void onAddNoteClick(View view) {
        switch (view.getId()) {
            case R.id.add_image:

                choosePhotoHelper = ChoosePhotoHelper.with(this)
                        .asFilePath()
                        .build(new ChoosePhotoCallback<String>() {
                            @Override
                            public void onChoose(String photo) {
                                Log.d("Picc", photo);
                                image_path = photo;
                                Glide.with(ActivityAddNote.this)
                                        .load(photo)
                                        .into(add_image);
                                btn_rm_pic.setVisibility(View.VISIBLE);
                            }
                        });

                choosePhotoHelper.showChooser();
                break;
            case R.id.startRecordingButton:
                startRecording();
                startRecordingButton.setVisibility(View.GONE);
                stopRecordingButton.setVisibility(View.VISIBLE);
                break;

            case R.id.stopRecordingButton:
                stopRecording();
                startRecordingButton.setVisibility(View.VISIBLE);
                stopRecordingButton.setVisibility(View.GONE);
                btnPlayRecording.setVisibility(View.VISIBLE);
                break;
            case R.id.btnPlayRecording:
                if (btnPlayRecording.getText().toString().equalsIgnoreCase("Play Recording")){
                    isPlaying=true;
                    btnPlayRecording.setText("Stop");
                    playRecording();
                }else{
                    btnPlayRecording.setText("Play Recording");
                    stopRecording();
                }
                break;

            case R.id.btn_save:
                if (getIntent().hasExtra("edit")){
                    db.updateNotes(new NotesData(1, et_title.getText().toString(), et_decription.getText().toString(), image_path, latitude, longitude, voice_path, getIntent().getStringExtra("catId"), String.valueOf(System.currentTimeMillis())));
                }else {
                    db.addNotes(new NotesData(1, et_title.getText().toString(), et_decription.getText().toString(), image_path, latitude, longitude, voice_path, getIntent().getStringExtra("catId"), String.valueOf(System.currentTimeMillis())));
                }
                finish();
                break;
            case R.id.btn_rm_pic:
                add_image.setImageResource(R.drawable.ic_add);
                btn_rm_pic.setVisibility(View.GONE);
                break;
        }
    }
    private void startRecording() {
        String uuid = UUID.randomUUID().toString();
        voice_path = getExternalCacheDir().getAbsolutePath() + "/" + uuid + ".3gp";
        Log.i(MainActivity.class.getSimpleName(), voice_path);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(voice_path);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName() + ":startRecording()", "prepare() failed");
        }

        recorder.start();
    }
    private void stopRecording() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        Toast.makeText(this,"Audio recorded!!",Toast.LENGTH_SHORT).show();
    }
    private void playRecording() {
        player = new MediaPlayer();
        try {
            player.setDataSource(voice_path);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlaying();
                }
            });
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName() + ":playRecording()", "prepare() failed");
        }
    }
    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                ActivityAddNote.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ActivityAddNote.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            Location locationGPS = locationManager.getLastKnownLocation(bestProvider);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                if (!getIntent().hasExtra("edit")) {

                    location.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                }
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (choosePhotoHelper!=null)
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
