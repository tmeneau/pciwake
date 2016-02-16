package com.xetus.pci.wake.persistence

import groovy.transform.CompileStatic

import javax.persistence.AttributeConverter
import javax.persistence.Converter

import org.apache.commons.codec.binary.Base64

/**
 * TODO: This is just a placeholder; the logic within both below methods
 * should be updated to use hashing and encryption support
 */
@Converter
@CompileStatic
public class CryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String value) {
      try {
         return Base64.encodeBase64String(value.getBytes("UTF-8"))
      } catch (e) {
         throw new RuntimeException(e);
      }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
      try {
        return new String(Base64.decodeBase64(dbData.getBytes("UTF-8")), "UTF-8")
      } catch (e) {
        throw new RuntimeException(e)
      }
    }
}