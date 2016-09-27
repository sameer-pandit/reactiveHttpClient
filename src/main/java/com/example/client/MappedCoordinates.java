package com.example.client;

/**
 * Created by sameerpandit on 9/21/16.
 */
public class MappedCoordinates {
  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  int x;
  int y;

  public MappedCoordinates(int x,int y) {
    this.x = x+100;
    this.y = y+100;
  }

  public MappedCoordinates() {
  }

  @Override
  public String toString() {
    return "MappedCoordinates{" +
            "x=" + x +
            ", y=" + y +
            '}';
  }
}
