package edu.sjsu.android.author_blog_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteActivity extends Activity {
    public int counter=0;
    private SimpleDateFormat s=new SimpleDateFormat("MM/dd/yyyy-hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("SQL_COUNTER", 0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("SQL_COUNTER", 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveMessage(View view) {
        EditText blogMsg = (EditText) findViewById(R.id.enterBlogMessage);
        String message = blogMsg.getText().toString();
        DataController dataController = new DataController(getBaseContext());
        dataController.open();
        long retValue = dataController.insert(message);
        dataController.close();
        if (retValue != -1) {
            Context context = getApplicationContext();
            CharSequence text = "Message Saved Successfully!";
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();

            try {
                counter += 1;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("SQL_COUNTER", counter);
                editor.commit();

                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(SetPreferencesActivity.STORE_PREFERENCES, MODE_APPEND));
                out.write("\nSQLite " + counter + ", " + s.format(new Date()));
                out.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        //Go back to main screen
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void cancelMessage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
