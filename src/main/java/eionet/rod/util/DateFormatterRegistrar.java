package eionet.rod.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;

/**
 * Default date format
 */
public class DateFormatterRegistrar implements FormatterRegistrar {

    @Value( "${date.format}" )
    private String dateFormat;

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter(dateFormat));
    }
}