package iotca.terratech;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

import iotca.terratech.databinding.ScanLoadingPageBinding;

public class ScanLoadingPage extends Fragment {

    private ScanLoadingPageBinding binding;
    private static String command_output="";
    private Handler handler;
    private static final long INITIAL_DELAY = 35000; // Initial delay before starting checking
    private static final long DELAY_INTERVAL = 2000;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ScanLoadingPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        command_output="";

        ImageView loadinggif = view.findViewById(R.id.loadinggif);
        Glide.with(this).asGif().load(R.drawable.loading).into(loadinggif);

        handler = new Handler(Looper.myLooper());
        checkScriptCompletion();

        binding.homeRedirectingLogo.setOnClickListener(v ->
                NavHostFragment.findNavController(ScanLoadingPage.this)
                        .navigate(R.id.action_ScanLoadingPage_to_HomePage)
        );

    }

    private void checkScriptCompletion() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scriptCompleted()) {
                    // Script execution completed, navigate to the next page
                    navigateToNextPage();
                } else {
                    // Script execution not completed yet, schedule the next check
                    handler.postDelayed(this, DELAY_INTERVAL);
                }
            }
        }, INITIAL_DELAY);
    }

    private boolean scriptCompleted() {
        new ExecuteScriptTask().execute();
        if(!command_output.isEmpty())
        {
            System.out.println(command_output);
            String[] values = command_output.split(" ");

            Values.getInstance().setFlower_type(values[0]);
            set_watering_routine(values[0]);

            Values.getInstance().setTemperature(Float.parseFloat(values[1]));

            Values.getInstance().setHumidity(Float.parseFloat(values[2]));

            Values.getInstance().setSoil_status(values[3]);

            return true;
        }
        return false;
    }

    private void set_watering_routine(String flower_type)
    {
        if(flower_type.equals("Rose"))
        {
            Values.getInstance().setPumps(2);
            Values.getInstance().setInterval(2);
        }
        else if(flower_type.equals("Sunflower"))
        {
            Values.getInstance().setPumps(6);
            Values.getInstance().setInterval(1);
        }
        else if(flower_type.equals("Tulip"))
        {
            Values.getInstance().setPumps(4);
            Values.getInstance().setInterval(1);
        }
        else if(flower_type.equals("Daisy"))
        {
            Values.getInstance().setPumps(5);
            Values.getInstance().setInterval(1);
        }

    }

    public static class ExecuteScriptTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                JSch jsch = new JSch();
                Session session = jsch.getSession(Values.USERNAME, Values.HOST, Values.PORT);
                session.setPassword(Values.PASSWORD);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(120000);
                session.connect();

                String command = "cd /home && cd barsi && cd Desktop && " +
                        "cd flower_recognition && cat output.txt ";
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);

                InputStream in = channel.getInputStream();

                channel.connect();

                StringBuilder output = new StringBuilder();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }

                in.close();

                command_output = output.toString();
                System.out.println("command output: " + command_output);


                channel.disconnect();
                session.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void navigateToNextPage() {
        NavHostFragment.findNavController(ScanLoadingPage.this)
                .navigate(R.id.action_ScanLoadingPage_to_ScanPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}