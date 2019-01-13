package smartcampus.bphc.smartcampusproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private String name;
    private String message;
    private TextView submitMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendFeedback(View view) {

        EditText nameEditText = findViewById(R.id.name_text_field);
        name = String.valueOf(nameEditText.getText());

        EditText messageEditText = findViewById(R.id.message_text_field);
        message = String.valueOf(messageEditText.getText());

        submitMessage = findViewById(R.id.thank_you_message);

        if ((name.equals("")) || (message.equals(""))) {

            submitMessage.setText("Empty feedback");
            submitMessage.setVisibility(View.VISIBLE);

        } else {

                new HTTPAsyncTask().execute("http://192.168.137.1:8080/freq/in/form/");
                submitMessage.setVisibility(View.INVISIBLE);
            }
        }

    private class HTTPAsyncTask extends AsyncTask<String,Void,String>{

        String msg ;

        @Override
        protected String doInBackground(String... urls){

            try{
                msg = "Thank you for your feedback!"+ HttpPost(urls[0]);

            }catch(IOException e){
                msg = "Unable to connect to the server!";
            }

            return msg;
        }

        @Override
        protected void onPostExecute(String result){

            submitMessage.setText(result);
            submitMessage.setVisibility(View.VISIBLE);
        }
    }

    private String HttpPost(String myurl) throws IOException {

        URL url = new URL(myurl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);

        conn.setRequestMethod("POST");

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("user",name)
                .appendQueryParameter("mssg",message);

        String info = builder.build().getEncodedQuery();

        setPostRequestContent(conn,info);

        conn.connect();

        return conn.getResponseMessage()+"";

    }

    private void setPostRequestContent(HttpURLConnection conn, String info) throws IOException{

        OutputStream os = conn.getOutputStream();

        BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

        writer.write(info);

        writer.flush();

        writer.close();

        os.close();

    }
}


