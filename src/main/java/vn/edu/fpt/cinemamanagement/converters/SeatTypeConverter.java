package vn.edu.fpt.cinemamanagement.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.edu.fpt.cinemamanagement.enums.SeatType;

import java.util.Locale;

@Converter
public class SeatTypeConverter implements AttributeConverter<SeatType, String>{
    @Override
    public String convertToDatabaseColumn(SeatType seatType) {
        if (seatType == null) {
            return null;
        }

        return seatType.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public SeatType convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return SeatType.valueOf(
                value.trim().toUpperCase(Locale.ROOT)
        );
    }
}
