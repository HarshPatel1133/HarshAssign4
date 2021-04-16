package harsh.patel.n01351133;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;




public class DownloadFrag extends Fragment {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button downloadBtn;
    URL ImageUrl = null;
    Bitmap bmImg = null;
    ImageView imageView = null;
    ProgressDialog progressDialog;
    TextView text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_download, container, false);
        downloadBtn = root.findViewById(R.id.HarshDownloadBtn);
        imageView = root.findViewById(R.id.HarshDownloadedImage);
        retreiveImageFromDevice();
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(retreiveImageFromDevice()){
                    //
                }
                else {
                    startDownload();
                }
            }
        });
        return root;
    }

//    public void checkExternalStoragePermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // request permission (see result in onRequestPermissionsResult() method)
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{WRITE_EXTERNAL_STORAGE},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            Toast.makeText(getActivity(), "not granted", Toast.LENGTH_SHORT).show();
//        } else {
//            // permission already granted run sms send
//            Toast.makeText(getActivity(), "granted", Toast.LENGTH_SHORT).show();
//            startDownload();
//        }
//    }

    public void startDownload() {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask();
        asyncTask.execute(getString(R.string.image_url));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class DownloadAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.downloading));
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            int count;
            try {
                ImageUrl = new URL(strings[0]);
//                HttpURLConnection conn = (HttpURLConnection) ImageUrl
//                        .openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//                is = conn.getInputStream();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                InputStream input = conn.getInputStream();
//                options.inPreferredConfig = Bitmap.Config.RGB_565;
//                bmImg = BitmapFactory.decodeStream(is, null, options);
//                bmImg = BitmapFactory.decodeStream(input);

                URLConnection connection = ImageUrl.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(ImageUrl.openStream());
                FileOutputStream output = null;
                ContextWrapper wrapper = new ContextWrapper(getContext());
                String folderName = getString(R.string.folder_name);
                File dir = wrapper.getDir(folderName, Context.MODE_PRIVATE);
                File saveImage = new File(dir, getString(R.string.file_name));
                int lenghtOfFile = connection.getContentLength();
                try {
                    output = new FileOutputStream(saveImage);
                    //bmImg.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    byte[] data = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int) (total * 100 / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                for(int i = 0; i < 100; i++){
//                    publishProgress(i);
//                    try{
//                        Thread.sleep(10);
//                    }
//                    catch(InterruptedException e){
//                    }
//                }
                return bmImg;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPostExecute(bitmap);
            if (imageView != null) {
                progressDialog.dismiss();
                //imageView.setImageBitmap(bitmap);
                //saveImageToDevice(bitmap);
                retreiveImageFromDevice();
            } else {
                progressDialog.show();
            }
        }

    }
    public boolean retreiveImageFromDevice() {
        Bitmap bitmap;
        boolean isSaved;

        ContextWrapper wrapper = new ContextWrapper(getContext());
        String folderName = getString(R.string.folder_name);
        File dir = wrapper.getDir(folderName, Context.MODE_PRIVATE);
        String path = dir.getAbsolutePath();
        try {
            File file = new File(path, getString(R.string.file_name));
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imageView.setImageBitmap(bitmap);
            isSaved = true;
        } catch (FileNotFoundException e) {
            isSaved = false;
        }
        return isSaved;
    }
}