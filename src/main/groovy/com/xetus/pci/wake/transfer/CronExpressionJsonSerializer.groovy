package com.xetus.pci.wake.transfer

import groovy.transform.CompileStatic

import org.quartz.CronExpression

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class CronExpressionJsonSerializer extends JsonSerializer<CronExpression> {
  @Override
  public void serialize(CronExpression value, JsonGenerator gen,
                        SerializerProvider serializers) throws IOException,
      JsonProcessingException {
    if (value != null) {
      gen.writeString(value.getCronExpression())
    }
  }
}
