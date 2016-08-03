package kr.swkang.sampleswimageview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import kr.swkang.swimageview.SwImageView;
import kr.swkang.swimageview.utils.RoundedDrawableParams;

public class SampleActivity
    extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    SwImageView siv1 = (SwImageView) findViewById(R.id.siv_1);
    siv1.setImageResource(R.drawable.sample_img_1);

    // with Picasso
    SwImageView siv2 = (SwImageView) findViewById(R.id.siv_2);
    Picasso.with(this)
           .load("http://burkdog.cafe24.com/wp/wp-content/uploads/2012/04/cropped-wallhaven-6466-1024x504.jpg")
           .fit()
           .centerCrop()
           .into(siv2);

    SwImageView siv3 = (SwImageView) findViewById(R.id.siv_3);
    siv3.setImageResource(R.drawable.sample_img_2);

    SwImageView siv4 = (SwImageView) findViewById(R.id.siv_4);
    siv4.setImageResource(R.drawable.sample_img_3);
    siv4.setCornerRadius(40);   // px
    siv4.setBorderColor(Color.RED);
    siv4.setBorderWidth(15);
    siv4.setTransitionDuration(200);
    siv4.setClickHighlightingColor(0.4f, Color.rgb(255, 0, 0));
    siv4.setClickEnterAnimDuration(500);
    siv4.setClickExitAnimDuration(1000);

    SwImageView siv5 = (SwImageView) findViewById(R.id.siv_5);
    siv5.setRoundedDrawableParams(
        new RoundedDrawableParams()
            .setCornerRadius(80)
            .setBorderColor(Color.LTGRAY)
            .setBorderWidth(20)
            .setClickHighlightingColor(Color.argb(160, 107, 185, 240))
            .setClickEnterAnimDuration(150)
            .setClickExitAnimDuration(200)
    );
    Picasso.with(this)
           .load("http://burkdog.cafe24.com/wp/wp-content/uploads/2012/04/Screenshot000-1024x643.jpg")
           .fit()
           .centerCrop()
           .into(siv5);

  }
}
