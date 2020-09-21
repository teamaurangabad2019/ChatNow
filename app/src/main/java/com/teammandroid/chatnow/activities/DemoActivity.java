package com.teammandroid.chatnow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.models.firebase.FirebaseUserModel;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_username;
    TextView tv_email;
    ImageView iv_pic;
    Button btn_signOut;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        bindView();

        btnListener();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUserModel user=dataSnapshot.getValue(FirebaseUserModel.class);
                tv_username.setText("Hello,"+user.getUsername());
                tv_email.setText("Your emailid is : "+user.getEmail());
                Picasso.get().load(user.getImageUrl()).into(iv_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void btnListener() {
        btn_signOut.setOnClickListener(this);
    }


    private void bindView() {

        tv_username=findViewById(R.id.tv_username);
        tv_email=findViewById(R.id.tv_email);
        iv_pic=findViewById(R.id.iv_pic);

        btn_signOut=findViewById(R.id.btn_signOut);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            default:
                break;
            case R.id.btn_signOut:

                FirebaseAuth.getInstance().signOut();

                gotoMainActivity();
                break;
        }
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(DemoActivity.this, OTPLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
