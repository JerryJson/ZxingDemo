package com.example.jiezhang.zxingdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiezhang.zxingdemo.activity.CaptureActivity;
import com.example.jiezhang.zxingdemo.encoding.EncodingUtils;

import java.io.File;


public class MainActivity extends Activity {

    private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;
    private CheckBox mCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
        mCheckBox = (CheckBox) findViewById(R.id.logo);

        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String contentString = qrStrEditText.getText().toString();
                if (!contentString.equals("")) {
                    final String filePath = getFileRoot(MainActivity.this) + File.separator
                            + "qr_" + System.currentTimeMillis() + ".jpg";

                    //如果生成的二维码图片较大，可能会在生成和保存图片的过程中产生阻塞，所以放在子线程中进行
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                            final Bitmap qr = EncodingUtils.createQRCode(contentString, 350, 350,
                                    mCheckBox.isChecked() ?
                                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) :
                                            null, filePath);
                            if (qr != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        qrImgImageView.setImageBitmap(qr);
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            resultTextView.setText(scanResult);
        }
    }

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }
}