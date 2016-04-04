package com.fallenritemonk.wakebot.dismisshandler.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.dismisshandler.DismissHandler;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.util.Random;

public class QRHandler extends Fragment {
    private static final String LOG_TAG = "QRHandlerFragment";

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Handler handler;
    private boolean dismissed;

    public QRHandler() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrhandler, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        final SurfaceView cameraView = (SurfaceView) view.findViewById(R.id.cameraView);

        dismissed = false;

        final String dismissCode = generateRandomString();

        Bitmap imageBitmap = generateQRCode(dismissCode);
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }

        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    ((DismissHandler) getActivity()).dismiss();
                }
                return false;
            }
        };

        handler = new Handler(callback);

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d(LOG_TAG, "Missing camera permission");
                        // TODO: implement requesting permission
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    Log.d(LOG_TAG, "cameraSouce.start exception");
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).displayValue.equals(dismissCode) && !dismissed) {
                        dismissed = true;
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        });

        return view;
    }

    @Nullable
    private Bitmap generateQRCode(String dismissCode) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bm = null;
        Bitmap imageBitmap = null;
        try {
            bm = writer.encode(dismissCode, BarcodeFormat.QR_CODE, 150, 150);
            imageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 150; i++) {
                for (int j = 0; j < 150; j++) {
                    imageBitmap.setPixel(149 - i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }

    public String generateRandomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < 20; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
