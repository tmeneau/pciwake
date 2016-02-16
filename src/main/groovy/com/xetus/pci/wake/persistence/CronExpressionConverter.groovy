package com.xetus.pci.wake.persistence

import javax.persistence.AttributeConverter
import javax.persistence.Converter

import org.quartz.CronExpression

@Converter
class CronExpressionConverter 
      implements AttributeConverter<CronExpression, String> {

  @Override
  public String convertToDatabaseColumn(CronExpression attribute) {
    return attribute == null ? attribute : attribute.getCronExpression() 
  }

  @Override
  public CronExpression convertToEntityAttribute(String dbData) {
    return dbData == null ? dbData : new CronExpression(dbData)
  }

}