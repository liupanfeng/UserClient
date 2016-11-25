package com.jikexueyuan.baidumap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputCurrentActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_input_current;

    private Button btn_convert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_current);
        et_input_current=(EditText)findViewById(R.id.et_input_current);

        btn_convert=(Button)findViewById(R.id.btn_convert);
        btn_convert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String data=et_input_current.getText().toString().trim();
        if ("".equals(data)||data==null){
            Toast.makeText(InputCurrentActivity.this,"位置不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent();
        intent.putExtra("data",data);
        setResult(RESULT_OK,intent);
        finish();

    }
}
