package com.moutamid.placesbeen.fragments.profile;

import static android.app.Activity.RESULT_OK;
import static com.moutamid.placesbeen.utils.Utils.toast;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.activities.login.RegistrationActivity;
import com.moutamid.placesbeen.databinding.FragmentProfileBinding;
import com.moutamid.placesbeen.utils.Constants;

public class ProfileFragment extends Fragment {

    public FragmentProfileBinding b;

    public ProfileController controller;
    public ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);

        controller = new ProfileController(this);

        controller.initArcViews(b.profileArcView);

        controller.findPercentage();

        controller.setOnClickOnSignOutBtn();

        setOnClickOnProfileImage();

        if (isAdded()) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
        }

        Constants.databaseReference()
                .child(Constants.auth().getUid())
                .child(Constants.USER_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            b.userNameProfile.setText(snapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return b.getRoot();

    }

    private void setOnClickOnProfileImage() {
        if (isAdded())
            Glide.with(requireActivity().getApplicationContext())
                    .load(Stash.getString(Constants.PROFILE_URL))
                    .apply(new RequestOptions()
                            .placeholder(R.color.grey)
                            .error(R.drawable.test)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(b.profileCircleImageView);

        b.profileCircleImageView.setOnClickListener(view -> {
            if (Constants.auth().getCurrentUser().isAnonymous()) {
                toast("You need to sign up first!");
                startActivity(new Intent(requireActivity(), RegistrationActivity.class));
                return;
            }

            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 1234);
        });

        b.editNameBtnProfile.setOnClickListener(view -> {
            if (Constants.auth().getCurrentUser().isAnonymous()) {
                toast("You need to sign up first!");
                startActivity(new Intent(requireActivity(), RegistrationActivity.class));
                return;
            }

            b.nameLayout.setVisibility(View.GONE);
            b.userNameETLayout.setVisibility(View.VISIBLE);
        });

        b.userNameEtBtn.setOnClickListener(view -> {
            if (b.userNameEtProfile.getText().toString().isEmpty())
                return;

            Stash.put(Constants.USER_NAME, b.userNameEtProfile.getText().toString());
            Constants.databaseReference()
                    .child(Constants.auth().getUid())
                    .child(Constants.USER_NAME)
                    .setValue(b.userNameEtProfile.getText().toString());

            b.nameLayout.setVisibility(View.VISIBLE);
            b.userNameETLayout.setVisibility(View.GONE);
            toast("Done");

        });

        b.resetSavedListBtnProfile.setOnClickListener(view -> {
            controller.showDeleteDialog(Constants.SAVED_LIST);
        });

        b.resetBeenListBtnProfile.setOnClickListener(view -> {
            controller.showDeleteDialog(Constants.BEEN_ITEMS_PATH);
        });

        b.resetWantToListBtnProfile.setOnClickListener(view -> {
            controller.showDeleteDialog(Constants.WANT_TO_ITEMS_PATH);
        });


    }

    String profileImageUrl = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
            progressDialog.show();

            final StorageReference filePath = storageReference
                    .child(Constants.auth().getCurrentUser().getUid() + imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    filePath.getDownloadUrl().addOnSuccessListener(photoUrl -> {
                        profileImageUrl = photoUrl.toString();
                        Constants.databaseReference()
                                .child(Constants.auth().getUid())
                                .child(Constants.PROFILE_URL)
                                .setValue(profileImageUrl)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Stash.put(Constants.PROFILE_URL, profileImageUrl);

                                        b.profileCircleImageView.setImageURI(data.getData());

                                        progressDialog.dismiss();

                                        Toast.makeText(getActivity(), "Upload done!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });

                    })).addOnFailureListener(e -> {
                progressDialog.dismiss();

                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

            });
        }
    }

    public void refreshData() {
        if (b != null) {
            controller.initArcViews(b.profileArcView);
            controller.findPercentage();
        }
    }
}
