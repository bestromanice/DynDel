package com.example.dyndel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManager;
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManagerFactory;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button dynamicFeature1Button;
    Button dynamicFeature2Button;
    ListView installedModulesListView;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Creates an instance of FakeSplitInstallManager with the app's context.
        FakeSplitInstallManager fakeSplitInstallManager =  FakeSplitInstallManagerFactory.create(
                context,
                context.getExternalFilesDir("local_testing"));

        // Tells Play Feature Delivery Library to force the next module request to
        // result in a network error.
        fakeSplitInstallManager.setShouldNetworkError(true);

        // Creates an instance of SplitInstallManager.
//        SplitInstallManager splitInstallManager = SplitInstallManagerFactory.create(context);

        // Specifies one feature module for deferred uninstall.
//        splitInstallManager.deferredUninstall(Arrays.asList("dynamicfeature1"));

        installedModulesListView = findViewById(R.id.installed_modules_listview);

        dynamicFeature1Button = findViewById(R.id.dynamic_feature_1_button);
        dynamicFeature2Button = findViewById(R.id.dynamic_feature_2_button);

        // Takes all installed modules.
        Set<String> installedModules = fakeSplitInstallManager.getInstalledModules();

        ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, Arrays.asList(installedModules.toArray()));
        installedModulesListView.setAdapter(adapter);

        dynamicFeature1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fakeSplitInstallManager.getInstalledModules().contains("dynamicfeature1")) {
                    Toast.makeText(MainActivity.this,
                            "The module is installed",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClassName("com.example.dyndel",
                            "com.example.dynamicfeature1.DynamicActivity1");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "The module is NOT installed",
                            Toast.LENGTH_SHORT).show();
                    SplitInstallRequest request = SplitInstallRequest.newBuilder()
                            .addModule("dynamicfeature1")
                            .build();

                    fakeSplitInstallManager.startInstall(request)
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer sessionId) {
                                    Intent intent = new Intent();
                                    Toast.makeText(MainActivity.this,
                                            "Module download completed",
                                            Toast.LENGTH_SHORT).show();
                                    intent.setClassName("com.example.dyndel",
                                            "com.example.dynamicfeature1.DynamicActivity1");
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(MainActivity.this,
                                            "Module download failed:\n" + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        dynamicFeature2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fakeSplitInstallManager.getInstalledModules().contains("dynamicfeature2")) {
                    Toast.makeText(MainActivity.this,
                            "The module is installed",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClassName("com.example.dyndel",
                            "com.example.dynamicfeature2.DynamicActivity2");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "The module is NOT installed",
                            Toast.LENGTH_SHORT).show();
                    SplitInstallRequest request = SplitInstallRequest.newBuilder()
                            .addModule("dynamicfeature2")
                            .build();

                    fakeSplitInstallManager.startInstall(request)
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer sessionId) {
                                    Intent intent = new Intent();
                                    Toast.makeText(MainActivity.this,
                                            "Module download completed",
                                            Toast.LENGTH_SHORT).show();
                                    intent.setClassName("com.example.dyndel",
                                            "com.example.dynamicfeature2.DynamicActivity2");
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(MainActivity.this,
                                            "Module download failed:\n" + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}