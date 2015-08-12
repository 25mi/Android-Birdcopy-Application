package com.birdcopy.BirdCopyApp.Scan;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Scan.camera.CameraManager;
import com.birdcopy.BirdCopyApp.Scan.decoding.CaptureActivityHandler;
import com.birdcopy.BirdCopyApp.Scan.decoding.InactivityTimer;
import com.birdcopy.BirdCopyApp.Scan.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import com.birdcopy.BirdCopyApp.R;
/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class ScanActivity extends Activity implements Callback
{
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		
		Button mButtonBack = (Button) findViewById(R.id.capture_button_cancel);
		mButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ScanActivity.this.finish();
			}
		});

        Button mPickPicture = (Button) findViewById(R.id.capture_button_album);
        mPickPicture.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 激活系统图库，选择一张图片
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume()
    {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy()
    {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	public void handleDecode(Result result, Bitmap barcode)
    {
		inactivityTimer.onActivity();
        MyApplication.getInstance().playbeepAndsVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(ScanActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			bundle.putParcelable("bitmap", barcode);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);
		}
        ScanActivity.this.finish();
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder()
    {
		viewfinderView.drawViewfinder();

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            Bitmap bitmap=null;
            try
            {
                bitmap =MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            }
            catch (Exception e){
            }
            finally {

                if(bitmap!=null)
                {
                    String barcode=new BitmapToText(bitmap).getText();

                    if(barcode!=null)
                    {
                        MyApplication.getInstance().playbeepAndsVibrate();

                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", barcode);
                        resultIntent.putExtras(bundle);
                        this.setResult(RESULT_OK, resultIntent);

                        ScanActivity.this.finish();
                    }
                    else
                    {
                        Toast.makeText(ScanActivity.this, "没有找到二维码!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}