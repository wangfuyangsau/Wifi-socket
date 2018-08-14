package wifichazuo.com.wifichazuo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    private EditText editText;
    private EditText editText2;
    private Button button;
    private TextView textView3;
    private Button button2;
    private boolean exit=false;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editText=(EditText)findViewById(R.id.editText);
        editText2=(EditText)findViewById(R.id.editText2);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        textView3=(TextView)findViewById(R.id.textView3);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean pang=sharedPreferences.getBoolean("remember_password",false);
         if(pang)
          {
            editText.setText(sharedPreferences.getString("Name",""));
            editText2.setText(sharedPreferences.getString("Password",""));
            checkBox.setChecked(true);
          }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zhanghu =editText.getText().toString();
                String mima =editText2.getText().toString();
                editor=sharedPreferences.edit();

                if(zhanghu.equals("12345")&&mima.equals("54321")){

                    Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                    startActivity(intent);
                        if(checkBox.isChecked())
                        {
                            editor.putString("Name",zhanghu);
                            editor.putString("Password",mima);
                            editor.putBoolean("remember_password",true);
                            editor.commit();
                            Toast.makeText(Main2Activity.this,"账户密码保存成功",Toast.LENGTH_SHORT).show();
                        } else {

                            editor.putBoolean("remember_password",false);
                            editor.commit();
                        }
                }

                else {

                    editText.setText("");
                    editText2.setText("");
                    editor.putBoolean("remember_password",false);
                    editor.commit();
                    Toast.makeText(Main2Activity.this,"账户名或密码错误",Toast.LENGTH_SHORT).show();}
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                editText2.setText("");
                editor.putBoolean("remember_password",false);
                textView3.setText("欢迎下次光临！！");
                editor.commit();
            }
        });
    }
    public void exit() {
        if (exit == true) {
            this.finish();
        }
        exit = true;
        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
    }


}
