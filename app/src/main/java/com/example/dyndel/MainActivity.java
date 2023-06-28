package com.example.dyndel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private FakeSplitInstallManager fSplitInstallManager;
    private SplitInstallManager splitInstallManager;

    Button dynamicFeature1Button;
    Button dynamicFeature2Button;
    ListView installedModulesListView;

    private String dynamicFeature1;
    private String dynamicFeature2;
    private final String DYNAMIC_FEATURE_1_SAMPLE_CLASSNAME = "com.example.dynamicfeature1.DynamicActivity1";
    private final String DYNAMIC_FEATURE_2_SAMPLE_CLASSNAME = "com.example.dynamicfeature2.DynamicActivity2";

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        dynamicFeature1  = getString(R.string.title_dynamicfeature1);
        dynamicFeature2  = getString(R.string.title_dynamicfeature2);

        // Creates an instance of FakeSplitInstallManager with the app's context.
        fSplitInstallManager =  FakeSplitInstallManagerFactory.create(
                context,
                context.getExternalFilesDir("local_testing"));

        // Tells Play Feature Delivery Library to force the next module request to
        // result in a network error.
        fSplitInstallManager.setShouldNetworkError(true);

        // Creates an instance of SplitInstallManager.
        splitInstallManager = SplitInstallManagerFactory.create(context);

        // Specifies one feature module for deferred uninstall.
//        splitInstallManager.deferredUninstall(Arrays.asList("dynamicfeature1"));

        installedModulesListView = findViewById(R.id.installed_modules_listview);

        dynamicFeature1Button = findViewById(R.id.dynamic_feature_1_button);
        dynamicFeature2Button = findViewById(R.id.dynamic_feature_2_button);

        // Takes all installed modules.
        Set<String> installedModules = splitInstallManager.getInstalledModules();

        ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, Arrays.asList(installedModules.toArray()));
        installedModulesListView.setAdapter(adapter);

        dynamicFeature1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAndLaunchModule(dynamicFeature1);

                // Takes all installed modules.
                Set<String> installedModules = splitInstallManager.getInstalledModules();

                ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, Arrays.asList(installedModules.toArray()));
                installedModulesListView.setAdapter(adapter);
            }
        });

        dynamicFeature2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAndLaunchModule(dynamicFeature2);

                // Takes all installed modules.
                Set<String> installedModules = splitInstallManager.getInstalledModules();

                ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, Arrays.asList(installedModules.toArray()));
                installedModulesListView.setAdapter(adapter);
            }
        });
    }

    private void loadAndLaunchModule(String name) {

        if (splitInstallManager.getInstalledModules().contains(name)) {
            onSuccessfullLoad(name);
            return;
        }
        else {
            Toast.makeText(MainActivity.this,
                    "The module is NOT installed",
                    Toast.LENGTH_SHORT).show();
            SplitInstallRequest request = SplitInstallRequest.newBuilder()
                    .addModule(name)
                    .build();

            splitInstallManager.startInstall(request)
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer sessionId) {
                            onSuccessfullLoad(name);
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

    private void onSuccessfullLoad(String moduleName) {

        if (moduleName == dynamicFeature1) {
            launchActivity(DYNAMIC_FEATURE_1_SAMPLE_CLASSNAME);
        }
        if (moduleName == dynamicFeature2) {
            launchActivity(DYNAMIC_FEATURE_2_SAMPLE_CLASSNAME);
        }
    }

    private void launchActivity(String className) {

        Intent intent = new Intent();
        Toast.makeText(MainActivity.this,
                "The module is installed.",
                Toast.LENGTH_SHORT).show();
        intent.setClassName("com.example.dyndel", className);
        startActivity(intent);
    }
}