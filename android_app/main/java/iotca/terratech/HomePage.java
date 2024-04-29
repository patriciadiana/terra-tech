package iotca.terratech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import iotca.terratech.databinding.HomePageBinding;

import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class HomePage extends Fragment {

    private HomePageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExecuteScriptTask().execute();
                NavHostFragment.findNavController(HomePage.this)
                        .navigate(R.id.action_HomePage_to_ScanLoadingPage);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

                String command = "cd /home && cd barsi && cd Desktop" +
                        " && cd flower_recognition && python flower_data.py > /dev/null 2>&1";
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);

                channel.connect();
                channel.disconnect();
                session.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}