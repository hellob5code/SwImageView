package kr.swkang.swimageview.utils;

/**
 * @author KangSung-Woo
 * @since 2016/08/08
 */
public enum ShapeType {
  RECTANGLE(0),
  OVAL(1),
  CIRCLE(2);

  int value;

  ShapeType(int v) {
    this.value = v;
  }

  public int getValue() {
    return value;
  }

  public static ShapeType parseFromValue(int v) {
    switch (v) {
      case 1:
        return OVAL;
      case 2:
        return CIRCLE;
      default:
        return RECTANGLE;
    }
  }
}
