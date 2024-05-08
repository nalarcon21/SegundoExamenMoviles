package com.example.segundoexamenmoviles;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Lista extends AppCompatActivity implements SongAdapter.OnItemClickListener{
    private List<Songs> songList = new ArrayList<>();
    private RecyclerView recyclerView;
    SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private  ImageButton btnPrevious;
    private LinearLayout liner;
    private TextView textView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.listRecyclerView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnSiguiente);
        btnPrevious = findViewById(R.id.btnAnterior);
        liner = findViewById(R.id.llReproductor);
        textView = findViewById(R.id.Reproduciendo);
        seekBar = findViewById(R.id.seekBar);
        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnPlayPause.setBackgroundResource(R.drawable.pause);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               togglePlayback();
            }
        });
    }
    public void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songList.add(new Songs(R.drawable.pedropedro, "pedro raffaella carra", R.raw.la_cancion_de_pedro));
        songList.add(new Songs(R.drawable.unstoppable, "Unstoppable", R.raw.unstoppable));
        songList.add(new Songs(R.drawable.fade, "Faded", R.raw.faded));
        songList.add(new Songs(R.drawable.alwaysremenberusthisway, "Always Remember Us This Way", R.raw.alwaysrememberusthisway));
        songList.add(new Songs(R.drawable.idontwanttotalkaboutit, "I Don't Want To Talk About It", R.raw.idontwanttotalkaboutit));

        SongAdapter adapter = new SongAdapter(songList, this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        if (position == currentSongIndex) {
            togglePlayback();
        } else {
            Songs song = songList.get(position);
            playSong(song.getResourceId());
            currentSongIndex = position;

            liner.setBackground(ContextCompat.getDrawable(this, song.getImage()));
            textView.setText(song.getTittle());
        }
    }
    //Mwetodo para controlar o verificar el estado del mediaplayer si esta o no repoduciendo
    private void togglePlayback() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setBackgroundResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                btnPlayPause.setBackgroundResource(R.drawable.pause);
            }
        }
    }

    private void playSong(int songResourceId) {
        stopSong();
        mediaPlayer = MediaPlayer.create(this, songResourceId);
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());

            seekBar.setProgress(0);

            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSong();
                    playNextSong();
                }
            });

            final int[] currentPosition = {0};

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        try {
                            Thread.sleep(1000);
                            currentPosition[0] = mediaPlayer.getCurrentPosition();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(currentPosition[0]);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
        }
    }




    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playNextSong() {
        if (currentSongIndex < songList.size() - 1) {
            currentSongIndex++;
            Songs nextSong = songList.get(currentSongIndex);
            playSong(nextSong.getResourceId());
            liner.setBackground(ContextCompat.getDrawable(this, nextSong.getImage()));
            textView.setText(nextSong.getTittle());
        }
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            Songs previousSong = songList.get(currentSongIndex);
            playSong(previousSong.getResourceId());
            liner.setBackground(ContextCompat.getDrawable(this, previousSong.getImage()));
            textView.setText(previousSong.getTittle());
        }
    }
}