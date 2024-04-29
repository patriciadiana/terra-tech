package iotca.terratech;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.w3c.dom.Text;

import iotca.terratech.databinding.ScanPageBinding;

public class ScanPage extends Fragment {

    private ScanPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = ScanPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTextFields();

        binding.homeRedirectingLogo.setOnClickListener(v ->
                NavHostFragment.findNavController(ScanPage.this)
                        .navigate(R.id.action_ScanPage_to_HomePage)
        );

        binding.wateringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExecuteScriptTask().execute();
                NavHostFragment.findNavController(ScanPage.this)
                        .navigate(R.id.action_ScanPage_to_WateringPage);
            }
        });
        binding.editPumps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog
                TextView dynamicTextView = getView().findViewById(R.id.number_of_pumps);
                showDialog("", dynamicTextView, " pumps", R.layout.pumps_layout);
            }
        });
        binding.editInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog
                TextView dynamicTextView = getView().findViewById(R.id.time_interval);
                showDialog("each pump at ", dynamicTextView, " seconds", R.layout.interval_layout);
            }
        });
    }

    private void setTextFields()
    {
        TextView dynamicTextView = getView().findViewById(R.id.number_of_pumps);
        String text = Values.getInstance().getPumps() + " pumps";
        dynamicTextView.setText(text);

        dynamicTextView = getView().findViewById(R.id.time_interval);
        text = "each pump at " + Values.getInstance().getInterval() + " seconds";
        dynamicTextView.setText(text);

        dynamicTextView = getView().findViewById(R.id.flower_type_button);
        dynamicTextView.setText(Values.getInstance().getFlower_type());

        dynamicTextView = getView().findViewById(R.id.humidity_value);
        text = Values.getInstance().getHumidity() + "%";
        dynamicTextView.setText(text);

        dynamicTextView = getView().findViewById(R.id.temperature_value);
        text = Values.getInstance().getTemperature() + "°C";
        dynamicTextView.setText(text);

        dynamicTextView = getView().findViewById(R.id.soil_status_value);
        text = Values.getInstance().getSoil_status();
        dynamicTextView.setText(text);

    }

    private void showDialog(String text1, TextView dynamicTextView, String text2, int dialog_id) {
        // Create the dialog
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialog_id);

        // Find views inside the dialog layout
        EditText editTextDialog = dialog.findViewById(R.id.editTextDialog);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        // Set OnClickListener on the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText
                String inputText = editTextDialog.getText().toString();

                if(!inputText.isEmpty())
                {
                    if(dialog_id==R.layout.pumps_layout){
                        Values.getInstance().setPumps(Integer.parseInt(inputText));
                        System.out.println(Values.getInstance().getPumps());
                    }
                    else {
                        Values.getInstance().setInterval(Integer.parseInt(inputText));
                        System.out.println(Values.getInstance().getInterval());
                    }
                }

                inputText = text1 + inputText + text2;
                System.out.println(inputText);

                dynamicTextView.setText(inputText);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
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

                String command = "cd /home && cd barsi && cd Desktop && " +
                        "python soil_sensor.py " + Values.getInstance().getPumps() +
                        " " + Values.getInstance().getInterval();

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