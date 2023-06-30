package my.awesome.dyndel;

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
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class MainActivity extends BaseSplitActivity {

    private SplitInstallManager splitInstallManager;
//    private FakeSplitInstallManager splitInstallManager;

    private Button dynamicFeature1Button;
    private Button dynamicFeature2Button;
    private ListView installedModulesListView;

    private String dynamicFeature1;
    private String dynamicFeature2;
    private final String DYNAMIC_FEATURE_1_SAMPLE_CLASSNAME = "my.awesome.dynamicfeature1.DynamicActivity1";
    private final String DYNAMIC_FEATURE_2_SAMPLE_CLASSNAME = "my.awesome.dynamicfeature2.DynamicActivity2";
    private final static String FILE_NAME = "exceptionText.txt";
    public static String PACKAGE_NAME;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        PACKAGE_NAME = getApplicationContext().getPackageName();

        dynamicFeature1  = getString(R.string.title_dynamicfeature1);
        dynamicFeature2  = getString(R.string.title_dynamicfeature2);

        // Creates an instance of FakeSplitInstallManager with the app's context.
//        splitInstallManager =  FakeSplitInstallManagerFactory.create(
//                context,
//                context.getExternalFilesDir("local_testing"));

        // Tells Play Feature Delivery Library to force the next module request to
        // result in a network error.
//        splitInstallManager.setShouldNetworkError(true);

        // Creates an instance of SplitInstallManager.
        splitInstallManager = SplitInstallManagerFactory.create(context);

        // Initializes a variable to later track the session ID for a given request.
        int mySessionId = 0;

        // Creates a listener for request status updates.
        SplitInstallStateUpdatedListener listener = state -> {
            if (state.sessionId() == mySessionId) {
                // Read the status of the request to handle the state update.
            }
        };

        // Registers the listener.
        splitInstallManager.registerListener(listener);

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
                            FileOutputStream fos = null;
                            try {
                                String exceptionText = e.getMessage().toString();
                                exceptionText += "\n";

                                fos = openFileOutput(FILE_NAME, MODE_APPEND);
                                fos.write(exceptionText.getBytes());
                                Toast.makeText(MainActivity.this,
                                        "Log in exceptionText.txt",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (IOException ex) {
                                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            finally {
                                try {
                                    if (fos != null)
                                        fos.close();
                                }
                                catch (IOException ex) {

                                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
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
                "The module is installed. Launching...",
                Toast.LENGTH_SHORT).show();
        intent.setClassName(PACKAGE_NAME, className);
        startActivity(intent);
    }
}