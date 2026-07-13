package vn.edu.fpt.cinemamanagement.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.edu.fpt.cinemamanagement.enums.SeatStatus;

import java.util.Locale;

@Converter
public class SeatStatusConverter implements AttributeConverter<SeatStatus, String> {

    @Override
    public String convertToDatabaseColumn(SeatStatus status) {
        if (status == null) {
            return null;
        }

        return status.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public SeatStatus convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return SeatStatus.valueOf(
                value.trim().toUpperCase(Locale.ROOT)
        );
    }
}
