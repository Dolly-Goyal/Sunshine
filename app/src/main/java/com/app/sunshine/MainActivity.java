package com.app.sunshine;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
            backgroungThread backgroungThread = new backgroungThread();
            backgroungThread.execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ForecastFragment extends Fragment {

        private ArrayAdapter<String> mForecastAdapter;

        public ForecastFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
/* Create some dummy data for the ListView. Here's a sample weekly forecast */
            String[] forecastArray = {
                    "Today - Sunny - 88/63",
                    "Tomorrow -Foggy- 70/40",
                    "Weds - Cloudy - 72/63",
                    "Thurs - AAsteroids - 75/65",
                    "Fri - Heavy Rain - 65/56",
                    "Sat - HELP TRAPPED IN WEATHERSTATION - 60/51",
                    "Sun - Sunny -80/68"
            };
            List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

            mForecastAdapter =

                    new ArrayAdapter<String>(
                            // The current context
                            getActivity(),
                            // ID of list item layout
                            R.layout.list_item_forecast,
                            // ID of the textView to populate
                            R.id.list_item_forecast_textView,
                            //Forecast data
                            weekForecast);

            // Get a reference to the ListView, and attach this adapter
            ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
            listView.setAdapter(mForecastAdapter);

            return rootView;
        }
    }
            protected class backgroungThread extends AsyncTask<Object,Void,JSONObject> {


                /**
                 * Override this method to perform a computation on a background thread. The
                 * specified parameters are the parameters passed to {@link #execute}
                 * by the caller of this task.
                 * <p/>
                 * This method can call {@link #publishProgress} to publish updates
                 * on the UI thread.
                 *
                 * @param params The parameters of the task.
                 * @return A result, defined by the subclass of this task.
                 * @see #onPreExecute()
                 * @see #onPostExecute
                 * @see #publishProgress
                 */
        @Override
        protected JSONObject doInBackground(Object... params) {

// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;

// Will contain the raw JSON response as a string.
                    String forecastJsonStr = null;
                    try

                    {
                        // Construct the URL for the OpenWeatherMap query
                        // Possible parameters are avaiable at OWM's forecast API page, at
                        // http://openweathermap.org/API#forecast
                        URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                        // Create the request to OpenWeatherMap, and open the connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        // Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.
                            forecastJsonStr = null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                            // But it does make debugging a *lot* easier if you print out the completed
                            // buffer for debugging.
                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {
                            // Stream was empty.  No point in parsing.
                            forecastJsonStr = null;
                        }
                        forecastJsonStr = buffer.toString();
                    }

                    catch(
                            IOException e
                            )

                    {
                        Log.e("ForecastFragment", "Error ", e);
                        // If the code didn't successfully get the weather data, there's no point in attemping
                        // to parse it.
                        forecastJsonStr = null;
                    }


                    finally

                    {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                Log.e("ForecastFragment", "Error closing stream", e);
                            }
                        }
                    }


                    return null;


                }
            }

    }

