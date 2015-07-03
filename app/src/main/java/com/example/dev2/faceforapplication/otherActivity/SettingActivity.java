package com.example.dev2.faceforapplication.otherActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dev2.faceforapplication.R;

public class SettingActivity extends AppCompatActivity {

    String name = "User name";
    String password = "Password";
    String server = "Server";
    String domain = "Domain";
    String use3g = "Use 3 G";
    String notifications = " Notifications";
    String callSetting = "Call settings";
    String about = "About";

    String[] names = {
            name,
            password,
            server,
            domain,
            use3g,
            notifications,
            callSetting,
            about
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ListView listView = (ListView) findViewById(R.id.listView_setting_activity);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inputDteInToSetting(parent.getItemAtPosition(position).toString());
            }
        });

    }

    private void inputDteInToSetting(String s) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);  // initialisation for dialog
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view;

        if (s.equals(name)) {
            alert.setMessage("Input your " + name);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            view = inflater.inflate(R.layout.dialog_user, null);
            alert.setView(view);
            final EditText editText= (EditText) view.findViewById(R.id.username);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SettingActivity.this, editText.getText().toString() , Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (s.equals(password)) {
            alert.setMessage("Input your " + password);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            view = inflater.inflate(R.layout.dialog_password, null);
            alert.setView(view);
            final EditText editText= (EditText) view.findViewById(R.id.password);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SettingActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (s.equals(server)) {
            alert.setMessage("Input your " + server);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            view = inflater.inflate(R.layout.dialog_server, null);
            alert.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.serverName);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SettingActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (s.equals(domain)) {

        }
        if (s.equals(use3g)) {

        }
        if (s.equals(notifications)) {

        }
        if (s.equals(callSetting)) {

        }
        if (s.equals(about)) {

        }
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setting, menu);
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
