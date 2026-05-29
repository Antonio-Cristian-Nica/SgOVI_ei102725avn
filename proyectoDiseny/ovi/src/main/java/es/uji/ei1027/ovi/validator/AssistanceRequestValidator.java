package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.AssistanceRequest;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class AssistanceRequestValidator implements Validator {

    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_LONGITUD = "longitud";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return AssistanceRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        AssistanceRequest request = (AssistanceRequest) target;

        if (request.getServiceLocation() == null || request.getServiceLocation().trim().isEmpty()) {
            errors.rejectValue("serviceLocation", ERROR_OBLIGATORI,
                    "La localització del servei és obligatòria");
        } else if (request.getServiceLocation().length() > 200) {
            errors.rejectValue("serviceLocation", ERROR_LONGITUD,
                    "La localització no pot superar els 200 caràcters");
        }

        if (request.getRequiredAssistance() == null || request.getRequiredAssistance().trim().isEmpty()) {
            errors.rejectValue("requiredAssistance", ERROR_OBLIGATORI,
                    "La descripció de l'assistència requerida és obligatòria");
        } else if (request.getRequiredAssistance().length() > 2000) {
            errors.rejectValue("requiredAssistance", ERROR_LONGITUD,
                    "La descripció no pot superar els 2000 caràcters");
        }

        // Validació específica per a sol·licituds flexibles:
        // les dates d'inici i fi del servei són obligatòries, han de ser futures
        // i la data de fi no pot ser anterior a la d'inici.
        if ("flexible".equals(request.getType())) {
            if (request.getStartServiceDate() == null) {
                errors.rejectValue("startServiceDate", ERROR_OBLIGATORI,
                        "La data d'inici del servei és obligatòria");
            } else if (request.getStartServiceDate().isBefore(LocalDate.now())) {
                errors.rejectValue("startServiceDate", "passada",
                        "La data d'inici no pot ser anterior a avui");
            }

            if (request.getEndServiceDate() == null) {
                errors.rejectValue("endServiceDate", ERROR_OBLIGATORI,
                        "La data de fi del servei és obligatòria");
            } else if (request.getStartServiceDate() != null
                    && request.getEndServiceDate().isBefore(request.getStartServiceDate())) {
                errors.rejectValue("endServiceDate", "ordre",
                        "La data de fi ha de ser posterior o igual a la data d'inici");
            }
        }
    }
}
