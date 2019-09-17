package com.example.sample35_network2_asynctask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static ProgressDialog dialog;

    MyDialogFragment xxx;
    EditText editUrl;
    TextView txtResult;

    int pValue;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUrl = findViewById(R.id.editUrl);
        txtResult = findViewById(R.id.txtResult);

        //코드로 스크롤 가능
        txtResult.setMovementMethod(new ScrollingMovementMethod());
    }
    public void send(View v){
        new MyAsyncTask().execute(100);
    }//end send
    // InputStream ==> String
    public String streamToString(InputStream is){

        StringBuffer buffer = new StringBuffer();

        BufferedReader reader=null;
        try{
            reader= new BufferedReader(new InputStreamReader(is));
            String data= reader.readLine();
            while(data!=null){
                buffer.append(data);
                data= reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(reader!=null)reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }//end streamToString
    class MyAsyncTask extends AsyncTask<Integer,Integer,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("MyTag" , "onPreExecute: 작업이 시작하기전에 호출,초기화작업");
            xxx = MyDialogFragment.getInstance();
            xxx.show(getSupportFragmentManager(),"MyTag");
        }
        // 작업스레드가 수행한다.
        @Override
        protected String doInBackground(Integer... integers) {
            Log.i("MyTag" , "doInBackground: 작업스레드가 수행,");

            final String url = editUrl.getText().toString();
            InputStream is = null;
            try {
                URL xxx = new URL(url);
                is = xxx.openStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = streamToString(is);
            while (pValue <integers[0]){
                pValue++;
                publishProgress(pValue);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//end while
            //  //onProgressUpdate가 받는다.
            return "작업완료";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("MyTag" , "onProgressUpdate: publishProgress메서드에 의해호출");
            dialog.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("MyTag" , "onPostExecute: doInBackground메서드 수행후에 호출");
            txtResult.setText(result);
            xxx.dismiss();
        }
    }//end MyAsyncTask

    ///////////////////////////////////
    public static class MyDialogFragment extends DialogFragment {
        public static MyDialogFragment getInstance(){
            return new MyDialogFragment();
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("타이틀");
            dialog.setMessage("메시지");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            return dialog;
        }
    }//end MyDialogFragment
    ///////////////////////////////////
}//end class
