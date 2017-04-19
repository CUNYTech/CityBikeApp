package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by coding class on 4/17/2017.
 */

public class AboutUs extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_page);

        textView = (TextView) findViewById(R.id.about_us_body);
        textView.setText(
                "     City​ ​Bike​ ​lets​ ​you​ ​travel​ ​anywhere​ ​in​ ​the​ ​city​ ​using​ ​CitiBikes​ ​by​ ​showing​ ​you​ ​the​ ​best" +
                "route​ ​to​ ​swap​ ​bikes​ ​at​ ​stations​ ​along​ ​the​ ​way​ ​to​ ​your​ ​destination. \r\n\n" +

                        "     CitiBike’s​ ​day​ ​and​ ​annual​ ​passes​ ​give​ ​you​ ​unlimited​ ​access​ ​to​ ​their​ ​bikes​ ​all​ ​day​ ​with" +
               " the​ ​one​ ​drawback​ ​of​ ​either​ ​a​ ​30​ ​or​ ​45​ ​minute​ ​limit​ ​per​ ​session.​ ​However,​ ​it’s​ ​possible​ ​to​ ​return" +
               " your​ ​bike​ ​to​ ​one​ ​of​ ​their​ ​more​ ​than​ ​a​ ​thousand​ ​stations,​ ​and​ ​immediately​ ​get​ ​on​ ​another​ ​one​ ​and "+
       " continue​ ​your​ ​journey. \r\n\n " +

              "     City​ ​Bike​ ​can​ ​show​ ​you​ ​the​ ​best​ ​way​ ​to​ ​your​ ​destination,​ ​even​ ​if​ ​it​ ​is​ ​outside​ ​your​ ​30/45 " +
      "  minute​ ​maximum​ ​time​ ​allotment.​ ​City​ ​Bike​ ​will​ ​give​ ​you​ ​a​ ​route​ ​to​ ​your​ ​destination​ ​that​ ​lets​ ​you" +
        "bunny​ ​hop​ ​from​ ​one​ ​station​ ​until​ ​the​ ​next,​ ​while​ ​staying​ ​below​ ​the​ ​30/45​ ​minute​ ​limit​ ​per​ ​station. \r\n\n "+

               "     Type​ ​in​ ​your​ ​destination,​ ​get​ ​on​ ​your​ ​CitiBike​ ​and​ ​start​ ​peddling​ ​without​ ​limitation!"  );



    }
}
