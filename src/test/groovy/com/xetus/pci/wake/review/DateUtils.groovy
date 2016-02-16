package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

@CompileStatic
class DateUtils {
  public static Date getDate(int year, int month, int day,
                             int hour, int minute, int second) {
    return new GregorianCalendar(year, month, day, hour, minute, second)
              .getTime()
  }
}
