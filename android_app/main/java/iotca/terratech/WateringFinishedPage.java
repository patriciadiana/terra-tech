package iotca.terratech;

import android.os.AsyncTask;
import android.os.Bundle;
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

import iotca.terratech.databinding.WateringFinishedPageBinding;

public class WateringFinishedPage extends Fragment {

    private WateringFinishedPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = WateringFinishedPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new ExecuteScriptTask().execute();

        binding.homeRedirectingLogo.setOnClickListener(v ->
                NavHostFragment.findNavController(WateringFinishedPage.this)
                        .navigate(R.id.action_WateringFinishedPage_to_HomePage)
        );
        binding.scanButton.setOnClickListener(v ->
                NavHostFragment.findNavController(WateringFinishedPage.this)
                        .navigate(R.id.action_WateringFinishedPage_to_HomePage)
        );
    }

    public static class ExecuteScriptTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(Values.USERNAME, Values.HOST, Values.PORT);
                session.setPassword(Values.PASSWORD);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(120000);
                session.connect();

                String command = "cd /home && cd barsi && cd Desktop " +
                        "&& rm finish.txt && cd flower_recognition && rm output.txt";
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);


                // Execute the command
                channel.connect();
                channel.disconnect();
                session.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}