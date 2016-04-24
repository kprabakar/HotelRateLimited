package com.hotel.api.error;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class ApiAccessLimitExceededException extends Throwable {
  public ApiAccessLimitExceededException(String suspensionTime) {
    super("You exceeded API access limit. Please try after " + suspensionTime);
  }
}
