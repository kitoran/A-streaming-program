package com.example.streamingclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Thread t = new Thread() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run()
                    {
                        try {
                            short[] buffer = new short[1024];
                            byte[] googleIsAids = new byte[buffer.length*2];
                            int minBufferSize = 64;
                            AudioTrack a = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

                            a.play();
                            Socket s;
                            s = new Socket("192.168.0.121", 6602);

                            InputStream ss = s.getInputStream();
                            MediaCodec codec = MediaCodec.createDecoderByType("audio/mpeg");
                            while (true) {
                                int ind = codec.dequeueInputBuffer(0);
                                if(ind != -1) {
                                    ByteBuffer bb = codec.getInputBuffer(ind);
                                    ss.read(googleIsAids);
                                    bb.clear();
                                    bb.put(googleIsAids);
                                    codec.queueInputBuffer(ind, 0, buffer.length*2);
                                }
                                ByteBuffer.wrap(googleIsAids).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
                                a.write( buffer, 0, buffer.length );  //write to the audio buffer.... and start all over again!

                            }
                        } catch (SecurityException e) {
                            TextView tw = (TextView)findViewById(R.id.textView);
                            tw.setText(e.toString());
                        } catch (IOException e) {
                            TextView tw = (TextView)findViewById(R.id.textView);
                            try {
                                tw.setText(e.toString());
                            } catch (Exception e1) {
                                e1.printStackTrace();

                                // Prints what exception has been thrown
                                System.out.println(e1);
                            }
                        } catch (RuntimeException e) {
                            TextView tw = (TextView)findViewById(R.id.textView);
                            tw.setText(e.toString());
                        }
                    }
                };
                    t.start();
//                Intent browserIntent =
//                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mkyong.com"));
//                startActivity(browserIntent);
            }

        });

    }
    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        return;

    }

}
