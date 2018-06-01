package com.mwapp.ron.fancydice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mwapp.ron.fancydice.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Random rng = new Random();
    private int numDice = 1;
    private TextView[] diceResults;
    private static final int DICE_PER_ROW = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.d4).setTag(4);
        findViewById(R.id.d6).setTag(6);
        findViewById(R.id.d8).setTag(8);
        findViewById(R.id.d10).setTag(10);
        findViewById(R.id.d12).setTag(12);
        findViewById(R.id.d20).setTag(20);
        findViewById(R.id.d100).setTag(100);
        if (savedInstanceState != null)
            numDice = savedInstanceState.getInt("numDice", 1);
        changeNumberOfDice();
    }

    private void changeNumberOfDice() {
        ((TextView) findViewById(R.id.numDice)).setText(String.valueOf(numDice));
        TableLayout individualDiceTable = (TableLayout) findViewById(R.id.individualDice);
        individualDiceTable.removeAllViews();
        if (numDice == 1) {
            ((Button) findViewById(R.id.decrementButton)).setEnabled(false);
            ((TextView) findViewById(R.id.totalText)).setVisibility(View.INVISIBLE);
            individualDiceTable.setVisibility(View.GONE);
        } else {
            ((Button) findViewById(R.id.decrementButton)).setEnabled(true);
            ((TextView) findViewById(R.id.totalText)).setVisibility(View.VISIBLE);
            individualDiceTable.setVisibility(View.VISIBLE);
        }
        diceResults = new TextView[numDice];
        TableRow currentRow = null;
        //Add the individual dice results.
        for (int i = 0; i < numDice; i++) {
            if ((i % DICE_PER_ROW) == 0) { //Add a new row.
                currentRow = new TableRow(getApplicationContext());
                currentRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                individualDiceTable.addView(currentRow);
            }
            TextView newView = new TextView(getApplicationContext());
            newView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            newView.setText("0");
            newView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            newView.setTextColor(0xff000000);
            newView.setTextSize(18);
            currentRow.addView(newView);
            diceResults[i] = newView;
        }
    }

    private void roll(int sides) {
        int[] rollResults = new int[numDice];
        int total = 0;
        for (int i = 0; i < numDice; i++) {
            int singleResult = rng.nextInt(sides) + 1;
            rollResults[i] = singleResult;
            total += singleResult;
            diceResults[i].setText(String.valueOf(singleResult));
        }
        TextView totalText = (TextView) findViewById(R.id.result);
        totalText.setText(String.valueOf(total));
    }

    private void startRolling(int sides) {
        new DiceRollAnimation(sides).execute();
    }

    public void rollButtonClicked(View v) {
        startRolling((Integer)v.getTag());
    }

    public void customDiceButtonClicked(View v) {
        EditText sidesAmount = (EditText) findViewById(R.id.sidesAmount);
        int customSides;
        if (sidesAmount.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.customSidesToast, Toast.LENGTH_SHORT).show();
            return;
        }
        customSides = Integer.parseInt(sidesAmount.getText().toString());
        if (customSides < 1) {
            Toast.makeText(getApplicationContext(), R.string.cutomSidesMinimumToast, Toast.LENGTH_SHORT).show();
            return;
        }
        startRolling(customSides);
    }

    public void decrementNumDice(View v) {
        numDice--;
        if (numDice < 1) numDice = 1;
        changeNumberOfDice();
    }

    public void  incrementNumDice(View v) {
        numDice++;
        changeNumberOfDice();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("numDice", 1);
    }

    private class DiceRollAnimation extends AsyncTask<Integer, Integer, Integer> {
        private int sides;
        static final int TIME_BETWEEN_FLASHES_MS = 20;
        static final int NUM_FAKE_FLASHES = 4;

        private DiceRollAnimation(int sides) { //When we add number of dice, etc., they will go in this constructor as well
            this.sides = sides;
        }


        @Override
        protected Integer doInBackground(Integer... integers) {
            //This sleeps, which is why we are running it in the background.  Progress is just "we need to roll" on the UI thread.
            for (int i = 0; i < NUM_FAKE_FLASHES; i++) {
                publishProgress();
                SystemClock.sleep(TIME_BETWEEN_FLASHES_MS);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integers) {
            MainActivity.this.roll(sides);
        }

        @Override
        protected void onProgressUpdate(Integer... integers) {
            MainActivity.this.roll(sides);
        }
    }
}
