package org.coffee.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.dhatim.businesshours.BusinessHours;

@Converter(autoApply = true)
public class BusinessHoursConverter implements AttributeConverter<BusinessHours, String> {
    @Override
    public String convertToDatabaseColumn(BusinessHours businessHours) {
        if(businessHours == null)
            return null;

        return businessHours.toString();
    }

    @Override
    public BusinessHours convertToEntityAttribute(String businessHoursStr) {
        if(businessHoursStr == null || businessHoursStr.trim().isEmpty())
            return null;

        return new BusinessHours(businessHoursStr);
    }
}
