package com.mio.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mio.base.databinding.ActivityYinshipinBinding;
import com.mio.basic.BaseActivity;
import com.mio.utils.ToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.FileProvider;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * 学习音视频
 * 参考文章：https://blog.csdn.net/u011418943/article/details/107224028
 *
 */
public class YinShipinActivity extends BaseActivity<ActivityYinshipinBinding> {
    private static final String TAG = "YinShipinActivity";
    private static final int AUDIO_RATE = 44100; // 采样率
    private static final String PATH = "data/data/com.mio.base"
            + "/videodemo";
    private int minBufferSize;
    private AudioRecord mRecord;
    private File wavFile;
    private File pcmFile;
    private MediaPlayer mediaPlayer;

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    @Override
    protected void initView() {
        // 获取权限 默认就获取了
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.RECORD_AUDIO
                )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        initRecord();
                    }
                })
                .start();


        mDataBinding.btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 开始录制
                        startRecord();
                        mDataBinding.btnRecord.setText("录制中...");
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 停止录制
                        mDataBinding.btnRecord.setText("录制完毕...");

                        stopRecord();

                        mDataBinding.getRoot().postDelayed(() ->
                                mDataBinding.btnRecord.setText("录制"), 1_000);
                        break;
                }


                return true;
            }
        });

        mDataBinding.btnPlay.setOnClickListener(v -> startPlay());
        mDataBinding.btnPlayPcm.setOnClickListener(v -> startPlayPcm());
        mDataBinding.btnPlay.setOnClickListener(v -> startPlayPcmStatic());
    }

    private void initRecord() {
        /**
         * 获取最小 buffer 大小,即一帧的buffer
         * 采样率为 44100，双声道，采样位数为 16bit
         * 双声道
         * 16位采样
         */
        minBufferSize = AudioRecord.getMinBufferSize(
                AUDIO_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        //使用 AudioRecord 去录音
        mRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                AUDIO_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
        );
    }

    private void startRecord() {
        initRecord();
        if (mAudioThread == null) mAudioThread = new AudioThread();
        mAudioThread.start();
    }

    private void stopRecord() {
        if (mAudioThread != null) {
            mAudioThread.done();
            mAudioThread = null;
        }
    }

    private AudioThread mAudioThread;

    class AudioThread extends Thread {

        private boolean isDone;

        private RandomAccessFile wavRaf;

        @Override
        public synchronized void start() {
            super.start();
            isDone = false;
        }

        @Override
        public void run() {
            super.run();
            FileOutputStream fos = null;
            FileOutputStream wavFos = null;

            try {
                //没有先创建文件夹
                File dir = new File(PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //创建 pcm 文件
                pcmFile = new File(PATH, "test.pcm");
                wavFile = new File(PATH, "test.wav");
//                Log.d(TAG, "run: 创建文件 : " + pcmFile.getAbsolutePath()
//                        + ", size : " + pcmFile.length());

                // 加一个 每次录制 就把之前录制的清了
                if (pcmFile.length() > 0) {
                    pcmFile.delete();
                    pcmFile = new File(PATH, "test.pcm");
                }
                if (wavFile.length() > 0) {
                    wavFile.delete();
                    wavFile = new File(PATH, "test.wav");
                }

                fos = new FileOutputStream(pcmFile);
                wavFos = new FileOutputStream(wavFile);

                //先写头部，刚才是，我们并不知道 pcm 文件的大小
                byte[] headers = generateWavFileHeader(0,
                        AUDIO_RATE,
                        mRecord.getChannelCount());
                wavFos.write(headers, 0, headers.length);

                //开始录制
                mRecord.startRecording();
                byte[] buffer = new byte[minBufferSize];
                while (!isDone) {
                    //读取数据
                    int read = mRecord.read(buffer, 0, buffer.length);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        //写 pcm 数据
                        fos.write(buffer, 0, read);
                        wavFos.write(buffer, 0, read);

                        Log.d(TAG, "run: 写入了数据，长度:" + buffer.length);
                    }

                }
                //录制结束
                mRecord.stop();
                mRecord.release();
                fos.flush();
                wavFos.flush();

                //修改头部的 pcm文件 大小
                wavRaf = new RandomAccessFile(wavFile, "rw");
                byte[] header = generateWavFileHeader(pcmFile.length(), AUDIO_RATE, mRecord.getChannelCount());
                wavRaf.seek(0);
                wavRaf.write(header);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (wavFos != null) {
                    try {
                        wavFos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mRecord.release();
            }
        }

        public void done() {
            isDone = true;
        }


    }

    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的
     *
     * @param pcmAudioByteCount 不包括header的音频数据总长度
     * @param longSampleRate    采样率,也就是录制时使用的频率
     * @param channels          audioRecord的频道数量
     */
    private byte[] generateWavFileHeader(long pcmAudioByteCount, long longSampleRate, int channels) {
        long totalDataLen = pcmAudioByteCount + 36; // 不包含前8个字节的WAV文件总长度
        long byteRate = longSampleRate * 2 * channels;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (2 * channels);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (pcmAudioByteCount & 0xff);
        header[41] = (byte) ((pcmAudioByteCount >> 8) & 0xff);
        header[42] = (byte) ((pcmAudioByteCount >> 16) & 0xff);
        header[43] = (byte) ((pcmAudioByteCount >> 24) & 0xff);
        return header;
    }

    private void startPlay() {
        wavFile = new File(PATH, "test.wav");
        if (!wavFile.exists()) {
            ToastUtils.showLongToast(mContext, "请检查是否录制过...");
            return;
        }

        openFile(wavFile.getAbsolutePath());


    }

    private void startPlayPcm() {
        pcmFile = new File(PATH, "test.pcm");
        if (!wavFile.exists()) {
            ToastUtils.showLongToast(mContext, "请检查是否录制过...");
            return;
        }


    }

    private void startPlayPcmStatic() {
        pcmFile = new File(PATH, "test.pcm");
        if (!wavFile.exists()) {
            ToastUtils.showLongToast(mContext, "请检查是否录制过...");
            return;
        }

        try {
            InputStream is = new FileInputStream(pcmFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            //创建一个数组
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) > 0) {
                //把数据存到ByteArrayOutputStream中
                baos.write(buffer, 0, len);
            }
            //拿到音频数据
            byte[] bytes = baos.toByteArray();

            //双声道
            int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
            /**
             * 设置音频信息属性
             * 1.设置支持多媒体属性，比如audio，video
             * 2.设置音频格式，比如 music
             */
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            /**
             * 设置音频哥特式
             * 1. 设置采样率
             * 2. 设置采样位数
             * 3. 设置声道
             */
            AudioFormat format = new AudioFormat.Builder()
                    .setSampleRate(AUDIO_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(channelConfig)
                    .build();
            //注意 bufferSizeInBytes 使用音频的大小
            AudioTrack audioTrack = new AudioTrack(
                    attributes,
                    format,
                    bytes.length,
                    AudioTrack.MODE_STATIC, //设置为静态模式
                    AudioManager.AUDIO_SESSION_ID_GENERATE //音频识别id
            );
            //一次性写入
            audioTrack.write(bytes, 0, bytes.length);
            //开始播放
            audioTrack.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(String filePath) {
        // 创建一个Intent对象，Action设置为ACTION_VIEW，Data设置为文件的URI
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = Uri.parse(filePath);
        intent.setDataAndType(fileUri, "*/*"); // 设置文件类型为通用

        // 如果你知道文件类型，可以根据文件类型设置Data和Type
        // 例如：如果是PDF文件，可以使用以下代码
        // intent.setDataAndType(fileUri, "application/pdf");

        // 尝试启动文件管理器应用
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_yinshipin;
    }
}
