package com.medialink.workmanager1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    private TextView tvStatus;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        final WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setConstraints(constraints)
                .build();

        final PeriodicWorkRequest mPeriodic = new PeriodicWorkRequest.Builder(NotificationWorker.class, 5, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWorkManager.enqueue(mPeriodic);
                mWorkManager.enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, mPeriodic);
            }
        });

        mWorkManager.getWorkInfoByIdLiveData(mPeriodic.getId())
                .observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    tvStatus.append(state.toString() + "\n");
                }
            }
        });
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_main);
        btnSend = findViewById(R.id.btn_main);
    }
}
