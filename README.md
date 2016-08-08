# SwImageView  

[ ![Download](https://api.bintray.com/packages/burkdog/maven/swimageview/images/download.svg) ](https://bintray.com/burkdog/maven/swimageview/_latestVersion)
  
### 1. Features
 - [Rounded corner Image drawable](https://github.com/vinc3m1/RoundedImageView/blob/master/roundedimageview/src/main/java/com/makeramen/roundedimageview/RoundedDrawable.java)
 - Oval Image drawable. 
 - Border color/width. 
 - specify corner types. (ALL, TOP, BOTTOM, LEFT, RIGHT... check [this](https://github.com/ksu3101/SwImageView/blob/master/swimageview/src/main/java/kr/swkang/swimageview/utils/CornerType.java)) 
 - Click on highlighting animation effect like iOS. (with enter, exit animation duration)
 - Loaded image transition effect. 

---
### 2. Update Note
- 1.0.1 
 - enable rounded rectangle [corner type](https://github.com/ksu3101/SwImageView/blob/master/swimageview/src/main/java/kr/swkang/swimageview/utils/CornerType.java). 
 - bug fixed 

---
### 4. ScreenShot
 update soon..  
  
---
### 5. Usage
```gradle
dependencies {
  compile 'kr.swkang.swimageview:swimageview:x.x.x'  
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
   app:siv_corner_type="all"
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
siv4.setRoundedCorner(CornerType.BOTTOM_RIGHT, 40);
siv4.setBorderColor(Color.RED);
siv4.setBorderWidth(15);
siv4.setTransitionDuration(200);
siv4.setClickHighlightingColor(0.4f, Color.rgb(255, 0, 0));
siv4.setClickEnterAnimDuration(500);
siv4.setClickExitAnimDuration(1000);

SwImageView siv5 = (SwImageView) findViewById(R.id.siv_5);
siv5.setRoundedDrawableParams(
    new RoundedDrawableParams()
        .setRoundedCorner(CornerType.EXCEPT_TOP_LEFT, 80)
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

---
### 6. Customization
 - See [RoundedDrawableParams](https://github.com/ksu3101/SwImageView/blob/master/swimageview/src/main/java/kr/swkang/swimageview/utils/RoundedDrawableParams.java)

---
### 7. License 
[Vicent mi RoundedDrawable](https://github.com/vinc3m1/RoundedImageView)  
```
Copyright 2016 KangSung-Woo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

