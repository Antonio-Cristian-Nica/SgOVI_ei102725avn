package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.RequestSchedule;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class RequestScheduleValidator implements Validator {

    private static final String ERROR_OBLIGATORI = "obligatori";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return RequestSchedule.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        RequestSchedule horari = (RequestSchedule) target;

        if (horari.getDate() == null) {
            errors.rejectValue("date", ERROR_OBLIGATORI, "La data és obligatòria");
        } else if (horari.getDate().isBefore(LocalDate.now())) {
            errors.rejectValue("date", "passada", "La data no pot ser passada");
        }

        if (horari.getStartHour() == null) {
            errors.rejectValue("startHour", ERROR_OBLIGATORI, "L'hora d'inici és obligatòria");
        }

        if (horari.getEndHour() == null) {
            errors.rejectValue("endHour", ERROR_OBLIGATORI, "L'hora de fi és obligatòria");
        } else if (horari.getStartHour() != null &&
                !horari.getEndHour().isAfter(horari.getStartHour())) {
            errors.rejectValue("endHour", "ordre", "L'hora de fi ha de ser posterior a l'hora d'inici");
        }
    }
}
