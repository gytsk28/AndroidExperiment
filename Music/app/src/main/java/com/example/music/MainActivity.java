package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity<systemPath, appFolderPath> extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaScannerNotifier.OnScanListener {

    private static final int NOT_NOTICE = 100;
    ImageView nextIv, playIv, lastIv;
    TextView singerTv, songTv;
    SeekBar mSeekProgress;
    TextView mTvPlayTime,mTvTotalTime;
    RecyclerView musicRv;
    List<LocalMusicBean> mDatas;
    private LocalMusicAdapter adapter;
    //记录当前正在播放的音乐位置
    int currentPlayPosition = -1;
    int totalTime = 0;
    //记录音乐暂停时进度条的位置
    int currentPausePositionInSong = 0;
    MediaPlayer mediaPlayer;
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    //本曲是否播放完成
    private boolean isComplete;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        //接收子线程
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(totalTime>0&&currentPausePositionInSong<totalTime){
                //延时发送空消息，将操作放入子线程
                mHandler.sendEmptyMessageDelayed(1,1000);
                currentPausePositionInSong = mediaPlayer.getCurrentPosition();
                if(!isDrag) { //拖动情况下不更新进度
                    mSeekProgress.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                }
                mTvPlayTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                mTvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            }
        }
    };
    private LocalMusicBean currentMusic;
    private boolean isDrag;
    private int dragPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();
        //创建适配器
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
        checkPermission();
        //设置每一项的点击事件
        setEventListener();
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            //启动 Activity
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            updateData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    updateData();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("permission")
                                .setMessage("点击允许")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();
                    } else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("permission")
                                .setMessage("点击允许")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            checkPermission();//由于不知道是否选择了允许所以需要再次判断
        }
    }

    private void setEventListener() {
        //设置列表每一项的点击事件
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition = position;
                currentMusic = mDatas.get(position);
                playMusicInBean();
            }
        });
    }

    /**
     * 在子线程对媒体数据进行更新
     */
    private void updateData(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MediaScannerNotifier.scan(MainActivity.this,Environment
                        .getExternalStorageDirectory().getAbsolutePath(),MainActivity.this);
            }
        });
    }

    //数据库更新完成，读取本地音频数据
    @Override
    public void onComplete() {
        loadLocalMusicData();
    }


        public void playMusicInBean() {
            //根据传入对象播放音乐
            //设置底部显示的歌手名和歌曲名
            singerTv.setText(currentMusic.getSinger());
            songTv.setText(currentMusic.getSong());
            stopMusic();
            //重置多媒体播放器
            mediaPlayer.reset();
            //设置新的播放路径
            try {
                mediaPlayer.setDataSource(currentMusic.getPath());
                playMusic();
            } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void playMusic() {
        isComplete = false;
        //播放音乐函数
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            //有音乐且暂停
            if (currentPausePositionInSong == 0) {
                //从头开始
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mSeekProgress.setMax(mediaPlayer.getDuration()/1000);
                    //获得播放时长
                    totalTime = mediaPlayer.getDuration();
                    //监听是否到结尾，自动播放下一曲
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            isComplete = true;
                            playIv.setImageResource(R.mipmap.play);
                            if(currentPlayPosition<mDatas.size()-1){
                                currentPlayPosition++;
                                currentMusic = mDatas.get(currentPlayPosition);
                                playMusicInBean();
                            }else{
                                Toast.makeText(MainActivity.this,"无下一曲",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //从暂停位置开始播放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            //延时发送空消息，将操作放入子线程
            mHandler.sendEmptyMessageDelayed(1,300);
            mSeekProgress.setProgress(mediaPlayer.getCurrentPosition()/1000);
            mTvPlayTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
            mTvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            playIv.setImageResource(R.mipmap.pause);
        }
    }

    private void pauseMusic() {
        //暂停音乐函数
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //有音乐且正在播放
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.mipmap.play);
        }
    }


    private void stopMusic() {
        //停止音乐函数
        if (mediaPlayer != null) {
            //有音乐
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            mTvPlayTime.setText(formatTime(0));
            mTvTotalTime.setText(formatTime(0));
            playIv.setImageResource(R.mipmap.play);

        }
    }


    private void loadLocalMusicData() {
        //加载本地存储当中的音乐文件到集合当中
        //获取ContentResolver对象
        ContentResolver resolver = getContentResolver();
        //获取本地音乐存储的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //开始查询地址
        //获取所有在外部存储卡上的音乐文件的信息
        Cursor cursor = resolver.query(uri, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历Cursor
        int id = 0;
        assert cursor != null;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String isMusic = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
            if("1".equals(isMusic)) {
                //将一行当中的数据封装到对象当中
                LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path, isMusic);
                mDatas.add(bean);
            }
        }
        cursor.close();

        runOnUiThread(new Runnable() { //在ui线程更新数据展示
            @Override
            public void run() {
                //数据源发生变化，提示数据源更新
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void initView() {
        //初始化控件
        nextIv = findViewById(R.id.local_music_bottom_iv_next);
        playIv = findViewById(R.id.local_music_bottom_iv_play);
        lastIv = findViewById(R.id.local_music_bottom_iv_last);
        singerTv = findViewById(R.id.local_music_bottom_tv_singer);
        songTv = findViewById(R.id.local_music_bottom_tv_song);
        musicRv = findViewById(R.id.local_music_rv);
        mSeekProgress = findViewById(R.id.seek_progress);
        mTvPlayTime = findViewById(R.id.tv_play_time);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        mSeekProgress.setOnSeekBarChangeListener(this);
        nextIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music_bottom_iv_last:
                if (currentPlayPosition == 0) {
                    Toast.makeText(this, "无上一曲", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition - 1;
                currentMusic = mDatas.get(currentPlayPosition);
                playMusicInBean();
                break;
            case R.id.local_music_bottom_iv_next:
                if (currentPlayPosition == mDatas.size() - 1) {
                    Toast.makeText(this, "无下一曲", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition + 1;
                currentMusic = mDatas.get(currentPlayPosition);
                playMusicInBean();
                break;
            case R.id.local_music_bottom_iv_play:
                if (currentPlayPosition == -1) {
                    //并没有选中想要播放的音乐
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    //此时处于播放状态，需要暂停音乐
                    pauseMusic();
                } else {
                    //此时没有播放音乐，点击开始播放音乐
                    if(!isComplete) {
                        playMusic();
                    }else{
                        //将所有的Callbacks和Messages全部清除掉
                        mHandler.removeCallbacksAndMessages(null);
                        playMusicInBean();
                    }
                }
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    //更新进度条触发
    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
        dragPosition = position;
    }

    //拖动进度条开始拖动时调用
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDrag = true;
    }

    //拖动进度条进度改变时调用
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(dragPosition<=0){
            //拖动到开始
            dragPosition = 1;
        }else if(dragPosition>=mediaPlayer.getDuration()){
            //拖动到结尾
            dragPosition = mediaPlayer.getDuration()-1000;
        }
        //转化为秒，更新时间
        currentPausePositionInSong = dragPosition*1000;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        playMusic();
        isDrag = false;
    }



    /**
     * 格式化时间显示样式
     * @param time
     * @return
     */
    private String formatTime(int time){
        StringBuilder builder = new StringBuilder();
        if(time<=0){
            builder.append("00:00");
        }else{
            int m = time/(60*1000);
            if(m<10){
                builder.append("0"+m+":");
            }else{
                builder.append(m+":");
            }
            int s = (time-m*60*1000)/1000;
            if(s<10){
                builder.append("0"+s);
            }else{
                builder.append(s);
            }
        }
        return builder.toString();
    }
}