package am.project.x.activities.widgets.cameraview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.cameraview.old.CameraStateCallback;
import am.widget.cameraview.old.CameraView;

public class CameraViewActivity extends BaseActivity implements CameraStateCallback {

    private static final int PERMISSIONS_REQUEST_CAMERA = 106;
    private CameraView cvCamera;

    public static void start(Context context) {
        Intent starter = new Intent(context, CameraViewActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_cameraview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.camera_toolbar);
        cvCamera = findViewById(R.id.camera_cv_camera);
        cvCamera.addCallback(this);
    }

    @Override
    public void onPermissionDenied(CameraView cameraView) {
        // 缂哄皯鎵撳紑鐩告満鐨勬潈闄?        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    public void onOpened(CameraView cameraView) {

    }

    @Override
    public void onDisconnected(CameraView cameraView) {

    }

    @Override
    public void onError(CameraView cameraView, int error, int reason) {
        // 鐩告満鏃犳硶鎵撳紑
        Toast.makeText(this, "鎵撳紑鐩告満澶辫触", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cvCamera.open();
                }
            }
        }
    }
}

