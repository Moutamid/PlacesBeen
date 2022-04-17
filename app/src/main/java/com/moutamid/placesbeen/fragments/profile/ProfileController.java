package com.moutamid.placesbeen.fragments.profile;

import static com.moutamid.placesbeen.utils.Utils.toast;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.fxn.stash.Stash;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.models.MainItemModel;
import com.moutamid.placesbeen.onboard.SplashActivity;
import com.moutamid.placesbeen.utils.Constants;

import java.util.ArrayList;

public class ProfileController {
    ProfileFragment fragment;
    Context context;

    public ProfileController(ProfileFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.requireContext();
    }

    public void initArcViews(DecoView arcView) {

        int a = 6;
        int b = 239;
        int c = 239;
        int d = 41001;
        int e = 1155;
        int f = 1155;
        int g = 57421;

        int total = a + b + c + d + e + f + g;

        int aa = Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS);
        int bb = Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS);
        int cc = Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS);
        int dd = Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS);
        int ee = Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS);
        int ff = Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS);
        int gg = Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS);

        int current = aa + bb + cc + dd + ee + ff + gg;

        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 235, 235, 235))
                .setRange(0, 100, 100)
                .setLineWidth(16f)
                .build());

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 55, 0, 179))
                .setRange(0, total, current)
                .setLineWidth(16f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);
    }

    public void findPercentage() {

        int a = 6;
        int b = 239;
        int c = 239;
        int d = 41001;
        int e = 1155;
        int f = 1155;
        int g = 57421;

        int total = a + b + c + d + e + f + g;

        int aa = Stash.getInt(Constants.PARAMS_Continent + Constants.FOR_CHARTS);
        int bb = Stash.getInt(Constants.PARAMS_Country + Constants.FOR_CHARTS);
        int cc = Stash.getInt(Constants.PARAMS_States + Constants.FOR_CHARTS);
        int dd = Stash.getInt(Constants.PARAMS_City + Constants.FOR_CHARTS);
        int ee = Stash.getInt(Constants.PARAMS_CulturalSites + Constants.FOR_CHARTS);
        int ff = Stash.getInt(Constants.PARAMS_NationalParks + Constants.FOR_CHARTS);
        int gg = Stash.getInt(Constants.PARAMS_Airports + Constants.FOR_CHARTS);

        int current = aa + bb + cc + dd + ee + ff + gg;

        double percentage1 = (double) current / (double) total;
        double percentage2 = percentage1 * 100;

        String s = String.format("%.1f", percentage2);

        fragment.b.finalPercentage.setText(s);
    }

    public void setOnClickOnSignOutBtn() {
        fragment.b.signOutBtn.setOnClickListener(view -> {
            Stash.clearAll();
            Constants.auth().signOut();
            Intent intent = new Intent(context, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            if (fragment.isAdded())
            fragment.requireActivity().finish();
        });
    }

    public void showDeleteDialog(String pathToDelete) {
        if (fragment.isAdded()){
            Dialog dialog = new Dialog(fragment.requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_delete);
            dialog.setCancelable(true);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.cancelBtnDialog).setOnClickListener(view -> {
                dialog.dismiss();
            });

            ProgressDialog progressDialog;
            if (fragment.isAdded()){
                progressDialog = new ProgressDialog(fragment.requireContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
            }

            dialog.findViewById(R.id.delBtnDialog).setOnClickListener(view -> {
                Stash.clear(pathToDelete);
                if (pathToDelete.equals(Constants.SAVED_LIST)) {
                    Log.d("TAG", "saveUnSaveItem: Placecontroller 137 HEHE REMOVED");
                    Constants.databaseReference().child(Constants.auth().getUid())
                            .child(Constants.SAVED_ITEMS_PATH).removeValue();
                }
                else {
                    Log.d("TAG", "saveUnSaveItem: Placecontroller 142 HEHE REMOVED");
                    Constants.databaseReference().child(Constants.auth().getUid())
                            .child(pathToDelete).removeValue();
                }

                dialog.dismiss();
                toast("Done");
                if (fragment.isAdded())
                fragment.requireActivity().recreate();
            });

            dialog.show();
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}
