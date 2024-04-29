package iotca.terratech;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

import iotca.terratech.databinding.WateringPageBinding;

public class WateringPage extends Fragment {

    private WateringPageBinding binding;
    private static final long INITIAL_DELAY = 1000; // Initial delay before starting checking
    private static final long DELAY_INTERVAL = 2000; // Delay between checking attempts

    private Handler handler;
    private static String command_output = "";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = WateringPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView loadinggif = view.findViewById(R.id.loadinggif);
        Glide.with(this).asGif().load(R.drawable.loading).into(loadinggif);

        command_output="";
        handler = new Handler(Looper.myLooper());
        checkScriptCompletion();

        binding.homeRedirectingLogo.setOnClickListener(v ->
                NavHostFragment.findNavController(WateringPage.this)
                        .navigate(R.id.action_WateringPage_to_HomePage)
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
            if(command_output.contains("finish.txt"))
            {
                System.out.println("bambam");
                return true;
            }
        return false;
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

                String command = "cd /home && cd barsi && cd Desktop && ls "; // Change this to the path of your script
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

                channel.disconnect();
                session.disconnect();

                command_output = output.toString();
                System.out.println("command output: " + command_output);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void navigateToNextPage() {
        NavHostFragment.findNavController(WateringPage.this)
                .navigate(R.id.action_WateringPage_to_WateringFinishedPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}