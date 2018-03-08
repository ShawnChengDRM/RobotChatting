package com.example.silence.sfchat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView msgListView;

    private EditText inputText;

    private Button send;

    private MsgAdapter adapter;

    private List<Msg> msgList = new ArrayList<Msg>();

    private final static String TAG = "最帅的那个丰";
    private final String apiUrl = "http://www.tuling123.com/openapi/api";
    private final String apiKey = "08ad24a15d36468b903d79e973e0266a";
    String urlStr = apiUrl + "?key=" + apiKey;
    final static int ROBOT_MESSAGE = 0;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ROBOT_MESSAGE:
                        String Jsonmessage = (String) msg.obj;
                        //Log.i(TAG, Jsonmessage);
                        String text = "";
                        try {
                            JSONObject jsonObject = new JSONObject(Jsonmessage);
                            text = (String) jsonObject.get("text");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Jsonmessage包括代码编号，数据类型，内容 例：{"code":100000,"text":"你也好 嘻嘻"}
                        if (!"".equals(text)) {
                            Msg msgRobot = new Msg(text, Msg.TYPE_RECEIVED);
                            msgList.add(msgRobot);
                            adapter.notifyDataSetChanged(); //当有新消息时，刷新了ListView中的显示
                            msgListView.setSelection(msgList.size()); //将ListView定位到最后一行
                            inputText.setText(""); //将输入框中的内容清空
                        }
                        //textView.setText(Jsonmessage);
                        //Log.i(TAG, text);
                        //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            }
        };




        initMsgs(); //初始化数据
        adapter = new MsgAdapter(MainActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
/*
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged(); //当有新消息时，刷新了ListView中的显示
                    msgListView.setSelection(msgList.size()); //将ListView定位到最后一行
                    inputText.setText(""); //将输入框中的内容清空
                }
            }
        });
*/
    }

    private void initMsgs() {
        Msg msg1 = new Msg("你好，我是最可爱的Robot(〃∀〃)~♡，我叫最帅的那个丰=。=", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        /*
        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_RECEIVED);
        msgList.add(msg1);

        Msg msg2 = new Msg("Hello. who is that", Msg.TYPE_SENT);
        msgList.add(msg2);

        Msg msg3 = new Msg("This is Tom. Nice talking to you.", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
        */
    }

    public void sendMessage(View view) {
        String sendmessage = inputText.getText().toString();
        final String params = "info=" + sendmessage;

        if (!"".equals(sendmessage)) {
            Msg msg = new Msg(sendmessage, Msg.TYPE_SENT);
            msgList.add(msg);
            adapter.notifyDataSetChanged(); //当有新消息时，刷新了ListView中的显示
            msgListView.setSelection(msgList.size()); //将ListView定位到最后一行
            inputText.setText(""); //将输入框中的内容清空
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                OutputStream outputStream = null;
                BufferedReader reader = null;
                StringBuilder result = new StringBuilder();
                String line = "";
                try {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);

                    outputStream = connection.getOutputStream();
                    outputStream.write(params.getBytes());

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Message message = new Message();
                    message.obj = result.toString();
                    message.what = ROBOT_MESSAGE;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            }
        }).start();
    }

}
