package vn.edu.fpt.cinemamanagement.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.edu.fpt.cinemamanagement.enums.BookingStatus;

import java.util.Locale;

@Converter
public class BookingStatusConverter
        implements AttributeConverter<BookingStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookingStatus status) {
        if (status == null) {
            return null;
        }

        return status.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public BookingStatus convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return BookingStatus.valueOf(
                value.trim().toUpperCase(Locale.ROOT)
        );
    }
}