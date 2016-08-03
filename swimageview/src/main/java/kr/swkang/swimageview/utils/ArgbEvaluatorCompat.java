package kr.swkang.swimageview.utils;

import android.animation.TypeEvaluator;

/**
 * This evaluator can be used to perform type interpolation between integer
 * values that represent ARGB colors.
 * <p>
 * 2015/03/26 강성우 : ArgbEvaluator가 API 21이상 에서만 동작하기 때문에 따로 구현 하였음.
 */
public class ArgbEvaluatorCompat
    implements TypeEvaluator {
  private static final ArgbEvaluatorCompat sInatance = new ArgbEvaluatorCompat();

  /**
   * Returns an instance of <code>ArgbEvaluator</code> that may be used in
   * {@link android.animation.ValueAnimator#setEvaluator(TypeEvaluator)}. The same instance may
   * be used in multiple <code>Animator</code>s because it holds no state.
   *
   * @return An instance of <code>ArgbEvalutor</code>.
   */
  public static ArgbEvaluatorCompat getsInatance() {
    return sInatance;
  }

  /**
   * This function returns the calculated in-between value for a color
   * given integers that represent the start and end values in the four
   * bytes of the 32-bit int. Each channel is separately linearly interpolated
   * and the resulting calculated values are recombined into the return value.
   *
   * @param fraction   The fraction from the starting to the ending values
   * @param startValue A 32-bit int value representing colors in the
   *                   separate bytes of the parameter
   * @param endValue   A 32-bit int value representing colors in the
   *                   separate bytes of the parameter
   * @return A value that is calculated to be the linearly interpolated
   * result, derived by separating the start and end values into separate
   * color channels and interpolating each one separately, recombining the
   * resulting values in the same way.
   */
  public Object evaluate(float fraction, Object startValue, Object endValue) {
    int startInt = (Integer) startValue;
    int startA = (startInt >> 24) & 0xff;
    int startR = (startInt >> 16) & 0xff;
    int startG = (startInt >> 8) & 0xff;
    int startB = startInt & 0xff;

    int endInt = (Integer) endValue;
    int endA = (endInt >> 24) & 0xff;
    int endR = (endInt >> 16) & 0xff;
    int endG = (endInt >> 8) & 0xff;
    int endB = endInt & 0xff;

    return (startA + (int) (fraction * (endA - startA))) << 24 |
        (startR + (int) (fraction * (endR - startR))) << 16 |
        (startG + (int) (fraction * (endG - startG))) << 8 |
        (startB + (int) (fraction * (endB - startB)));
  }

}
