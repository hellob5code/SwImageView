package kr.swkang.swimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import kr.swkang.swimageview.utils.ArgbEvaluatorCompat;
import kr.swkang.swimageview.utils.CornerType;
import kr.swkang.swimageview.utils.RoundedDrawable;
import kr.swkang.swimageview.utils.RoundedDrawableParams;
import kr.swkang.swimageview.utils.ShapeType;

/**
 * @author KangSung-Woo
 * @since 2016/08/03
 */
public class SwImageView
    extends ImageView {
  public static final  int         DEFAULT_TRANSITION_DURATION = 250;
  private static final String      TAG                         = SwImageView.class.getSimpleName();
  private final        ScaleType[] scaleTypes                  = {
      ScaleType.MATRIX,
      ScaleType.FIT_XY,
      ScaleType.FIT_START,
      ScaleType.FIT_CENTER,
      ScaleType.FIT_END,
      ScaleType.CENTER,
      ScaleType.CENTER_CROP,
      ScaleType.CENTER_INSIDE
  };
  private Drawable              drawable;
  private RoundedDrawableParams roundedDrawableParams;
  private boolean               isEnableTransition;
  private int                   transitionDuration;

  public SwImageView(Context context) {
    this(context, null);
  }

  public SwImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SwImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializeParams(context, attrs, defStyleAttr);
  }

  private void initializeParams(@NonNull Context context, AttributeSet attrs, int defStyle) {
    // set default values
    initializeDefaultParams();
    roundedDrawableParams = new RoundedDrawableParams();
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwImageView, defStyle, 0);

      // image drawable ScaleTypes
      final int scaleTypeIndex = a.getInt(R.styleable.SwImageView_android_scaleType, 6);
      final ScaleType scaleType = scaleTypes[scaleTypeIndex];
      roundedDrawableParams.setScaleType(scaleType);

      // corner type
      CornerType cornerType = CornerType.NONE;
      final int cornerTypeValue = a.getInt(R.styleable.SwImageView_siv_corner_type, CornerType.NONE.getValue());
      cornerType = CornerType.parseFromValue(cornerTypeValue);

      // corner radius
      final float cornerRadius = (float) a.getDimensionPixelSize(R.styleable.SwImageView_siv_corner_radius, (int) RoundedDrawableParams.DEFAULT_CORNER_RADIUS);
      if (cornerRadius > 0) {
        if (cornerType == CornerType.NONE) {
          // set Default
          cornerType = CornerType.ALL;
        }
      }
      roundedDrawableParams.setRoundedCorner(cornerType, cornerRadius);

      // border width
      final float borderWidth = (float) a.getDimensionPixelSize(R.styleable.SwImageView_siv_border_width, (int) RoundedDrawableParams.DEFAULT_BORDER_WIDTH);
      roundedDrawableParams.setBorderWidth(borderWidth);

      // border colors
      final ColorStateList borderColor = a.getColorStateList(R.styleable.SwImageView_siv_border_color);
      if (borderColor != null) {
        roundedDrawableParams.setBorderColorList(borderColor);
      }

      // get shape type
      final int shapeTypeValue = a.getInt(R.styleable.SwImageView_siv_shapetype, ShapeType.RECTANGLE.getValue());
      roundedDrawableParams.setShapeType(ShapeType.parseFromValue(shapeTypeValue));

      // x, y tile mode
      final int tileModeValue = a.getInt(R.styleable.SwImageView_siv_tileMode, -1);
      if (tileModeValue != -1) {
        Shader.TileMode tileMode = parseTileModeFromValues(tileModeValue);
        if (tileMode != null) {
          roundedDrawableParams.setTileModeX(tileMode);
          roundedDrawableParams.setTileModeY(tileMode);
        }
      }

      // x tile mode
      final int tileModeXvalue = a.getInt(R.styleable.SwImageView_siv_tileMode_x, -1);
      if (tileModeXvalue != -1) {
        Shader.TileMode tileModeX = parseTileModeFromValues(tileModeXvalue);
        if (tileModeX != null) {
          roundedDrawableParams.setTileModeX(tileModeX);
        }
      }

      // y tile mode
      final int tileModeYvalue = a.getInt(R.styleable.SwImageView_siv_tileMode_y, -1);
      if (tileModeYvalue != -1) {
        Shader.TileMode tileModeY = parseTileModeFromValues(tileModeYvalue);
        if (tileModeY != null) {
          roundedDrawableParams.setTileModeY(tileModeY);
        }
      }

      // click highlighting color
      final ColorStateList clickColors = a.getColorStateList(R.styleable.SwImageView_siv_click_color);
      if (clickColors != null) {
        final int clickColor = clickColors.getDefaultColor();
        roundedDrawableParams.setClickHighlightingColor(clickColor);
      }

      // click animation durations
      final int clickAnimDurations = a.getInt(R.styleable.SwImageView_siv_click_duration, RoundedDrawableParams.DEFAULT_CLICK_ANIM_DURATION);
      roundedDrawableParams.setClickEnterAnimDuration(clickAnimDurations);
      roundedDrawableParams.setClickExitAnimDuration(clickAnimDurations);

      // click enter animation durations
      final int clickEnterAnimDuration = a.getInt(R.styleable.SwImageView_siv_click_enter_duration, RoundedDrawableParams.DEFAULT_CLICK_ANIM_DURATION);
      roundedDrawableParams.setClickEnterAnimDuration(clickEnterAnimDuration);

      // click exit animation durations
      final int clickExitAnimDuration = a.getInt(R.styleable.SwImageView_siv_click_exit_duration, RoundedDrawableParams.DEFAULT_CLICK_ANIM_DURATION);
      roundedDrawableParams.setClickExitAnimDuration(clickExitAnimDuration);

      a.recycle();
    }

    updateRoundedDrawableParameters(drawable);
    setFocusable(true);
  }

  private void initializeDefaultParams() {
    this.isEnableTransition = true;
    this.transitionDuration = DEFAULT_TRANSITION_DURATION;
  }

  private void updateRoundedDrawableParameters(@NonNull Drawable drawable) {
    if (roundedDrawableParams != null) {
      if (drawable instanceof RoundedDrawable) {
        RoundedDrawable rd = (RoundedDrawable) drawable;
        rd.setScaleType(roundedDrawableParams.getScaleType())
          .setCorner(roundedDrawableParams.getCornerType(), roundedDrawableParams.getCornerRadius())
          .setBorderWidth(roundedDrawableParams.getBorderWidth())
          .setBorderColor(roundedDrawableParams.getBorderColor())
          .setShapeType(roundedDrawableParams.getShapeType())
          .setTileModeX(roundedDrawableParams.getTileModeX())
          .setTileModeY(roundedDrawableParams.getTileModeY());

      }
      else if (drawable instanceof LayerDrawable) {
        LayerDrawable ld = ((LayerDrawable) drawable);
        for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
          updateRoundedDrawableParameters(ld.getDrawable(i));
        }
      }
    }
  }

  public void setRoundedDrawableParams(@NonNull RoundedDrawableParams params) {
    this.roundedDrawableParams = params;
    updateRoundedDrawableParameters(drawable);
  }

  public RoundedDrawableParams getRoundedDrawableParams() {
    return this.roundedDrawableParams;
  }

  public void setEnableTransition(boolean isEnable) {
    this.isEnableTransition = isEnable;
  }

  public void setTransitionDuration(@IntRange(from = 50) int transitionDuration) {
    this.transitionDuration = transitionDuration;
  }

  @Override
  public void setScaleType(@NonNull ScaleType scaleType) {
    if (roundedDrawableParams != null && roundedDrawableParams.getScaleType() != null &&
        roundedDrawableParams.getScaleType() != scaleType) {
      if (roundedDrawableParams != null) {
        roundedDrawableParams.setScaleType(scaleType);
      }
      updateRoundedDrawableParameters(drawable);
      invalidate();
    }
  }

  public void setBorderWidth(@FloatRange(from = 0f) float borderWidth) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setBorderWidth(borderWidth);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setBorderWidthDiP(@IntRange(from = 0) int borderWidthDiP) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setBorderWidth(convertPixelToDpi(getResources(), borderWidthDiP));
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setBorderColor(@ColorInt int borderColor) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setBorderColor(borderColor);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setBorderColorList(@NonNull ColorStateList colorStateList) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setBorderColorList(colorStateList);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setShapeType(@NonNull ShapeType shapeType) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setShapeType(shapeType);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setTileModeX(@NonNull Shader.TileMode tileModeX) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setTileModeX(tileModeX);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setTileModeY(@NonNull Shader.TileMode tileModeY) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setTileModeY(tileModeY);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setClickHighlightingColor(@ColorInt int highlightingColor) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setClickHighlightingColor(highlightingColor);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setClickHighlightingColor(@FloatRange(from = 0.0f, to = 1.0f) float alpha, @ColorInt int highlightingColor) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setClickHighlightingColor(alpha, highlightingColor);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setClickEnterAnimDuration(@IntRange(from = 50) int enterAnimDuration) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setClickEnterAnimDuration(enterAnimDuration);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setClickExitAnimDuration(@IntRange(from = 50) int exitAnimDuration) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setClickExitAnimDuration(exitAnimDuration);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setRoundedCorner(@NonNull CornerType cornerType, @FloatRange(from = 0f) float cornerRadius) {
    if (roundedDrawableParams != null) {
      roundedDrawableParams.setRoundedCorner(cornerType, cornerRadius);
    }
    updateRoundedDrawableParameters(drawable);
    invalidate();
  }

  public void setRoundedCornerRadius(@FloatRange(from = 0f) float cornerRadius) {
    if (roundedDrawableParams != null) {
      setRoundedCorner(roundedDrawableParams.getCornerType(), cornerRadius);
    }
  }

  public void setRoundedCornerType(@NonNull CornerType cornerType) {
    if (roundedDrawableParams != null) {
      setRoundedCorner(cornerType, roundedDrawableParams.getCornerRadius());
    }
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    this.drawable = RoundedDrawable.fromDrawable(drawable);
    if (this.drawable != null) {
      setImageFromRounndedDrawable(this.drawable);
    }
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    drawable = RoundedDrawable.fromBitmap(bm);
    if (drawable != null) {
      setImageFromRounndedDrawable(drawable);
    }
  }

  @Override
  public void setImageResource(int resId) {
    drawable = RoundedDrawable.fromDrawable(ResourcesCompat.getDrawable(getResources(), resId, null));
    if (drawable != null) {
      setImageFromRounndedDrawable(drawable);
    }
  }

  private void setImageFromRounndedDrawable(Drawable drawable) {
    if (drawable == null) {
      Log.e(TAG, "Image drawable instance is Null.");
    }
    else {
      updateRoundedDrawableParameters(drawable);
      if (isEnableTransition) {
        clearAnimation();
        final Drawable back = getDrawable();
        final TransitionDrawable td = createTransitionDrawable(
            back == null ? new ColorDrawable(
                Color.BLACK
            ) : back,
            drawable
        );
        td.setCrossFadeEnabled(true);
        super.setImageDrawable(td);
        td.startTransition(transitionDuration <= 0 ? 250 : transitionDuration);
        return;
      }
    }
    super.setImageDrawable(drawable);
  }

  private TransitionDrawable createTransitionDrawable(Drawable startLayer, Drawable endLayer) {
    return new TransitionDrawable(new Drawable[]{
        startLayer, endLayer
    });
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isClickable()) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
          // is touch started
          updateARGBvalueAnimation(true);
          break;
        }
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_SCROLL: {
          break;
        }
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL: {
          // cancle event
          updateARGBvalueAnimation(false);
          break;
        }
      }
    }
    return super.onTouchEvent(event);
  }

  private void updateARGBvalueAnimation(final boolean isEnterAnimation) {
    clearAnimation();

    if (roundedDrawableParams != null) {
      // animation of ARGB start color(EnterColor) to end color(ExitColor)
      int defColor = roundedDrawableParams.getClickHighlightingColor().getDefaultColor();
      ValueAnimator valueAnimator = ofARGB(
          isEnterAnimation ? Color.TRANSPARENT : defColor,
          isEnterAnimation ? defColor : Color.TRANSPARENT
      );

      // set animation durations
      final int duration = (isEnterAnimation ?
          roundedDrawableParams.getClickEnterDuration() :
          roundedDrawableParams.getClickExitDuration());
      valueAnimator.setDuration(duration);

      // update color filters
      valueAnimator.addUpdateListener(
          new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
              final int argbValue = (int) valueAnimator.getAnimatedValue();
              getDrawable().setColorFilter(argbValue, PorterDuff.Mode.SRC_ATOP);
              invalidate();
            }
          }
      );

      // attach end animation callback listener
      valueAnimator.addListener(
          new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              if (!isEnterAnimation) {
                SwImageView.this.clearColorFilter();
              }
            }
          }
      );

      // start animations
      valueAnimator.start();
    }
    else {
      Log.e(TAG, "RoundedDrawable Parameter Object is Null.");
    }
  }

  private ValueAnimator ofARGB(int... values) {
    ValueAnimator valueAnimator = new ValueAnimator();
    valueAnimator.setIntValues(values);
    valueAnimator.setEvaluator(ArgbEvaluatorCompat.getsInatance());
    return valueAnimator;
  }

  private Shader.TileMode parseTileModeFromValues(int value) {
    switch (value) {
      case 0:
        return Shader.TileMode.CLAMP;
      case 1:
        return Shader.TileMode.REPEAT;
      case 2:
        return Shader.TileMode.MIRROR;
    }
    return null;
  }

  /**
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(@NonNull Resources res, int pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(@NonNull Resources res, int dpi) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, res.getDisplayMetrics());
  }

  /**
   * Pixel 단위 숫자를 DPI단위 Float형태의 숫자로 변환한다.
   *
   * @param res   Resources.
   * @param pixel 변환대상 Pixel 단위 숫자.
   * @return Float형태의 DPI.
   */
  public static float convertPixelToDpi(@NonNull Resources res, float pixel) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixel, res.getDisplayMetrics());
  }

  /**
   * DPI 단위 숫자를 Pixel 단위 Float형태의 숫자로 변환한다.
   *
   * @param res Resources.
   * @param dpi 변환대상 DPI단위의 숫자.
   * @return Float형태의 pixel 숫자.
   */
  public static float convertDpiToPixel(@NonNull Resources res, float dpi) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, res.getDisplayMetrics());
  }

}
