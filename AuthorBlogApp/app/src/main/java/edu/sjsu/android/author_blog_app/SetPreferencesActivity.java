package edu.sjsu.android.author_blog_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SetPreferencesActivity extends Activity {
    public final static String STORE_PREFERENCES="storePrefFinal.txt";
    public int counter=0;
    private SimpleDateFormat s=new SimpleDateFormat("MM/dd/yyyy-hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_preferences);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("COUNTER", 0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        counter=sharedPrefs.getInt("COUNTER", 0);
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

    public void onSave(View view){
        EditText book_name = (EditText)findViewById(R.id.enterBookName);
        String name =book_name.getText().toString();
        EditText book_author = (EditText)findViewById(R.id.enterBookAuthor);
        String author =book_author.getText().toString();
        EditText description = (EditText)findViewById(R.id.enterDescription);
        String descriptionValue = description.getText().toString();

        if(name!=null && author!=null && descriptionValue!=null)
        {
            try
            {
                counter+=1;
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("COUNTER", counter);
                editor.putString("NAME", name);
                editor.putString("AUTHOR", author);
                editor.putString("DESCRIPTION", descriptionValue);
                editor.commit();

                OutputStreamWriter out=new OutputStreamWriter(openFileOutput(STORE_PREFERENCES,MODE_APPEND));
                String message ="\nSaved Preference " +counter+", "+s.format(new Date())+", " + "Book Name: "+ name +", " + "Author: " + author + ", " + "Description: " +descriptionValue;
                out.write(message);
                out.close();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        //Go back to the main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCancel(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
