/*
* Copyright (C) 2015 Vincent Mi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* 참고:
* https://github.com/vinc3m1/RoundedImageView/blob/master/roundedimageview/src/main/java/com/makeramen/roundedimageview/RoundedDrawable.java
*
*/

package kr.swkang.swimageview.utils;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author (Edited by) KangSung-Woo
 * @since 2016/08/03
 */
public class RoundedDrawable
    extends Drawable {
  public static final String TAG                  = "RoundedDrawable";
  public static final int    DEFAULT_BORDER_COLOR = Color.BLACK;

  private final RectF mBounds       = new RectF();
  private final RectF mDrawableRect = new RectF();
  private final RectF mBitmapRect   = new RectF();
  private final Bitmap mBitmap;
  private final Paint  mBitmapPaint;
  private final int    mBitmapWidth;
  private final int    mBitmapHeight;
  private final RectF mBorderRect = new RectF();
  private final Paint mBorderPaint;
  private final Matrix mShaderMatrix = new Matrix();

  private BitmapShader mBitmapShader;
  private Shader.TileMode mTileModeX     = Shader.TileMode.CLAMP;
  private Shader.TileMode mTileModeY     = Shader.TileMode.CLAMP;
  private boolean         mRebuildShader = true;

  private CornerType cornerType;
  private float      cornerRadius;
  private RectF rectCorners = new RectF();

  private boolean             mOval        = false;
  private float               mBorderWidth = 0;
  private ColorStateList      mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
  private ImageView.ScaleType mScaleType   = ImageView.ScaleType.FIT_CENTER;


  public RoundedDrawable(Bitmap bitmap) {
    mBitmap = bitmap;

    mBitmapWidth = bitmap.getWidth();
    mBitmapHeight = bitmap.getHeight();
    mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

    mBitmapPaint = new Paint();
    mBitmapPaint.setStyle(Paint.Style.FILL);
    mBitmapPaint.setAntiAlias(true);

    mBorderPaint = new Paint();
    mBorderPaint.setStyle(Paint.Style.STROKE);
    mBorderPaint.setAntiAlias(true);
    mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
    mBorderPaint.setStrokeWidth(mBorderWidth);
  }

  public static RoundedDrawable fromBitmap(Bitmap bitmap) {
    if (bitmap != null) {
      return new RoundedDrawable(bitmap);
    }
    else {
      return null;
    }
  }

  public static Drawable fromDrawable(Drawable drawable) {
    if (drawable != null) {
      if (drawable instanceof RoundedDrawable) {
        // just return if it's already a RoundedDrawable
        return drawable;
      }
      else if (drawable instanceof LayerDrawable) {
        LayerDrawable ld = (LayerDrawable) drawable;
        int num = ld.getNumberOfLayers();

        // loop through layers to and change to RoundedDrawables if possible
        for (int i = 0; i < num; i++) {
          Drawable d = ld.getDrawable(i);
          ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
        }
        return ld;
      }

      // try to get a bitmap from the drawable and
      Bitmap bm = drawableToBitmap(drawable);
      if (bm != null) {
        return new RoundedDrawable(bm);
      }
    }
    return drawable;
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    Bitmap bitmap;
    int width = Math.max(drawable.getIntrinsicWidth(), 2);
    int height = Math.max(drawable.getIntrinsicHeight(), 2);
    try {
      bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
    } catch (Exception e) {
      e.printStackTrace();
      Log.w(TAG, "Failed to create bitmap from drawable!");
      bitmap = null;
    }

    return bitmap;
  }

  @Override
  public boolean isStateful() {
    return mBorderColor.isStateful();
  }

  @Override
  protected boolean onStateChange(int[] state) {
    int newColor = mBorderColor.getColorForState(state, 0);
    if (mBorderPaint.getColor() != newColor) {
      mBorderPaint.setColor(newColor);
      return true;
    }
    else {
      return super.onStateChange(state);
    }
  }

  private void updateShaderMatrix() {
    float scale;
    float dx;
    float dy;

    switch (mScaleType) {
      case CENTER:
        mBorderRect.set(mBounds);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

        mShaderMatrix.reset();
        mShaderMatrix.setTranslate((int) ((mBorderRect.width() - mBitmapWidth) * 0.5f + 0.5f),
                                   (int) ((mBorderRect.height() - mBitmapHeight) * 0.5f + 0.5f));
        break;

      case CENTER_CROP:
        mBorderRect.set(mBounds);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);

        mShaderMatrix.reset();

        dx = 0;
        dy = 0;

        if (mBitmapWidth * mBorderRect.height() > mBorderRect.width() * mBitmapHeight) {
          scale = mBorderRect.height() / (float) mBitmapHeight;
          dx = (mBorderRect.width() - mBitmapWidth * scale) * 0.5f;
        }
        else {
          scale = mBorderRect.width() / (float) mBitmapWidth;
          dy = (mBorderRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                                    (int) (dy + 0.5f) + mBorderWidth);
        break;

      case CENTER_INSIDE:
        mShaderMatrix.reset();

        if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
          scale = 1.0f;
        }
        else {
          scale = Math.min(mBounds.width() / (float) mBitmapWidth,
                           mBounds.height() / (float) mBitmapHeight);
        }

        dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
        dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(dx, dy);

        mBorderRect.set(mBitmapRect);
        mShaderMatrix.mapRect(mBorderRect);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
        mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
        break;

      default:
      case FIT_CENTER:
        mBorderRect.set(mBitmapRect);
        mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.CENTER);
        mShaderMatrix.mapRect(mBorderRect);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
        mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
        break;

      case FIT_END:
        mBorderRect.set(mBitmapRect);
        mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.END);
        mShaderMatrix.mapRect(mBorderRect);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
        mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
        break;

      case FIT_START:
        mBorderRect.set(mBitmapRect);
        mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.START);
        mShaderMatrix.mapRect(mBorderRect);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
        mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
        break;

      case FIT_XY:
        mBorderRect.set(mBounds);
        mBorderRect.inset((mBorderWidth) / 2, (mBorderWidth) / 2);
        mShaderMatrix.reset();
        mShaderMatrix.setRectToRect(mBitmapRect, mBorderRect, Matrix.ScaleToFit.FILL);
        break;
    }

    mDrawableRect.set(mBorderRect);
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);

    mBounds.set(bounds);

    updateShaderMatrix();
  }

  @Override
  public void draw(Canvas canvas) {
    if (mRebuildShader) {
      mBitmapShader = new BitmapShader(mBitmap, mTileModeX, mTileModeY);
      if (mTileModeX == Shader.TileMode.CLAMP && mTileModeY == Shader.TileMode.CLAMP) {
        mBitmapShader.setLocalMatrix(mShaderMatrix);
      }
      mBitmapPaint.setShader(mBitmapShader);
      mRebuildShader = false;
    }

    if (mOval) {
      if (mBorderWidth > 0) {
        canvas.drawOval(mDrawableRect, mBitmapPaint);
        canvas.drawOval(mBorderRect, mBorderPaint);
      }
      else {
        canvas.drawOval(mDrawableRect, mBitmapPaint);
      }
    }

    else {
      drawRoundedRectangles(canvas);
    }

  }

  private void drawRoundedRectangles(Canvas canvas) {
    if (cornerType == CornerType.NONE) {
      canvas.drawRect(mDrawableRect, mBitmapPaint);
    }
    else {
      canvas.drawRoundRect(mDrawableRect, cornerRadius, cornerRadius, mBitmapPaint);

      final float left = mDrawableRect.left;
      final float right = left + mDrawableRect.width();
      final float top = mDrawableRect.top;
      final float bottom = top + mDrawableRect.height();

      final boolean drawBorder = (mBorderWidth > 0);
      float offset = (drawBorder ? mBorderWidth / 2 : 0);
      if (offset < 0) offset = 0;

      switch (cornerType) {
        case ALL: {
          // draw border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          break;
        }
        case TOP: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + offset, top + cornerRadius + offset, right - offset, bottom - offset);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // bottom left corner
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
            // bottom right corner
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
          }
          break;
        }
        case BOTTOM: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + offset, top + offset, right - offset, bottom - cornerRadius - offset);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top left corner
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
            // top right corner
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
          }
          break;
        }
        case LEFT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + cornerRadius + offset, top + offset, right - offset, bottom - offset);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top right corner
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
            // bottom right corner
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
          }
          break;
        }
        case RIGHT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + offset, top + offset, right - cornerRadius - offset, bottom - offset);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top left corner
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
            // bottom left corner
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
          }
          break;
        }
        case TOP_LEFT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + cornerRadius + offset, top + offset, right - offset, bottom - offset);
          canvas.drawRect(new RectF(left + offset, top + cornerRadius + offset, right - offset, bottom - offset), mBitmapPaint);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top right corner
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
            // bottom left corner
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
            // bottom right corner
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
          }
          break;
        }
        case TOP_RIGHT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + offset, top + offset, right - cornerRadius - offset, bottom - offset);
          canvas.drawRect(new RectF(left + offset, top + cornerRadius + offset, right - offset, bottom - offset), mBitmapPaint);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top left corner
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
            // bottom left corner
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
            // bottom right corner
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
          }
          break;
        }
        case BOTTOM_LEFT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + cornerRadius + offset, top + offset, right - offset, bottom - offset);
          canvas.drawRect(new RectF(left + offset, top + offset, right - offset, bottom - cornerRadius - offset), mBitmapPaint);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top left corner
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
            // top right corner
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
            // bottom right corner
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
          }
          break;
        }
        case BOTTOM_RIGHT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left + offset, top + offset, right - cornerRadius - offset, bottom - offset);
          canvas.drawRect(new RectF(left + offset, top + offset, right - offset, bottom - cornerRadius - offset), mBitmapPaint);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            // top left corner
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
            // top right corner
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
            // bottom left corner
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
          }
          break;
        }
        case EXCEPT_TOP_LEFT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left, top, left + cornerRadius, top + cornerRadius);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            canvas.drawLine(left - offset, top, left + cornerRadius, top, mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + cornerRadius, mBorderPaint);
          }
          break;
        }
        case EXCEPT_TOP_RIGHT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(right - cornerRadius, top, right, top + cornerRadius);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            canvas.drawLine(right - cornerRadius - offset, top, right, top, mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + cornerRadius, mBorderPaint);
          }
          break;
        }
        case EXCEPT_BOTTOM_LEFT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(left, bottom - cornerRadius, left + cornerRadius, bottom);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            canvas.drawLine(left - cornerRadius, bottom, left + cornerRadius, bottom, mBorderPaint);
            canvas.drawLine(left, bottom - cornerRadius, left, bottom, mBorderPaint);
          }
          break;
        }
        case EXCEPT_BOTTOM_RIGHT: {
          // draw base border
          if (drawBorder) {
            canvas.drawRoundRect(mBorderRect, cornerRadius, cornerRadius, mBorderPaint);
          }
          // excepted corner drawing
          rectCorners.set(right - cornerRadius, bottom - cornerRadius, right, bottom);
          canvas.drawRect(rectCorners, mBitmapPaint);

          // draw corner border
          if (drawBorder) {
            canvas.drawLine(right - cornerRadius - offset, bottom, right + offset, bottom, mBorderPaint);
            canvas.drawLine(right, bottom - cornerRadius, right, bottom, mBorderPaint);
          }
          break;
        }
      } // end of 'Corner Rectangle' switch case
    }
  } // end of `drawRoundedRectangles()` methods

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public int getAlpha() {
    return mBitmapPaint.getAlpha();
  }

  @Override
  public void setAlpha(int alpha) {
    mBitmapPaint.setAlpha(alpha);
    invalidateSelf();
  }

  @Override
  public ColorFilter getColorFilter() {
    return mBitmapPaint.getColorFilter();
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    mBitmapPaint.setColorFilter(cf);
    invalidateSelf();
  }

  @Override
  public void setDither(boolean dither) {
    mBitmapPaint.setDither(dither);
    invalidateSelf();
  }

  @Override
  public void setFilterBitmap(boolean filter) {
    mBitmapPaint.setFilterBitmap(filter);
    invalidateSelf();
  }

  @Override
  public int getIntrinsicWidth() {
    return mBitmapWidth;
  }

  @Override
  public int getIntrinsicHeight() {
    return mBitmapHeight;
  }

  public RoundedDrawable setCorner(CornerType corner, float cornerRadius) {
    this.cornerType = corner;
    this.cornerRadius = cornerRadius;
    invalidateSelf();
    return this;
  }

  public float getBorderWidth() {
    return mBorderWidth;
  }

  public RoundedDrawable setBorderWidth(float width) {
    mBorderWidth = width;
    mBorderPaint.setStrokeWidth(mBorderWidth);
    return this;
  }

  public int getBorderColor() {
    return mBorderColor.getDefaultColor();
  }

  public RoundedDrawable setBorderColor(int color) {
    return setBorderColor(ColorStateList.valueOf(color));
  }

  public RoundedDrawable setBorderColor(ColorStateList colors) {
    mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
    mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
    return this;
  }

  public ColorStateList getBorderColors() {
    return mBorderColor;
  }

  public boolean isOval() {
    return mOval;
  }

  public RoundedDrawable setOval(boolean oval) {
    mOval = oval;
    return this;
  }

  public ImageView.ScaleType getScaleType() {
    return mScaleType;
  }

  public RoundedDrawable setScaleType(ImageView.ScaleType scaleType) {
    if (scaleType == null) {
      scaleType = ImageView.ScaleType.FIT_CENTER;
    }
    if (mScaleType != scaleType) {
      mScaleType = scaleType;
      updateShaderMatrix();
    }
    return this;
  }

  public Shader.TileMode getTileModeX() {
    return mTileModeX;
  }

  public RoundedDrawable setTileModeX(Shader.TileMode tileModeX) {
    if (mTileModeX != tileModeX) {
      mTileModeX = tileModeX;
      mRebuildShader = true;
      invalidateSelf();
    }
    return this;
  }

  public Shader.TileMode getTileModeY() {
    return mTileModeY;
  }

  public RoundedDrawable setTileModeY(Shader.TileMode tileModeY) {
    if (mTileModeY != tileModeY) {
      mTileModeY = tileModeY;
      mRebuildShader = true;
      invalidateSelf();
    }
    return this;
  }

  public Bitmap toBitmap() {
    return drawableToBitmap(this);
  }

  private float findMax(float... values) {
    float max = 0;
    for (float v : values) {
      if (v > max) max = v;
    }
    return max;
  }

  private float findMin(float... values) {
    float min = Float.MAX_VALUE;
    for (float v : values) {
      if (v < min) min = v;
    }
    return min;
  }

}
