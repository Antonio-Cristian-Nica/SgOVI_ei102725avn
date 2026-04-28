package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.RequestSchedule;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDate;

public class RequestScheduleValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestSchedule.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestSchedule horari = (RequestSchedule) target;

        if (horari.getDate() == null) {
            errors.rejectValue("date", "obligatori", "La data és obligatòria");
        } else if (!horari.getDate().isAfter(LocalDate.now())) {
            errors.rejectValue("date", "passada", "La data ha de ser futura");
        }

        if (horari.getStartHour() == null) {
            errors.rejectValue("startHour", "obligatori", "L'hora d'inici és obligatòria");
        }

        if (horari.getEndHour() == null) {
            errors.rejectValue("endHour", "obligatori", "L'hora de fi és obligatòria");
        } else if (horari.getStartHour() != null &&
                !horari.getEndHour().isAfter(horari.getStartHour())) {
            errors.rejectValue("endHour", "ordre", "L'hora de fi ha de ser posterior a l'hora d'inici");
        }
    }
}
