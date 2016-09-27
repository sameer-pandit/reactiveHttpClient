package com.example.client;

/**
 * Created by sameerpandit on 9/21/16.
 */

public class Coordinates {
  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  int x;
  int y;

  public Coordinates(int x,int y) {
    this.x = x;
    this.y = y;
  }

  public Coordinates() {

  }

  @Override
  public String toString() {
    return "Coordinates{" +
            "x=" + x +
            ", y=" + y +
            '}';
  }
}
