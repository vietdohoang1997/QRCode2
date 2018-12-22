package com.example.lenovo.qrcode2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class QRcodeGenerator extends Fragment {

    private EditText etTen;
    private EditText etSoLuong;
    private EditText etTinhTrang;
    private EditText etNguoiGiu;

    private Button btnCreate;
    private ImageView imageView;
    private Button btnSave;
    private Button btnShare;
    private Button btnReset;

    private AlertDialog dialog;
    View view;
    ByteArrayOutputStream bytearrayoutputstream;
    File file;
    FileOutputStream fileoutputstream;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_generator, container, false);

    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Generator");

        etTen = (EditText) getView().findViewById(R.id.etTen);
        etSoLuong = (EditText) getView().findViewById(R.id.etSoLuong);
        etTinhTrang = (EditText) getView().findViewById(R.id.etTinhTrang);
        etNguoiGiu = (EditText) getView().findViewById(R.id.etNguoiGiu);

        btnCreate = (Button) getView().findViewById(R.id.btnCreate);
        btnSave = (Button) getView().findViewById(R.id.btnSave);
        btnShare = (Button) getView().findViewById(R.id.btnShare);
        btnReset = (Button) getView().findViewById(R.id.btnReset);

        imageView = (ImageView) getView().findViewById(R.id.imageView);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Ten = etTen.getText().toString().trim();
                String TinhTrang = etTinhTrang.getText().toString().trim();
                String NguoiGiu = etNguoiGiu.getText().toString().trim();
                String SoLuong = etSoLuong.getText().toString().trim();
                String text = "Thiet Bi    : " + Ten+ "\n"+ "So Luong  :  "+ SoLuong + "\n" + "Nguoi Giu  :  "+ NguoiGiu+ "\n"+ "Tinh Trang :" + TinhTrang;
                /* String text = etInput.getText().toString().trim();*/
                if (text != null) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        imageView.setImageBitmap(bitmap);
                    } catch (WriterException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveImageLocally(imageView);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                try {
                    File file = new File(getActivity().getCacheDir(),"QR_image.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.setType("image/png");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String saveImageLocally(ImageView iv) {
        iv.buildDrawingCache();

        Bitmap bmp = iv.getDrawingCache();

        File storageLocal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM); //context.getExternalFilesDir(null);

        Random random = new Random();
        int n = 10000;
        String path;
        n = random.nextInt(n);
        String name = "Image-" + n + ".png" ;

        File file = new File(storageLocal, name );

        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Toast.makeText(getActivity(), "Saved successfully in " + path + "/" + name , Toast.LENGTH_LONG).show();

        try{
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            scanFile(getActivity(), Uri.fromFile(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("bitmap","not found , "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("bitmap","io ex , "+e.getMessage());
        }

        return "true";
    }

    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }


}