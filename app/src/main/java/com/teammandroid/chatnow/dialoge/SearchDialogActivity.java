package com.teammandroid.chatnow.dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.android.material.snackbar.Snackbar;
import com.teammandroid.chatnow.R;
import com.teammandroid.chatnow.utils.Utility;

import java.util.ArrayList;

public class SearchDialogActivity extends Dialog implements View.OnClickListener {

    TextView b_ok;
    TextView b_cancel;

    TextView tv_basic,tv_advanced,  tv_other;
    RadioGroup rb_interest;
    Spinner spin_distance;

    OnMyDialogResult mDialogResult;

    int selectedPosition = 0;
    int selectedSearchType = 0; // 0 for basic , 1 = advanced, 2 = other
    Bundle bundle = new Bundle();

    Activity context;
   // private ProgressDialog dialog;
    private int interestType = 2;

    ArrayList<Integer> arraySpinner = new ArrayList<>();
    private Integer selectedDistance = 30;

    public SearchDialogActivity(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);

        bindView();
        initListener();
        GetUserNearMe();
    }
    private void initListener() {
       // b_ok.setOnClickListener(this);
        b_ok.setOnClickListener(new OKListener());
        b_cancel.setOnClickListener(this);
        tv_basic.setOnClickListener(this);
        tv_advanced.setOnClickListener(this);
        tv_other.setOnClickListener(this);

        rb_interest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_male:
                        interestType = 0;
                        break;

                    case R.id.rb_female:
                        interestType = 1;
                        break;

                    case R.id.rb_any:
                        interestType = 2;
                        break;
                }
            }
        });

        spin_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistance = arraySpinner.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void bindView() {
        b_ok = findViewById(R.id.b_ok);
        b_cancel = findViewById(R.id.b_cancel);
        tv_basic = findViewById(R.id.tv_basic);
        tv_advanced = findViewById(R.id.tv_advanced);
        tv_other = findViewById(R.id.tv_other);
        rb_interest = findViewById(R.id.rb_interest);
        spin_distance = findViewById(R.id.spin_distance);

        arraySpinner.add(30);
        arraySpinner.add(50);
        arraySpinner.add(100);
        arraySpinner.add(200);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(context,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_distance.setAdapter(adapter);
    }

    private void GetUserNearMe() {
        try {
            if (Utility.isNetworkAvailable(context)) {



/*
                TrainerService.getInstance(context).GetApprovedTrainerDetails(new ApiStatusCallBack<ArrayList<TrainerModel>>() {
                    @Override
                    public void onSuccess(ArrayList<TrainerModel> arraylist) {
                        //  lyt_progress_employees.setVisibility(View.GONE);
                        dialog.dismiss();

                    }
                    @Override
                    public void onError(ANError anError) {
                        //  lyt_progress_employees.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(context, "Network:" + anError.getMessage(), Snackbar.LENGTH_LONG);
                    }
                    @Override
                    public void onUnknownError(Exception e) {
                        //lyt_progress_employees.setVisibility(View.GONE);
                        dialog.dismiss();
                        Utility.showErrorMessage(context, e.getMessage(), Snackbar.LENGTH_LONG);
                    }
                });
*/
            } else {
                Utility.showErrorMessage(context, "Could not connect to the internet", Snackbar.LENGTH_LONG);
            }
        } catch (Exception ex) {
            Utility.showErrorMessage(context, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.on_clicked));

        switch (v.getId()){
            case R.id.b_cancel:
                dismiss();
                break;

            case R.id.tv_basic:
                tv_basic.setBackgroundColor(getContext().getResources().getColor(R.color.grey_5));
                tv_basic.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

                tv_advanced.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_advanced.setTextColor(getContext().getResources().getColor(R.color.colorWhite));

                tv_other.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_other.setTextColor(getContext().getResources().getColor(R.color.colorWhite));

                selectedSearchType = 0;
                break;

            case R.id.tv_advanced:

                tv_advanced.setBackgroundColor(getContext().getResources().getColor(R.color.grey_5));
                tv_advanced.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

                tv_basic.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_basic.setTextColor(getContext().getResources().getColor(R.color.colorWhite));

                tv_other.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_other.setTextColor(getContext().getResources().getColor(R.color.colorWhite));

                selectedSearchType = 1;

                break;


            case R.id.tv_other:

                tv_other.setBackgroundColor(getContext().getResources().getColor(R.color.grey_5));
                tv_other.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

                tv_basic.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_basic.setTextColor(getContext().getResources().getColor(R.color.colorWhite));

                tv_advanced.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                tv_advanced.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
                selectedSearchType = 2;
                break;
        }
    }

    private class OKListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if( mDialogResult != null ){
                mDialogResult.finish(selectedDistance, interestType);
            }
            SearchDialogActivity.this.dismiss();
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(Integer selectedDistance, int interestType);
    }
}
