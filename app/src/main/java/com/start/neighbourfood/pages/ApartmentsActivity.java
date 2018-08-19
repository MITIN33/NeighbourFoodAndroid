package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ApartmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_apartments);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final ElegantNumberButton button = (ElegantNumberButton) findViewById(R.id.quantity_button);
        button.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantitySelected = button.getNumber();
            }
        });

        // set the initial and the final Number for the quantity_button
        Integer minQty = 0;
        Integer maxQty = 10;        // Read thid from the DB later
        button.setRange(minQty, maxQty);*/
    }
}
