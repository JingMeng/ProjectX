package am.project.x.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.storage.StorageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.project.support.compat.AMStorageManagerCompat;

/**
 * 鏂囦欢瀛樺偍宸ュ叿绫? * Created by Alex on 2016/10/10.
 */
public class FileUtils {

    /**
     * 閫氳繃璺緞鑾峰彇鏂囦欢鍚?     *
     * @param path 璺緞
     * @return 鏂囦欢鍚?     */
    public static String getFileName(String path) {
        if (path == null)
            return null;
        if (path.contains("/"))
            return path.substring(path.lastIndexOf("/") + 1, path.length());
        return path;
    }

    /**
     * 閫氳繃璺緞鑾峰彇鏂囦欢鎵€鍦ㄦ枃浠跺す璺緞
     *
     * @param path 璺緞
     * @return 鏂囦欢鎵€鍦ㄦ枃浠跺す璺緞
     */
    public static String getFileDir(String path) {
        if (path == null)
            return null;
        if (path.contains("/"))
            return path.substring(0, path.lastIndexOf("/"));
        return path;
    }

    /**
     * 鑾峰彇璺緞鏍圭洰褰?     *
     * @param manager 瀛樺偍璁惧绠＄悊鍣?     * @param path    璺緞
     * @return 鏍圭洰褰?     */
    public static String getPathStorage(StorageManager manager, String path) {
        if (StringUtils.isNullOrEmpty(path))
            return null;
        List<AMStorageManagerCompat.StorageVolumeImpl> volumes =
                AMStorageManagerCompat.getEmulatedStorageVolumes(manager);
        if (volumes.size() < 1)
            return null;
        String storage = null;
        for (AMStorageManagerCompat.StorageVolumeImpl volume : volumes) {
            final String storagePath = volume.getPath();
            if (path.startsWith(storagePath)) {
                storage = storagePath;
                break;
            }
        }
        return storage;
    }

    /**
     * 澶嶅埗鏂囦欢
     *
     * @param source      婧愭枃浠?     * @param destination 鐩爣鏂囦欢
     * @return 鏄惁鎴愬姛
     */
    public static boolean copyFile(File source, File destination) {
        if (source == null || !source.exists() || !source.canRead())
            return false;
        if (destination == null || !destination.exists() || !destination.canWrite())
            return false;
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) != -1) {
                if (count == 0) {
                    count = input.read();
                    if (count < 0)
                        break;
                    output.write(count);
                    continue;
                }
                output.write(buffer, 0, count);
            }
            return true;
        } catch (Exception e) {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                return false;
            } catch (IOException ioe) {
                return false;
            }
        }
    }

    /**
     * 鍒犻櫎鏂囦欢
     *
     * @param source 婧愭枃浠?     * @return 鏄惁鎴愬姛
     */
    public static boolean deleteFile(File source) {
        if (source == null || !source.exists())
            return true;
        if (source.isFile())
            return source.delete();
        if (source.isDirectory()) {
            File[] childFile = source.listFiles();
            if (childFile == null || childFile.length == 0) {
                return source.delete();
            }
            for (File child : childFile) {
                deleteFile(child);
            }
            return source.delete();
        }
        return source.delete();
    }


    /**
     * 绉诲姩鏂囦欢骞堕噸鍛藉悕
     *
     * @param file 鏂囦欢
     * @param dir  鐩綍
     * @param name 鍚嶅瓧
     * @return 鏄惁鎴愬姛
     */
    public static boolean moveFile(File file, File dir, String name) {
        return !(file == null || !file.exists() || !file.canWrite()) &&
                !(dir == null || !dir.exists() || !dir.canWrite() || !dir.isDirectory()) &&
                file.renameTo(new File(dir, name));
    }

    /**
     * 閲嶅懡鍚嶆枃浠?     *
     * @param file 鏂囦欢
     * @param name 鏂囦欢鍚?     * @return 鏄惁鎴愬姛
     */
    public static boolean renameFile(File file, String name) {
        if (file == null || !file.exists() || !file.canWrite())
            return false;
        final File dest = new File(getFileDir(file.getPath()), name);
        return !dest.exists() && file.renameTo(dest);
    }

    /**
     * 瀛樺偍鍥剧墖鍒版枃浠?     *
     * @param file 鏂囦欢
     * @param crop 瑁佸壀鍥剧墖
     * @return 鏄惁鎴愬姛
     */
    public static boolean saveImage(File file, Bitmap crop) {
        FileOutputStream output;
        try {
            output = new FileOutputStream(file);
            crop.compress(Bitmap.CompressFormat.JPEG, 100, output);// TODO
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 鍒ゆ柇鏄惁鍏峰鏂囦欢鏉冮檺
     *
     * @param context Context
     * @param paths   璺緞
     * @return 鏄惁鍏峰
     */
    public static boolean hasFilePermission(Context context, String... paths) {
        if (Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            return true;
        File[] fileDirs = ContextCompat.getExternalFilesDirs(context, null);
        final ArrayList<File> appDirs = new ArrayList<>();
        for (File dir : fileDirs) {
            appDirs.add(dir.getParentFile());
        }
        for (String path : paths) {
            if (StringUtils.isNullOrEmpty(path))
                continue;
            for (File app : appDirs) {
                if (!path.startsWith(app.getPath())) {
                    return false;
                }
            }
        }
        return true;
    }
}

