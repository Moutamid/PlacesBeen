package com.moutamid.placesbeen.activities.saved;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.placesbeen.utils.Constants.GET_COUNTRY_FLAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fxn.stash.Stash;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.utils.Constants;

import java.io.IOException;
import java.io.InputStream;

public class SavedListController {

    SavedListsActivity activity;
    Context context;

    public SavedListController(SavedListsActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public void checkBeenStatus(ImageView flag, String title) {
        // IF USER BEEN
        if (Stash.getBoolean(title + activity.CURRENT_PATH, false)) {
            setUnlocked(flag);
        }
    }

    public void setLocked(ImageView v) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(128);   // 128 = 0.5
    }

    public void setUnlocked(ImageView v) {
        v.setColorFilter(null);
        v.setImageAlpha(255);
    }

    public void loadFlagOnImage(MainItemModel model, ImageView flagImg) {
        String desc = model.desc;
        String title = model.title;
        Log.d("FISH", "loadFlagOnImage: desc: " + desc + " title: " + title);
        if (desc.equals(Constants.NULL) || desc.isEmpty()) {
            Log.d("FISH", "loadFlagOnImage: desc is null. Title: " + title);
            // DOWNLOAD FLAG OF TITLE
            with(activity.getApplicationContext())
                    .asBitmap()
                    .load(GET_COUNTRY_FLAG(title))
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            new Handler().post(() -> {
                                String[] plitted = title.split(",");
                                if (plitted.length == 0)
                                    return;

                                String finall = plitted[0].trim();

                                with(activity.getApplicationContext())
                                        .asBitmap()
                                        .load(GET_COUNTRY_FLAG(finall))
                                        .addListener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                new Handler().post(() -> {
                                                    String[] plitted = title.split(" ");
                                                    if (plitted.length == 0)
                                                        return;

                                                    String finall = plitted[0].trim();

                                                    with(activity.getApplicationContext())
                                                            .asBitmap()
                                                            .load(GET_COUNTRY_FLAG(finall))
                                                            .apply(new RequestOptions()
                                                                    .placeholder(R.color.lighterGrey)
                                                                    .error(R.drawable.neutral_flag)
                                                            )
                                                            .diskCacheStrategy(DATA)
                                                            .into(flagImg);

                                                });
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .apply(new RequestOptions()
                                                .placeholder(R.color.lighterGrey)
                                                .error(R.color.lighterGrey)
                                        )
                                        .diskCacheStrategy(DATA)
                                        .into(flagImg);

                            });

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .apply(new RequestOptions()
                            .placeholder(R.color.lighterGrey)
                            .error(R.color.lighterGrey)
                    )
                    .diskCacheStrategy(DATA)
                    .into(flagImg);

        } else {
            with(activity.getApplicationContext())
                    .asBitmap()
                    .load(GET_COUNTRY_FLAG(desc))
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            // TRY WITH A SINGLE PART OF THE DESC
                            new Handler().post(() -> {
                                // EXTRACTING THE COUNTRY CODE
                                String[] splitted = desc.split(",");
                                if (splitted.length > 1) {
                                    String finall = splitted[1].trim();
                                    String finalfinal = finall.substring(0, 2);

                                    with(activity.getApplicationContext())
                                            .asBitmap()
                                            .load(GET_COUNTRY_FLAG(finalfinal))
                                            .apply(new RequestOptions()
                                                    .placeholder(R.color.lighterGrey)
                                                    .error(R.drawable.neutral_flag)
                                            )
                                            .diskCacheStrategy(DATA)
                                            .into(flagImg);
                                }
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_outline_info_24)
                            .error(R.drawable.ic_outline_info_24)
                    )
                    .diskCacheStrategy(DATA)
                    .into(flagImg);
        }
    }

    public String loadJSONFromAsset() {

        String json = null;
        try {
            InputStream is = context.getAssets().open("countries_to_cities.json");
//            InputStream is = context.getAssets().open("worldcities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.d("TAG", "loadJSONFromAsset: error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
