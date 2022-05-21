package com.example.inai.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class NetworkImage extends AsyncTask<String, Void, Bitmap> {
    private String TAG = "NETWORK IMAGE";
    private ImageView bmImage;
    private Integer imageHeight;
    private Integer imageWidth;
    private NetworkImageCallback callback;

    private NetworkImage(NetworkImageBuilder netWorkImageBuilder) {
        this.bmImage = netWorkImageBuilder.bmImage;
        this.imageWidth = netWorkImageBuilder.imageWidth;
        this.imageHeight = netWorkImageBuilder.imageHeight;
        this.callback = netWorkImageBuilder.callback;
    }

    public static class NetworkImageBuilder {
        private ImageView bmImage;
        private Integer imageHeight;
        private Integer imageWidth;
        private NetworkImageCallback callback;

        public NetworkImageBuilder() {}

        public NetworkImageBuilder setImageView(ImageView bmImage) {
            this.bmImage = bmImage;
            return this;
        }

        public NetworkImageBuilder setDimensions(int imageHeight, int imageWidth) {
            this.imageHeight = imageHeight;
            this.imageWidth = imageWidth;
            return this;
        }

        public NetworkImageBuilder setCallback(NetworkImageCallback callback) {
            this.callback = callback;
            return this;
        }

        public NetworkImage build() {
            return new NetworkImage(this);
        }
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (imageWidth == null || imageHeight == null) {
            bmImage.setImageBitmap(result);
        } else {
            if (result != null) {
                Bitmap scaledImage = Bitmap.createScaledBitmap(result, imageWidth, imageHeight, false);
                bmImage.setImageBitmap(scaledImage);
            }
        }
        if (callback != null) callback.callback(bmImage);
    }
}
