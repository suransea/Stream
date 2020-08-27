package top.srsea.stream.tool;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import top.srsea.stream.BaseActivity;
import top.srsea.stream.R;

public class PingActivity extends BaseActivity {
    private static final int PING_COUNT = 10;
    private TextInputEditText hostEdit;
    private Button pingButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        setTitle(R.string.title_activity_ping);
        initView();
    }

    private void initView() {
        hostEdit = findViewById(R.id.et_ping);
        pingButton = findViewById(R.id.btn_ping);
        resultTextView = findViewById(R.id.tv_ping_result);
        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String host = hostEdit.getText().toString();
                resultTextView.setText("");
                hostEdit.setEnabled(false);
                pingButton.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        ping(host);
                    }
                }.start();
            }
        });
    }

    private void ping(String host) {
        String cmd = String.format(Locale.getDefault(),
                "ping -c %d %s", PING_COUNT, host);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            final Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                final String next = scanner.nextLine();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String last = resultTextView.getText().toString() + "\n";
                        resultTextView.setText(last + next);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hostEdit.setEnabled(true);
                pingButton.setEnabled(true);
            }
        });
    }
}
