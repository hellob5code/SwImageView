package kr.swkang.swimageview.utils;

/**
 * @author kangSung-Woo
 * @since 2016/08/04
 */
public enum CornerType {
  NONE(0),
  ALL(1),
  TOP(2),
  BOTTOM(3),
  LEFT(4),
  RIGHT(5),
  TOP_LEFT(6),
  TOP_RIGHT(7),
  BOTTOM_LEFT(8),
  BOTTOM_RIGHT(9),
  EXCEPT_TOP_LEFT(10),
  EXCEPT_TOP_RIGHT(11),
  EXCEPT_BOTTOM_LEFT(12),
  EXCEPT_BOTTOM_RIGHT(13);

  int value;

  CornerType(int v) {
    this.value = v;
  }

  public int getValue() {
    return value;
  }

  public static CornerType parseFromValue(int value) {
    switch (value) {
      case 1:
        return ALL;
      case 2:
        return TOP;
      case 3:
        return BOTTOM;
      case 4:
        return LEFT;
      case 5:
        return RIGHT;
      case 6:
        return TOP_LEFT;
      case 7:
        return TOP_RIGHT;
      case 8:
        return BOTTOM_LEFT;
      case 9:
        return BOTTOM_RIGHT;
      case 10:
        return EXCEPT_TOP_LEFT;
      case 11:
        return EXCEPT_TOP_RIGHT;
      case 12:
        return EXCEPT_BOTTOM_LEFT;
      case 13:
        return EXCEPT_BOTTOM_RIGHT;
      default:
        return NONE;
    }
  }

}
