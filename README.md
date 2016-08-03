# SwImageView  

### Features
1. [Rounded corner Image drawable](https://github.com/vinc3m1/RoundedImageView/blob/master/roundedimageview/src/main/java/com/makeramen/roundedimageview/RoundedDrawable.java)
2. Circle Image drawable
3. Border color/width 
4. Click on highlighting animation effect (with enter, exit animation duration)
5. Loaded image transition animation effect. 

#### Todo List
1. specify corner image drawable. 

### ScreenShot
 update soon..  

### Usage
```gradle
dependencies {
  
}
```
- [See sample layout xml file](https://github.com/ksu3101/SwImageView/blob/master/app/src/main/res/layout/activity_sample.xml)
```xml
<kr.swkang.swimageview.SwImageView
  android:id="@+id/siv_1"
  android:layout_width="300dp"
  android:layout_height="200dp"
  android:layout_marginBottom="20dp"
  android:clickable="true"
  app:siv_border_color="@android:color/darker_gray"
  app:siv_border_width="5dp"
  app:siv_click_color="#80000000"
  app:siv_click_enter_duration="150"
  app:siv_click_exit_duration="300"
  app:siv_corner_radius="20dp"
  app:siv_enable_transition="true"
  app:siv_transition_duration="250"/>
```
- [See sample activity source](https://github.com/ksu3101/SwImageView/blob/master/app/src/main/java/kr/swkang/sampleswimageview/SampleActivity.java)
```java
SwImageView siv1 = (SwImageView) findViewById(R.id.siv_1);
siv1.setImageResource(R.drawable.sample_img_1);

// with Picasso
SwImageView siv2 = (SwImageView) findViewById(R.id.siv_2);
Picasso.with(this)
       .load("http://...")
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
       .load("http://...")
       .fit()
       .centerCrop()
       .into(siv5);
```

### Customization
 update soon..  

### License 

