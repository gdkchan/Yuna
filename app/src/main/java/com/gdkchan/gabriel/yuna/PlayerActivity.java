package com.gdkchan.gabriel.yuna;

import com.gdkchan.gabriel.yuna.YunaCore.StreamInfo;
import com.gdkchan.gabriel.yuna.YunaCore.Youtube.*;

import android.app.DownloadManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;

/**
 * Created by gabriel on 09/10/2015.
 */
public class PlayerActivity extends AppCompatActivity {
    StreamInfo Stream;
    String VideoTitle;
    String VideoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        final VideoView Player = (VideoView) findViewById(R.id.player);
        MediaController Controller = new MediaController(this);
        Controller.setAnchorView(Player);
        Player.setMediaController(Controller);

        Intent PlayerIntent = getIntent();
        VideoTitle = PlayerIntent.getStringExtra("VideoTitle");
        VideoURL = PlayerIntent.getStringExtra("VideoURL");

        if (savedInstanceState != null)
        {
            String VideoStream = savedInstanceState.getString("VideoStream");
            int SeekTo = savedInstanceState.getInt("Position");

            if (VideoStream != null)
            {
                Player.setVideoURI(Uri.parse(Stream.URL));
                Player.seekTo(SeekTo);
                Player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer MP) {
                        MP.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            @Override
                            public void onSeekComplete(MediaPlayer MP) {
                                Player.start();
                            }
                        });

                    }
                });

                return;
            }
        }

        Stream = new StreamInfo();
        new VideoStream.GetStreams(Player, Stream).execute(VideoURL);
        Toast.makeText(getApplicationContext(), getString(R.string.loading), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        VideoView Player = (VideoView) findViewById(R.id.player);
        savedInstanceState.putString("VideoStream", Stream.URL);
        savedInstanceState.putInt("Position", Player.getCurrentPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        MenuItem ShareItem = menu.findItem(R.id.action_share);
        MenuItem DownloadItem = menu.findItem(R.id.action_download);

        ShareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent Share = new Intent(android.content.Intent.ACTION_SEND);
                Share.setType("text/plain");

                Share.putExtra(Intent.EXTRA_SUBJECT, VideoTitle);
                Share.putExtra(Intent.EXTRA_TEXT, VideoURL);

                startActivity(Intent.createChooser(Share, getString(R.string.share_txt)));
                return true;
            }
        });

        DownloadItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (Stream != null) {
                    DownloadManager Manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request Request = new DownloadManager.Request(Uri.parse(Stream.URL));
                    Request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Filter(VideoTitle + ".mp4"));
                    Long Reference = Manager.enqueue(Request);
                }
                return true;
            }
        });

        return true;
    }

    private String Filter(String Source)
    {
        if (Source == null) return null;
        String ReservedChars = "?:\"*|/\\<>";
        for (int i = 0; i < ReservedChars.length(); i++) {
            String Chr = ReservedChars.substring(i, i + 1);
            Source.replace(Chr, "");
        }
        return Source;
    }
}
